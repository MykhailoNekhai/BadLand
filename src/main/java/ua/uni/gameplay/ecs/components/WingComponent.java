package ua.uni.gameplay.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class WingComponent implements Component {
    private static final int WING_FRAME_COUNT = 5;
    private static final int WING_MID_FRAME = 2;

    public Texture wingSpritesheet;
    public TextureRegion[] wingFrames;
    
    public float currentYOffset = 0f;
    public float flapTime = 0f;
    public int currentFrame = WING_MID_FRAME;
    public boolean isVisible = false;

    public WingComponent() {
        wingSpritesheet = new Texture(Gdx.files.internal(
                "game-resourses/textures/avatar-wings/animation/wing_spritesheet_5frames.png"));
        int frameWidth = wingSpritesheet.getWidth() / WING_FRAME_COUNT;
        TextureRegion[][] splitFrames = TextureRegion.split(wingSpritesheet, frameWidth, wingSpritesheet.getHeight());
        wingFrames = new TextureRegion[WING_FRAME_COUNT];
        for (int i = 0; i < WING_FRAME_COUNT; i++) {
            wingFrames[i] = splitFrames[0][i];
        }
    }

    public TextureRegion currentRegion() {
        if (wingFrames == null || wingFrames.length == 0) {
            return null;
        }
        int frame = Math.max(0, Math.min(currentFrame, wingFrames.length - 1));
        return wingFrames[frame];
    }
}
