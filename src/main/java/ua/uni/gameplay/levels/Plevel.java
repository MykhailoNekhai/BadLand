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
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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
import ua.uni.gameplay.ecs.systems.PositionalAudioSystem;
import ua.uni.gameplay.factory.EntityFactory;
import ua.uni.gameplay.physics.GameContactListener;
import ua.uni.presentation.screen.menu.pause.PauseMenu;
import ua.uni.gameplay.stats.LevelStats;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public abstract class Plevel implements Screen {
    protected static final float TIMESTEP = 1 / 35f;
    protected static final int VELOCITY_ITERATIONS = 8;
    protected static final int POSITION_ITERATIONS = 3;
    protected static final float SHADOW_SIZE = 1.2f;

    protected Box2DDebugRenderer debugRenderer;
    protected ShapeRenderer shapeRenderer;
    protected Engine engine;
    protected World world;
    protected OrthographicCamera camera;
    protected MainGame game;

    private Texture backgroundGradient;
    private Texture backgroundGlow;
    private Texture backgroundGlowInner;

    protected boolean isGameStarted = false; // потрібен для початку руху камери
    protected Viewport viewport;

    protected float finishLineX = 2000f; // Фінішна пряма рівня
    protected int levelNumber;

    // Lazy spawning: об'єкти спавняться тільки коли камера наближається
    private static final float SPAWN_LOOKAHEAD = 50f;
    private final List<PendingSpawn> pendingSpawns = new ArrayList<>();

    protected static final class PendingSpawn {
        final String name;
        final float x, y, angle, size, spinSpeed;
        final boolean isSaw;
        PendingSpawn(String n, float x, float y, float a, float s) {
            name=n; this.x=x; this.y=y; angle=a; size=s; spinSpeed=0; isSaw=false;
        }
        PendingSpawn(String n, float x, float y, float a, float s, float sp) {
            name=n; this.x=x; this.y=y; angle=a; size=s; spinSpeed=sp; isSaw=true;
        }
    }

    protected void scheduleObstacle(String name, float x, float y, float angle, float size) {
        pendingSpawns.add(new PendingSpawn(name, x, y, angle, size));
    }

    protected void scheduleSaw(String name, float x, float y, float angle, float size, float spinSpeed) {
        pendingSpawns.add(new PendingSpawn(name, x, y, angle, size, spinSpeed));
    }
    private float dynamicTimeStep;
    private PauseMenu pauseMenu;
    private ComponentMapper<PhysicsComponent> physMapper = ComponentMapper.getFor(PhysicsComponent.class);
    private ComponentMapper<PlayerComponent> playerMapper = ComponentMapper.getFor(PlayerComponent.class);
    private float lastCameraSpeed = 3f;
    public LevelStats levelStats = new LevelStats();

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

        // Lazy spawning: спавнимо об'єкти в міру руху камери
        if (!pendingSpawns.isEmpty()) {
            float spawnEdge = camera.position.x + (camera.viewportWidth / 2f) + SPAWN_LOOKAHEAD;
            for (int i = pendingSpawns.size() - 1; i >= 0; i--) {
                PendingSpawn p = pendingSpawns.get(i);
                if (p.x <= spawnEdge) {
                    if (p.isSaw) EntityFactory.createSaw(engine, world, p.name, p.x, p.y, p.angle, p.size, p.spinSpeed);
                    else         EntityFactory.createObstacle(engine, world, p.name, p.x, p.y, p.angle, p.size);
                    pendingSpawns.remove(i);
                }
            }
        }

        ImmutableArray<Entity> players = engine.getEntitiesFor(Family.all(PlayerComponent.class, PhysicsComponent.class).get());
        float leftCameraEdge = camera.position.x - (camera.viewportWidth / 2f);
        float deathLineX = leftCameraEdge - SHADOW_SIZE;

        for (int i = 0; i < players.size(); ++i) {
            Entity player = players.get(i);
            PhysicsComponent phys = physMapper.get(player);
            PlayerComponent playerComp = playerMapper.get(player);

            if (playerComp.isFinished && phys.body.getPosition().x > finishLineX + SHADOW_SIZE) {
                levelStats.collectedClones++;
                world.destroyBody(phys.body);
                engine.removeEntity(player);
                continue;
            }

            if (phys.body.getPosition().x < deathLineX) {
                AudioManager.get().playSideDeathSound();
                levelStats.deathScore++;
                world.destroyBody(phys.body);
                engine.removeEntity(player);
            } else if (playerComp.isDead) {
                AudioManager.get().playSquishSound();
                levelStats.deathScore++;
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
            if (!levelStats.levelFinished) {
                levelStats.loseScore++;
                state = GameState.GAME_OVER;
                if (levelNumber > 0) {
                    game.getAchievementManager().onDeath(levelNumber);
                }
                AudioManager.get().playLevelLose(0.95f);
                Gdx.app.postRunnable(this::restartLevel);
                System.out.println("Гра завершена. Програв");
                return;
            }
        }

        if (levelStats.levelFinished) {
            float leftCameraEdgeCheck = camera.position.x - (camera.viewportWidth / 2f);
            if (leftCameraEdgeCheck >= finishLineX) {
                levelStats.winScore++;
                state = GameState.VICTORY;
                if (levelNumber > 0) {
                    game.getAchievementManager().onLevelComplete(levelNumber);
                }
                AudioManager.get().playLevelWin(0.95f);
                System.out.println("Гра завершена. Перемога!");

            }
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

    protected void createPortal(float x) {
        this.finishLineX = x;
        Entity portal = new Entity();
        portal.add(new ua.uni.gameplay.ecs.components.FinishComponent());
        
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.StaticBody;
        bdef.position.set(x, 9f);
        Body body = world.createBody(bdef);
        
        FixtureDef fdef = new FixtureDef();
        com.badlogic.gdx.physics.box2d.PolygonShape shape = new com.badlogic.gdx.physics.box2d.PolygonShape();
        shape.setAsBox(0.5f, 18f);
        fdef.shape = shape;
        fdef.isSensor = true;
        body.createFixture(fdef);
        shape.dispose();
        
        body.setUserData(portal);
        
        PhysicsComponent phys = new PhysicsComponent();
        phys.body = body;
        portal.add(phys);
        
        engine.addEntity(portal);

    }

    private void baseParameters() {
        world = new World(new Vector2(0, -9.81f), true);
        debugRenderer = new Box2DDebugRenderer();
        shapeRenderer = new ShapeRenderer();
        camera = new OrthographicCamera();
        viewport = new FitViewport(32f, 18f, camera);
        viewport.apply();
        camera.position.set(16f, 9f, 0f);

        world.setContactListener(new GameContactListener(levelStats));

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
        engine.addSystem(new PositionalAudioSystem(camera));
        pauseMenu = new PauseMenu(game, this::resumeFromPause, this::restartLevel, this::checkpointAction);
        backgroundGradient = makeMultiStopGradient(64, 512, new Color[]{
                new Color(0.03f, 0.03f, 0.03f, 1f),
                new Color(0.07f, 0.06f, 0.05f, 1f),
                new Color(0.13f, 0.12f, 0.10f, 1f),
                new Color(0.19f, 0.17f, 0.14f, 1f),
                new Color(0.24f, 0.21f, 0.17f, 1f),
        });
        backgroundGlow = makeGlowTexture(480,
                new Color(1.0f, 0.78f, 0.25f, 0.34f),
                new Color(0.90f, 0.55f, 0.10f, 0f));
        backgroundGlowInner = makeGlowTexture(220,
                new Color(1.0f, 0.95f, 0.60f, 0.72f),
                new Color(1.0f, 0.80f, 0.30f, 0f));
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
        Gdx.gl.glClearColor(0.03f, 0.03f, 0.03f, 1f);

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

        if (isGameStarted && state == GameState.PLAYING && (players.size() > 0 || levelStats.levelFinished)) {
            float baseMinCameraSpeed = 3f;
            float actualCameraSpeed = lastCameraSpeed;

            if (players.size() > 0) {
                float leaderX = -Float.MAX_VALUE;
                float leaderSpeedModifier = 1.0f;

                for (int i = 0; i < players.size(); ++i) {
                    com.badlogic.ashley.core.Entity player = players.get(i);
                    float x = physMapper.get(player).body.getPosition().x;
                    if (x > leaderX) {
                        leaderX = x;
                        ua.uni.gameplay.ecs.components.PlayerComponent playerComp = playerMapper.get(player);
                        if (playerComp != null) {
                            leaderSpeedModifier = playerComp.speedModifier;
                        }
                    }
                }
                float minCameraSpeed = baseMinCameraSpeed * leaderSpeedModifier;

                float heroOffset = camera.viewportWidth * 0.3f;
                float cameraX = leaderX + heroOffset;
                float smoothness = 4.0f;
                float neededSpeed = (cameraX - camera.position.x) * smoothness;
                actualCameraSpeed = Math.max(minCameraSpeed, neededSpeed);
                lastCameraSpeed = actualCameraSpeed;
            }

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

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 1);
        shapeRenderer.rect(finishLineX, -5f, 100f, 30f);
        shapeRenderer.end();

        // SdebugRenderer.render(world, camera.combined);
    }

    private void renderLevelBackground() {
        float vw = camera.viewportWidth;
        float vh = camera.viewportHeight;
        float left = camera.position.x - (vw / 2f);
        float bottom = camera.position.y - (vh / 2f);

        float glowCx = camera.position.x + (vw * 0.22f);
        float glowCy = camera.position.y + (vh * 0.10f);

        float outerW = vw * 1.45f;
        float outerH = vh * 1.65f;
        float innerW = vw * 0.65f;
        float innerH = vh * 0.75f;

        game.getBatch().begin();
        game.getBatch().setColor(Color.WHITE);
        game.getBatch().draw(backgroundGradient, left, bottom, vw, vh);
        game.getBatch().setColor(1f, 1f, 1f, 1f);
        game.getBatch().draw(backgroundGlow,
                glowCx - outerW / 2f, glowCy - outerH / 2f, outerW, outerH);
        game.getBatch().draw(backgroundGlowInner,
                glowCx - innerW / 2f, glowCy - innerH / 2f, innerW, innerH);
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
        if (engine != null) {
            engine.removeAllEntities();
        }
    }

    @Override
    public void dispose() {
        AudioManager.get().stopLevelMusic();
        if (engine != null) {
            engine.removeAllEntities();
        }
        if (pauseMenu != null) {
            pauseMenu.dispose();
        }
        if (backgroundGradient != null) backgroundGradient.dispose();
        if (backgroundGlow != null) backgroundGlow.dispose();
        if (backgroundGlowInner != null) backgroundGlowInner.dispose();
        if (world != null) {
            world.dispose();
        }
        if (debugRenderer != null) {
            debugRenderer.dispose();
        }
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
    }

    private Texture makeMultiStopGradient(int width, int height, Color[] stops) {
        // stops[0] = bottom, stops[last] = top
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        int n = stops.length;
        for (int y = 0; y < height; y++) {
            float t = y / (float) (height - 1);
            float segF = t * (n - 1);
            int seg = Math.min((int) segF, n - 2);
            float lt = segF - seg;
            Color c0 = stops[seg], c1 = stops[seg + 1];
            pixmap.setColor(
                    c0.r + (c1.r - c0.r) * lt,
                    c0.g + (c1.g - c0.g) * lt,
                    c0.b + (c1.b - c0.b) * lt,
                    1f);
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
