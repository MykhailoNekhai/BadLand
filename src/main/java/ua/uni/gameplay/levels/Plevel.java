package ua.uni.gameplay.levels;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import ua.uni.gameplay.ecs.components.PhysicsComponent;
import ua.uni.gameplay.ecs.components.PlayerComponent;
import ua.uni.audio.services.AudioManager;
import ua.uni.core.config.GameSettings;
import ua.uni.bootstrap.MainGame;
import ua.uni.gameplay.ecs.systems.PhysicsSystem;
import ua.uni.gameplay.ecs.systems.PullSystem;
import ua.uni.gameplay.ecs.systems.RenderSystem;
import ua.uni.gameplay.ecs.systems.ShadowSystem;
import ua.uni.gameplay.ecs.systems.BonusSystem;
import ua.uni.gameplay.factory.EntityFactory;
import ua.uni.gameplay.physics.GameContactListener;
import ua.uni.presentation.screen.menu.pause.PauseMenu;

import java.lang.reflect.Constructor;

public abstract class Plevel implements Screen {
    protected static final float TIMESTEP = 1 / 15f;
    protected static final int VELOCITY_ITERATIONS = 8;
    protected static final int POSITION_ITERATIONS = 3;
    protected static final float SHADOW_SIZE = 1.2f;

    protected Box2DDebugRenderer debugRenderer;
    protected Engine engine;
    protected World world;
    protected OrthographicCamera camera;
    protected MainGame game;

    private Texture backgroundGradient;
    private Texture backgroundGlow;

    protected boolean isGameStarted = false; // потрібен для початку руху камери
    protected Viewport viewport;

    protected float cameraSpeed = 3f; // швидкість камери
    protected float finishLineX = 2000f; // Фінішна пряма рівня
    protected int levelNumber;
    private float dynamicTimeStep;
    private PauseMenu pauseMenu;
    private ComponentMapper<PhysicsComponent> physMapper = ComponentMapper.getFor(PhysicsComponent.class);
    private ComponentMapper<PlayerComponent> playerMapper = ComponentMapper.getFor(PlayerComponent.class);

    protected enum GameState {
        PLAYING, PAUSED, GAME_OVER, VICTORY
    }

    protected GameState state = GameState.PLAYING;

    public Plevel(MainGame game) {
        this.game = game;
    }

    protected void mainGameLogic() {
        if (state != GameState.PLAYING) {
            return;
        }

        ImmutableArray<Entity> players = engine.getEntitiesFor(Family.all(PlayerComponent.class, PhysicsComponent.class).get());
        float leftCameraEdge = camera.position.x - (camera.viewportWidth / 2f);
        float deathLineX = leftCameraEdge - SHADOW_SIZE;

        for (int i = 0; i < players.size(); ++i) {
            Entity player = players.get(i);
            PhysicsComponent phys = physMapper.get(player);
            PlayerComponent playerComp = playerMapper.get(player);

            if (phys.body.getPosition().x < deathLineX || playerComp.isDead) {
                world.destroyBody(phys.body);
                engine.removeEntity(player);
            }
        }

        // тут шматок коду перевіряє чи далеко від нас об'єкт позаду та видаляє задля продуктивності
        float edgeX = leftCameraEdge - 25f;
        ImmutableArray<Entity> obstacles = engine.getEntitiesFor(Family.all(PhysicsComponent.class).exclude(PlayerComponent.class).get());
        for (int i = 0; i < obstacles.size(); ++i) {
            Entity obstacle = obstacles.get(i);
            PhysicsComponent phys = physMapper.get(obstacle);
            if (phys != null && phys.body != null) {
                if (phys.body.getPosition().x < edgeX) {
                    world.destroyBody(phys.body);
                    engine.removeEntity(obstacle);
                }
            }
        }

        players = engine.getEntitiesFor(Family.all(PlayerComponent.class, PhysicsComponent.class).get());

        if (players.size() == 0) {
            state = GameState.GAME_OVER;
            if (levelNumber > 0) {
                game.getAchievementManager().onLevelFailed();
                game.getAchievementManager().onDeath();
            }
            AudioManager.get().playLevelLose(0.95f);
            Gdx.app.postRunnable(this::restartLevel);
            System.out.println("Гра завершена. Програв");
            return;
        }

        float leaderX = -Float.MAX_VALUE;
        for (int i = 0; i < players.size(); ++i) {
            Entity player = players.get(i);

            PhysicsComponent phys = physMapper.get(player);

            float x = phys.body.getPosition().x;
            if (x > leaderX) {
                leaderX = x;
            }
        }

        if (leaderX >= finishLineX) {
            state = GameState.VICTORY;
            if (levelNumber > 0) {
                game.getAchievementManager().onLevelComplete(levelNumber);
            }
            AudioManager.get().playLevelWin(0.95f);
            System.out.println("Гра завершена. Перемога!");
        }
    }

