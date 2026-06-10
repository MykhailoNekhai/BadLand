package ua.uni.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import ua.uni.game.MainGame;
import ua.uni.entity.Shadow;
import ua.uni.utilite.BodyEditorLoader;
import ua.uni.utilite.GameContactListener;
import ua.uni.config.GameSettings;


public abstract class Plevel implements Screen {

    // Потрібно для world.step(TIMESTEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS),
    // тут задаєтся точність обрахунків фізики (кількість розрахунків на кадр)
    protected static final float TIMESTEP = 1/35f;
    protected static final int VELOCITY_ITERATIONS = 8;
    protected static final int POSITION_ITERATIONS = 3;

    // Краще не чіпати, через те що розмір тіні впливає на його масу, то при збільшенні об'єкту
    // силу тяги треба змінювати відповідно!!!
    // інакше тінь просто не зможе злетіти!!! (або буде літати моментально, якщо вага буде малою)
    protected static final float SHADOW_SIZE = 1.2f;


    protected Box2DDebugRenderer debugRenderer; // Режим дебагу, необхнідний для розробки
    protected BodyEditorLoader heroLoader;

    protected World world;
    protected OrthographicCamera camera;
    protected MainGame game;


    protected Array<Shadow> clones = new Array<>(); // Клони як масив, оскільки передбачено їх збільшення (як в Badlands
    protected boolean isGameStarted = false; // потрібен для початку руху камери
    protected Viewport viewport;

    protected float cameraSpeed = 3f; // швидкість камери
    protected float finishLineX = 220f; // Фінішна пряма рівня
    private float dynamicTimeStep;

    // Enum станів, потрібен для розуміння, коли користувач натиснув на паузу, коли програв і т.д
    protected enum GameState {
        PLAYING, PAUSED, GAME_OVER, VICTORY
    }


    // Вважаємо старт рівня вже грою
    protected GameState state = GameState.PLAYING;


    // размерность от 0 до 30 метров - оптимизирован box2d

    public Plevel(MainGame game) {
        this.game = game;
    }

    // Перевірка стану гри
    protected void mainGameLogic() {
        if (state != GameState.PLAYING) {
            return;
        }
        float leftCameraEdge = camera.position.x - (camera.viewportWidth / 2f);

        float deathLineX = leftCameraEdge - SHADOW_SIZE;

        for (int i = clones.size - 1; i >= 0; i--) {
            Shadow clone = clones.get(i);
            if (clone.getBody().getPosition().x < deathLineX || clone.isDead()) {
                world.destroyBody(clone.getBody());
                clones.removeIndex(i);
            }
        }


        if (clones.size == 0) {
            state = GameState.GAME_OVER;
            System.out.println("Гра завершена. Програв");
            return;
        }


        float leaderX = -Float.MAX_VALUE;
        for (Shadow clone : clones) {
            float x = clone.getBody().getPosition().x;
            if (x > leaderX) {
                leaderX = x;
            }
        }


        if (leaderX >= finishLineX) {
            state = GameState.VICTORY;
            System.out.println("Гра завершена. Перемога!");
        }
    }


    @Override
    public void show() {
        baseParameters();
        createGround();
        buildLevel();

        // settingControls();
    }


    // метод для створення клонів, наших тіней. В планах зробити подвоювач
    public void spawnClone(float x, float y) {
        Shadow newClone = new Shadow(world, heroLoader, x, y, SHADOW_SIZE);
        clones.add(newClone);
    }

    // метод із базовими налаштуваннями рівнів (усіх)!!!

    private void baseParameters() {
        world = new World(new Vector2(0, -9.81f), true);
        debugRenderer = new Box2DDebugRenderer();
        camera = new OrthographicCamera();
        viewport = new FitViewport(32f, 18f, camera);
        viewport.apply();
        camera.position.set(16f, 9f, 0f);

        heroLoader = new BodyEditorLoader(Gdx.files.internal("game-resourses/assetData/shadow.json"));

        world.setContactListener(new GameContactListener());

        int refreshRate = Gdx.graphics.getDisplayMode().refreshRate;

        if (refreshRate == 0) {
            refreshRate = 60;
        }


        dynamicTimeStep = 4.0f / refreshRate;

    }

    protected abstract void buildLevel();

    // створення землі, простої
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

    // xTarget = xLeader + W/4 - формула місцезнаходження гг відносно камери
    // V = deltaX*коофіцієнт різкості - формула плавного переходу камери, яка рухається відносно швидкості гг


    @Override
    public void render(float delta) {
        // базові налаштування кольору та камери
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        debugRenderer.render(world, camera.combined);

        boolean w = Gdx.input.isKeyPressed(GameSettings.getMoveUp());
        boolean s = Gdx.input.isKeyPressed(GameSettings.getMoveDown());
        boolean a = Gdx.input.isKeyPressed(GameSettings.getMoveLeft());
        boolean d = Gdx.input.isKeyPressed(GameSettings.getMoveRight());



        if (!isGameStarted && (w || s || a || d)) {
            isGameStarted = true;
        }

        if (state == GameState.PLAYING) {
            for (Shadow clone : clones) {
                clone.move(w, s, a, d);
            }

            world.step(dynamicTimeStep, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
        }


        if (isGameStarted && clones.size > 0) {

            float minCameraSpeed = 3f;  // мін швидкість камери (3м/c)


            float leaderX = -Float.MAX_VALUE;
            for (Shadow clone : clones) {
                float x = clone.getBody().getPosition().x;
                if (x > leaderX) {
                    leaderX = x;
                }
            }


            float heroOffset = camera.viewportWidth * 0.3f; // максимальний поріг місцезнаходження гг відносно камери (щоб не був по центру)
            float cameraX = leaderX + heroOffset; // координата, щоб точка камери була правіша за героя


            float smoothness = 4.0f; // коофіцієнт плавності переходу камери

            float neededSpeed = (cameraX - camera.position.x) * smoothness;

            float actualCameraSpeed = Math.max(minCameraSpeed, neededSpeed); // проміжок швидкостей, від мінімального до максимального враховуючи швидкість гг

            camera.position.x = camera.position.x + (actualCameraSpeed * delta);
        }

        camera.position.y = camera.viewportHeight / 2f;
        camera.update();
        mainGameLogic();

    }





// (ЩЕ НЕ ВИРІШИВ)
  /*  private void settingControls(){
        Gdx.input.setInputProcessor(new com.badlogic.gdx.InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                switch(keycode){
                    case Input.Keys.ESCAPE:
                        game.setScreen(new GameScreen(game));
                        break;
                    case Input.Keys.W:
                        movement.y = speedToY;
                        movement.x = speedToX;

                        break;
                    case Input.Keys.D:
                        movement.x = speedToX;
                        break;
                    case Input.Keys.A:
                        movement.x = -speedToX;
                        break;
                }
                return true;
            }
            @Override
            public boolean keyUp(int keycode) {
                switch(keycode){
                    case Input.Keys.W:
                        movement.y = 0;
                        movement.x = 0;
                        break;
                    case Input.Keys.D:
                    case Input.Keys.A:
                        movement.x = 0;
                        break;
                }
                return true;
            }
        });
    }                   */

    // НЕОБХІДНО ДЛЯ ПІДТРИМКИ БУДЬ ЯКОГО РОЗМІРУ ЕКРАНА!!!
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
    }

    // Нюанси рушія, який написаний на c++
    @Override
    public void dispose() {
        world.dispose();
        debugRenderer.dispose();
    }
}
