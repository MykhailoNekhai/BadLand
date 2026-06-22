package ua.uni.gameplay.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Pool;

public class EyeComponent implements Component, Pool.Poolable {
    public static final int OPEN_FRAME = 0;
    public static final int HALF_CLOSED_FRAME = 1;
    public static final int CLOSED_FRAME = 2;

    public Texture texture;
    public TextureRegion[] frames;
    public int currentFrame = OPEN_FRAME;
    public float blinkTimer = 0f;
    public float nextBlinkTime = 2.5f;
    public boolean isBlinking = false;

    // Offset is relative to the avatar body size: 0.1 means 10% of body width/height.
    public float offsetX = 0f;
    public float offsetY = 0f;
    public float scale = 1f;

    public void loadSpritesheet(String path, int frameCount) {
        texture = new Texture(Gdx.files.internal(path));
        texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);

        int frameWidth = texture.getWidth() / frameCount;
        int frameHeight = texture.getHeight();
        TextureRegion[][] splitFrames = TextureRegion.split(texture, frameWidth, frameHeight);
        frames = new TextureRegion[frameCount];
        for (int i = 0; i < frameCount; i++) {
            frames[i] = splitFrames[0][i];
        }
    }

    public TextureRegion currentRegion() {
        if (frames == null || frames.length == 0) {
            return null;
        }
        int frame = Math.max(0, Math.min(currentFrame, frames.length - 1));
        return frames[frame];
    }


    @Override
    public void reset() {
        texture = null;
        frames = null;
        currentFrame = OPEN_FRAME;
        blinkTimer = 0f;
        nextBlinkTime = 2.5f;
        isBlinking = false;
        offsetX = 0f;
        offsetY = 0f;
        scale = 1f;
    }
}
