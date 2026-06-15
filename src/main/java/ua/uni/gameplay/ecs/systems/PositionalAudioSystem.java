package ua.uni.gameplay.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import ua.uni.gameplay.ecs.components.PhysicsComponent;
import ua.uni.gameplay.ecs.components.SoundSourceComponent;
import ua.uni.core.config.GameSettings;

public class PositionalAudioSystem extends IteratingSystem {
    private final ComponentMapper<PhysicsComponent> physMapper = ComponentMapper.getFor(PhysicsComponent.class);
    private final ComponentMapper<SoundSourceComponent> audioMapper = ComponentMapper.getFor(SoundSourceComponent.class);
    private final OrthographicCamera camera;

    public PositionalAudioSystem(OrthographicCamera camera) {
        super(Family.all(PhysicsComponent.class, SoundSourceComponent.class).get());
        this.camera = camera;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PhysicsComponent phys = physMapper.get(entity);
        SoundSourceComponent audio = audioMapper.get(entity);

        if (phys.body == null || audio.sound == null) {
            return;
        }

        Vector2 position = phys.body.getPosition();
        float distance = Vector2.dst(camera.position.x, camera.position.y, position.x, position.y);
        
        float masterVolume = GameSettings.getMusicVolume();

        if (masterVolume <= 0f) {
            if (audio.isPlaying) {
                audio.sound.setVolume(audio.soundId, 0f);
            }
            return;
        }

        if (distance < audio.maxDistance) {
            float volumeRatio = 1.0f - (distance / audio.maxDistance);
            if (volumeRatio < 0f) volumeRatio = 0f;
            
            volumeRatio = volumeRatio * volumeRatio;
            
            float finalVolume = volumeRatio * audio.baseVolume * masterVolume;

            if (!audio.isPlaying) {
                audio.soundId = audio.sound.loop(finalVolume);
                audio.isPlaying = true;
            } else {
                audio.sound.setVolume(audio.soundId, finalVolume);
            }
        } else {
            if (audio.isPlaying) {
                audio.sound.setVolume(audio.soundId, 0f);
            }
        }
    }
}