    @Override
    public void show() {
        baseParameters();
        createGround();
        buildLevel();
        if (levelNumber > 0) {
            game.getAchievementManager().onLevelStart(levelNumber);
        }
        AudioManager.get().startLevelMusic();
    }

    public void spawnClone(float x, float y) {
        EntityFactory.createPlayer(engine, world, x, y, SHADOW_SIZE);
    }

    private void baseParameters() {
        world = new World(new Vector2(0, -9.81f), true);
        debugRenderer = new Box2DDebugRenderer();
        camera = new OrthographicCamera();
        viewport = new FitViewport(32f, 18f, camera);
        viewport.apply();
        camera.position.set(16f, 9f, 0f);

        world.setContactListener(new GameContactListener());

        int refreshRate = Gdx.graphics.getDisplayMode().refreshRate;
        System.out.println("Refresh rate: " + refreshRate);
        if (refreshRate == 0) {
            System.out.println("Refresh rate is 0, setting to 60");
            refreshRate = 60;
        }

        dynamicTimeStep = 4.0f / refreshRate;
        System.out.println("Dynamic time step: " + dynamicTimeStep);

        engine = new PooledEngine();
        engine.addSystem(new PhysicsSystem());
        engine.addSystem(new ShadowSystem(world));
        engine.addSystem(new RenderSystem(game.getBatch()));
        engine.addSystem(new PullSystem());
        engine.addSystem(new BonusSystem(world));
        pauseMenu = new PauseMenu(game, this::resumeFromPause, this::restartLevel, this::checkpointAction);
        backgroundGradient = makeLevelGradientTexture(64, 256,
                new Color(0.18f, 0.24f, 0.32f, 1f),
                new Color(0.04f, 0.05f, 0.08f, 1f));
        backgroundGlow = makeGlowTexture(420,
                new Color(0.88f, 0.76f, 0.46f, 0.30f),
                new Color(0.88f, 0.76f, 0.46f, 0f));
    }

    protected abstract void buildLevel();

    private void createGround() {


        BodyDef roofBody = new BodyDef();
        roofBody.type = BodyDef.BodyType.StaticBody;
        roofBody.position.set(0, 0);

        FixtureDef roofFix = new FixtureDef();
        roofFix.density = 0f;
        roofFix.friction = 0.2f;
        roofFix.restitution = 0.1f;

        ChainShape floorShape = new ChainShape();
        floorShape.createChain(new Vector2[]{new Vector2(-500, 0), new Vector2(500, 0)});
        roofFix.shape = floorShape;
        Body floorBody = world.createBody(roofBody);
        floorBody.createFixture(roofFix);
        floorShape.dispose();

        ChainShape ceilShape = new ChainShape();
        ceilShape.createChain(new Vector2[]{new Vector2(-500, 18), new Vector2(500, 18)});
        roofFix.shape = ceilShape;
        Body ceilBody = world.createBody(roofBody);
        ceilBody.createFixture(roofFix);
        ceilShape.dispose();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.04f, 0.05f, 0.08f, 1f);
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (state == GameState.PLAYING) {
                state = GameState.PAUSED;
                AudioManager.get().pauseLevelMusic();
                pauseMenu.show();
            } else if (state == GameState.PAUSED) {
                pauseMenu.handleEscape();
            }
        }

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (!isGameStarted) {
            boolean w = Gdx.input.isKeyPressed(GameSettings.getMoveUp());
            boolean s = Gdx.input.isKeyPressed(GameSettings.getMoveDown());
            boolean a = Gdx.input.isKeyPressed(GameSettings.getMoveLeft());
            boolean d = Gdx.input.isKeyPressed(GameSettings.getMoveRight());

            if (state == GameState.PLAYING && !isGameStarted && (w || s || a || d)) {
                isGameStarted = true;
            }
        }
