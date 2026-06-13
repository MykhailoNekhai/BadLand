package ua.uni.levels;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
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
import ua.uni.components.PhysicsComponent;
import ua.uni.components.PlayerComponent;
import ua.uni.entity.Barbwire;
import ua.uni.audio.services.AudioManager;
import ua.uni.config.GameSettings;
import ua.uni.entity.Shadow;
import ua.uni.game.MainGame;
import ua.uni.entity.Shadow;
import ua.uni.systems.PhysicsSystem;
import ua.uni.systems.RenderSystem;
import ua.uni.systems.ShadowSystem;
import ua.uni.utilite.BodyEditorLoader;
import ua.uni.utilite.EntityFactory;
import ua.uni.utilite.GameContactListener;
import ua.uni.web.main_menu.pause_menu.PauseMenu;

import java.lang.reflect.Constructor;

public abstract class Plevel implements Screen {
    protected static final float TIMESTEP = 1 / 35f;
    protected static final int VELOCITY_ITERATIONS = 8;
    protected static final int POSITION_ITERATIONS = 3;
    protected static final float SHADOW_SIZE = 1.2f;

    protected Box2DDebugRenderer debugRenderer;
    protected Engine engine;
    protected World world;
    protected OrthographicCamera camera;
    protected MainGame game;


    protected boolean isGameStarted = false; // потрібен для початку руху камери
    protected Viewport viewport;
    protected float cameraSpeed = 3f;
    protected float finishLineX = 1000f;

    protected float cameraSpeed = 3f; // швидкість камери
    protected float finishLineX = 2000f; // Фінішна пряма рівня
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

        players = engine.getEntitiesFor(Family.all(PlayerComponent.class, PhysicsComponent.class).get());

        if (players.size() == 0) {
            state = GameState.GAME_OVER;
            AudioManager.get().playLevelLose(0.95f);
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
            AudioManager.get().playLevelWin(0.95f);
            System.out.println("Гра завершена. Перемога!");
        }
    }

    @Override
    public void show() {
        baseParameters();
        createGround();
        buildLevel();
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
        if (refreshRate == 60) {
            System.out.println("Refresh rate is 0, setting to 60");
            refreshRate = 240;
        }

        dynamicTimeStep = 4.0f / refreshRate;
        System.out.println("Dynamic time step: " + dynamicTimeStep);

        engine = new PooledEngine();
        engine.addSystem(new PhysicsSystem());
        engine.addSystem(new ShadowSystem());
        engine.addSystem(new RenderSystem(game.getBatch()));
        pauseMenu = new PauseMenu(game, this::resumeFromPause, this::restartLevel, this::checkpointAction);
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
        Gdx.gl.glClearColor(0.5f, 0.8f, 1f, 1);
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (state == GameState.PLAYING) {
                state = GameState.PAUSED;
                AudioManager.get().pauseLevelMusic();
                pauseMenu.show();
            } else if (state == GameState.PAUSED) {
                pauseMenu.handleEscape();
            }
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (!isGameStarted) {
            boolean w = Gdx.input.isKeyPressed(GameSettings.getMoveUp());
            boolean s = Gdx.input.isKeyPressed(GameSettings.getMoveDown());
            boolean a = Gdx.input.isKeyPressed(GameSettings.getMoveLeft());
            boolean d = Gdx.input.isKeyPressed(GameSettings.getMoveRight());

        if (state == GameState.PLAYING && !isGameStarted && (w || s || a || d)) {
            isGameStarted = true;
        }

        if (state == GameState.PLAYING) {
            AudioManager.get().updateLevelAmbience(delta);
            for (Shadow clone : clones) {
                clone.move(w, s, a, d);
            }
        }

        ImmutableArray<Entity> players = engine.getEntitiesFor(Family.all(PlayerComponent.class, PhysicsComponent.class).get());

        if (isGameStarted && players.size() > 0) {
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

        if (state == GameState.PLAYING) {
            world.step(dynamicTimeStep, VELOCITY_ITERATIONS, POSITION_ITERATIONS);

            engine.update(delta);
        } else {
            engine.getSystem(RenderSystem.class).update(delta);
        }


        mainGameLogic();
            camera.position.y = camera.viewportHeight / 2f;
            camera.update();
            mainGameLogic();
        } else {
            camera.position.y = camera.viewportHeight / 2f;
            camera.update();
            pauseMenu.render(delta);
        }
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
        world.dispose();
        debugRenderer.dispose();
    }
}
