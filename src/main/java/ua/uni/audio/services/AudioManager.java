package ua.uni.audio.services;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import ua.uni.config.GameSettings;

public final class AudioManager {
    private static AudioManager INSTANCE;

    private Music menuMusic;
    private Sound uiHover;
    private Sound uiSelect;
    private Sound uiStart;
    private Sound uiPanelInOut;
    private Sound uiLevelSelect;
    private Sound achievementNotice;
    private Sound achievementWinner;
    private Sound[] menuAmbience;
    private boolean menuMusicWanted;

    private AudioManager() {
        menuMusic = Gdx.audio.newMusic(Gdx.files.internal("game-resourses/audio/menu_music.mp3"));
        menuMusic.setLooping(true);
        uiHover = Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/extracted/BadlandBank/69-Hover.wav"));
        uiSelect = Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/ui_select.wav"));
        uiStart = Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/extracted/BadlandBank/76-Start.wav"));
        uiPanelInOut = Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/extracted/BadlandBank/75-MenuInOuts.wav"));
        uiLevelSelect = Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/extracted/BadlandBank/73-MP4select.wav"));
        achievementNotice = Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/extracted/BadlandBank/46-achievement notice.wav"));
        achievementWinner = Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/extracted/BadlandBank/51-winner.wav"));
        menuAmbience = new Sound[] {
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/extracted/DawnDayBank/27-smaller birds.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/extracted/DawnDayBank/12-distant frogs.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/extracted/DawnDayBank/14-morningbird.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/extracted/DawnDayBank/5-birdie.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/extracted/DawnDayBank/6-birdie2.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/extracted/DawnDayBank/8-blackbird1.wav"))
        };
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

    public void playStart(float volume) {
        float v = GameSettings.getMusicVolume();
        if (v > 0f) uiStart.play(volume * v);
    }

    public void playPanelInOut(float volume) {
        float v = GameSettings.getMusicVolume();
        if (v > 0f) uiPanelInOut.play(volume * v);
    }

    public void playLevelSelect(float volume) {
        float v = GameSettings.getMusicVolume();
        if (v > 0f) uiLevelSelect.play(volume * v);
    }

    public void playAchievementNotice(float volume) {
        float v = GameSettings.getMusicVolume();
        if (v > 0f) achievementNotice.play(volume * v);
    }

    public void playAchievementWinner(float volume) {
        float v = GameSettings.getMusicVolume();
        if (v > 0f) achievementWinner.play(volume * v);
    }

    public void playRandomMenuAmbience(float volume) {
        float v = GameSettings.getMusicVolume();
        if (v <= 0f || menuAmbience == null || menuAmbience.length == 0) {
            return;
        }
        int index = MathUtils.random(menuAmbience.length - 1);
        menuAmbience[index].play(volume * v);
    }

    public void dispose() {
        if (menuMusic != null) menuMusic.dispose();
        if (uiHover != null) uiHover.dispose();
        if (uiSelect != null) uiSelect.dispose();
        if (uiStart != null) uiStart.dispose();
        if (uiPanelInOut != null) uiPanelInOut.dispose();
        if (uiLevelSelect != null) uiLevelSelect.dispose();
        if (achievementNotice != null) achievementNotice.dispose();
        if (achievementWinner != null) achievementWinner.dispose();
        if (menuAmbience != null) {
            for (Sound sound : menuAmbience) {
                if (sound != null) sound.dispose();
            }
        }
        INSTANCE = null;
    }
}
