package ua.uni.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.audio.Sound;

public class SoundComponent implements Component {
    public Sound collisionSound;
    public float volume = 1.0f;
}