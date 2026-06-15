package ua.uni.gameplay.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Pool;

public class SoundSourceComponent implements Component, Pool.Poolable {
    public Sound sound;
    public long soundId = -1;
    public float maxDistance = 16f;
    public boolean isPlaying = false;
    public float baseVolume = 1.0f;

    @Override
    public void reset() {
        if (sound != null && soundId != -1) {
            sound.stop(soundId);
        }
        sound = null;
        soundId = -1;
        maxDistance = 16f;
        isPlaying = false;
        baseVolume = 1.0f;
    }
}
