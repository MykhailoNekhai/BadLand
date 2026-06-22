package ua.uni.audio.music;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

public class PMusic {
    private final Music music;
    private final float volumeMultiplier;
    private boolean wanted;

    protected PMusic(String internalPath) {
        this(internalPath, 1f, true);
    }

    protected PMusic(String internalPath, float volumeMultiplier, boolean looping) {
        this.music = Gdx.audio.newMusic(Gdx.files.internal(internalPath));
        this.volumeMultiplier = volumeMultiplier;
        this.music.setLooping(looping);
    }

    public void play(float masterVolume) {
        wanted = true;
        applyVolume(masterVolume);
    }

    public void resume(float masterVolume) {
        play(masterVolume);
    }

    public void pause() {
        wanted = false;
        if (music.isPlaying()) {
            music.pause();
        }
    }

    public void stop() {
        wanted = false;
        music.stop();
    }

    public void setVolume(float masterVolume) {
        music.setVolume(masterVolume * volumeMultiplier);
    }

    public void applyVolume(float masterVolume) {
        setVolume(masterVolume);
        if (wanted && masterVolume > 0f) {
            if (!music.isPlaying()) {
                music.play();
            }
        } else if (music.isPlaying()) {
            music.pause();
        }
    }

    public void setLooping(boolean looping) {
        music.setLooping(looping);
    }

    public boolean isPlaying() {
        return music.isPlaying();
    }

    public boolean isWanted() {
        return wanted;
    }

    public void dispose() {
        music.dispose();
    }
}
