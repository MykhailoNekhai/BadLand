package ua.uni.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import ua.uni.config.GameSettings;

public final class AudioManager {
    private static AudioManager INSTANCE;

    private Music menuMusic;
    private Sound uiHover;
    private Sound uiSelect;
    private boolean menuMusicWanted;

    private AudioManager() {
        menuMusic = Gdx.audio.newMusic(Gdx.files.internal("game-resourses/audio/menu_music.mp3"));
        menuMusic.setLooping(true);
        uiHover = Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/ui_hover.wav"));
        uiSelect = Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/ui_select.wav"));
    }

    public static AudioManager get() {
        if (INSTANCE == null) INSTANCE = new AudioManager();
        return INSTANCE;
    }

    public void playMenuMusic() {
        menuMusicWanted = true;
        applySoundSetting();
    }

    public void stopMenuMusic() {
        menuMusicWanted = false;
        menuMusic.stop();
    }

    public void applySoundSetting() {
        float v = GameSettings.getMusicVolume();
        menuMusic.setVolume(v);
        if (menuMusicWanted && v > 0f) {
            if (!menuMusic.isPlaying()) menuMusic.play();
        } else {
            if (menuMusic.isPlaying()) menuMusic.pause();
        }
    }

    public void playHover() {
        float v = GameSettings.getMusicVolume();
        if (v > 0f) uiHover.play(0.35f * v);
    }

    public void playSelect(float volume) {
        float v = GameSettings.getMusicVolume();
        if (v > 0f) uiSelect.play(volume * v);
    }

    public void dispose() {
        if (menuMusic != null) menuMusic.dispose();
        if (uiHover != null) uiHover.dispose();
        if (uiSelect != null) uiSelect.dispose();
        INSTANCE = null;
    }
}