/*
        if (state == GameState.PLAYING) {
            AudioManager.get().updateLevelAmbience(delta);
            for (Shadow clone : clones) {
                clone.move(w, s, a, d);
            }
        }
*/
        ImmutableArray<Entity> players = engine.getEntitiesFor(Family.all(PlayerComponent.class, PhysicsComponent.class).get());

        if (isGameStarted && state == GameState.PLAYING && players.size() > 0) {
            float minCameraSpeed = 3f;
            float leaderX = -Float.MAX_VALUE;

            for (int i = 0; i < players.size(); ++i) {
                    float x = physMapper.get(players.get(i)).body.getPosition().x;
                    if (x > leaderX) {
                        leaderX = x;
                    }
                }

            float heroOffset = camera.viewportWidth * 0.3f;
            float cameraX = leaderX + heroOffset;
            float smoothness = 4.0f;
            float neededSpeed = (cameraX - camera.position.x) * smoothness;
            float actualCameraSpeed = Math.max(minCameraSpeed, neededSpeed);

            camera.position.x = camera.position.x + (actualCameraSpeed * delta);
        }

        camera.position.y = camera.viewportHeight / 2f;
        camera.update();

        game.getBatch().setProjectionMatrix(camera.combined);
        renderLevelBackground();

        if (state == GameState.PLAYING) {
            AudioManager.get().updateLevelAmbience(delta);
            world.step(dynamicTimeStep, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
            engine.update(delta);
            mainGameLogic();
        } else if (state == GameState.PAUSED) {
            engine.getSystem(RenderSystem.class).update(delta);
            pauseMenu.render(delta);
        } else {
            engine.getSystem(RenderSystem.class).update(delta);
        }

        debugRenderer.render(world, camera.combined);
    }

    private void renderLevelBackground() {
        float viewportWidth = camera.viewportWidth;
        float viewportHeight = camera.viewportHeight;
        float left = camera.position.x - (viewportWidth / 2f);
        float bottom = camera.position.y - (viewportHeight / 2f);

        float glowWidth = viewportWidth * 1.35f;
        float glowHeight = viewportHeight * 1.55f;
        float glowX = camera.position.x + (viewportWidth * 0.22f) - (glowWidth / 2f);
        float glowY = camera.position.y + (viewportHeight * 0.10f) - (glowHeight / 2f);

        game.getBatch().begin();
        game.getBatch().setColor(Color.WHITE);
        game.getBatch().draw(backgroundGradient, left, bottom, viewportWidth, viewportHeight);
        game.getBatch().setColor(1f, 1f, 1f, 0.92f);
        game.getBatch().draw(backgroundGlow, glowX, glowY, glowWidth, glowHeight);
        game.getBatch().setColor(1f, 1f, 1f, 1f);
        game.getBatch().end();
    }

    private void resumeFromPause() {
        state = GameState.PLAYING;
        pauseMenu.hide();
        AudioManager.get().resumeLevelMusic();
        Gdx.input.setInputProcessor(null);
    }

    private void restartLevel() {
        pauseMenu.hide();
        AudioManager.get().stopLevelMusic();
        Gdx.input.setInputProcessor(null);
        try {
            Constructor<? extends Plevel> constructor = getClass().getConstructor(MainGame.class);
            game.setScreen(constructor.newInstance(game));
        } catch (Exception e) {
            throw new RuntimeException("Failed to restart level: " + getClass().getSimpleName(), e);
        }
    }

    private void checkpointAction() {
        // Placeholder until checkpoint system exists.
    }


    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        if (pauseMenu != null) {
            pauseMenu.resize(width, height);
        }
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {
        AudioManager.get().stopLevelMusic();
    }

    @Override
    public void dispose() {
        AudioManager.get().stopLevelMusic();
        if (pauseMenu != null) {
            pauseMenu.dispose();
        }
        if (backgroundGradient != null) {
            backgroundGradient.dispose();
        }
        if (backgroundGlow != null) {
            backgroundGlow.dispose();
        }
        world.dispose();
        debugRenderer.dispose();
    }

    private Texture makeLevelGradientTexture(int width, int height, Color topColor, Color bottomColor) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        for (int y = 0; y < height; y++) {
            float t = y / (float) (height - 1);
            float r = bottomColor.r + ((topColor.r - bottomColor.r) * t);
            float g = bottomColor.g + ((topColor.g - bottomColor.g) * t);
            float b = bottomColor.b + ((topColor.b - bottomColor.b) * t);
            float a = bottomColor.a + ((topColor.a - bottomColor.a) * t);
            pixmap.setColor(r, g, b, a);
            pixmap.drawLine(0, y, width, y);
        }
        Texture texture = new Texture(pixmap);
        texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        pixmap.dispose();
        return texture;
    }

    private Texture makeGlowTexture(int size, Color centerColor, Color edgeColor) {
        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        float center = size / 2f;
        float radius = size / 2f;
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                float dx = x - center;
                float dy = y - center;
                float distance = (float) Math.sqrt((dx * dx) + (dy * dy));
                float alpha = Math.max(0f, 1f - (distance / radius));
                alpha = alpha * alpha;
                float r = edgeColor.r + ((centerColor.r - edgeColor.r) * alpha);
                float g = edgeColor.g + ((centerColor.g - edgeColor.g) * alpha);
                float b = edgeColor.b + ((centerColor.b - edgeColor.b) * alpha);
                float a = edgeColor.a + ((centerColor.a - edgeColor.a) * alpha);
                pixmap.setColor(r, g, b, a);
                pixmap.drawPixel(x, y);
            }
        }
        Texture texture = new Texture(pixmap);
        texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        pixmap.dispose();
        return texture;
    }
}
