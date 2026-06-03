package ua.uni.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import ua.uni.MainGame;
import ua.uni.entity.Shadow;

public abstract class Plevel implements Screen {

    private static final float TIMESTEP = 1/60f;
    private static final int VELOCITY_ITERATIONS = 8;
    private static final int POSITION_ITERATIONS = 3;

    private Box2DDebugRenderer debugRenderer;
    protected World world;
    protected OrthographicCamera camera;
    protected MainGame game;
    protected Array<Shadow> clones = new Array<>();

    // размерность от 0 до 30 метров - оптимизирован box2d

    public Plevel(MainGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        baseParameters();
        buildLevel();
        // settingControls();
    }

    public void spawnClone(float x, float y) {
        Shadow newClone = new Shadow(world, x, y);
        clones.add(newClone);
    }

    private void baseParameters() {
        world = new World(new Vector2(0, -9.8f), true);
        debugRenderer = new Box2DDebugRenderer();
        camera = new OrthographicCamera(Gdx.graphics.getWidth() / 40f, Gdx.graphics.getHeight() / 40f);
    }

    protected abstract void buildLevel();

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        debugRenderer.render(world, camera.combined);

            boolean w = Gdx.input.isKeyPressed(Input.Keys.W);
            boolean s = Gdx.input.isKeyPressed(Input.Keys.S);
            boolean a = Gdx.input.isKeyPressed(Input.Keys.A);
            boolean d = Gdx.input.isKeyPressed(Input.Keys.D);

            for (Shadow clone : clones) {
                clone.move(w, s, a, d);
            }

        world.step(TIMESTEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);

        updateCameraToFollowSwarm();
        camera.update();



    }

    private void updateCameraToFollowSwarm() {
        if (clones.size == 0) return;

        float sumX = 0;
        float sumY = 0;

        for (Shadow clone : clones) {
            sumX += clone.getBody().getPosition().x;
            sumY += clone.getBody().getPosition().y;
        }

        float averageX = sumX / clones.size;
        float averageY = sumY / clones.size;

        camera.position.set(averageX, averageY, 0);
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

    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = Gdx.graphics.getWidth() / 40f;
        camera.viewportHeight = Gdx.graphics.getHeight() / 40f;
        camera.update();
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

    @Override
    public void dispose() {
    world.dispose();
    debugRenderer.dispose();
    }
}
