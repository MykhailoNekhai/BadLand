package ua.uni.audio.services;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import ua.uni.config.GameSettings;

public final class AudioManager {
    private static final float MENU_AMBIENT_VOLUME = 0.30f;
    private static final float MENU_AMBIENT_FIRST_DELAY = 4.5f;
    private static final float MENU_AMBIENT_MIN_DELAY = 5.5f;
    private static final float MENU_AMBIENT_MAX_DELAY = 10.5f;
    private static final float LEVEL_MUSIC_VOLUME = 0.42f;
    private static final float LEVEL_AMBIENT_VOLUME = 0.16f;
    private static final float LEVEL_AMBIENT_FIRST_DELAY = 5.0f;
    private static final float LEVEL_AMBIENT_MIN_DELAY = 6.5f;
    private static final float LEVEL_AMBIENT_MAX_DELAY = 11.5f;

    private static AudioManager INSTANCE;

    private final Music menuMusic;
    private final Music levelMusic;
    private final Sound uiHover;
    private final Sound uiSelect;
    private final Sound uiStart;
    private final Sound uiPanelInOut;
    private final Sound uiLevelSelect;
    private final Sound achievementNotice;
    private final Sound achievementWinner;
    private final Sound levelWin;
    private final Sound levelLose;
    private final Sound[] menuAmbience;
    private final Sound[] levelAmbience;

    private boolean menuMusicWanted;
    private boolean levelMusicWanted;
    private int menuContextDepth;
    private float menuAmbientElapsed;
    private float nextMenuAmbientAt = MENU_AMBIENT_FIRST_DELAY;
    private float levelAmbientElapsed;
    private float nextLevelAmbientAt = LEVEL_AMBIENT_FIRST_DELAY;

    private AudioManager() {
        menuMusic = Gdx.audio.newMusic(Gdx.files.internal("game-resourses/audio/catalog/used/menu/background/menu_music.mp3"));
        menuMusic.setLooping(true);

        levelMusic = Gdx.audio.newMusic(Gdx.files.internal("game-resourses/audio/catalog/used/level/background/1-Amb24b48k.wav"));
        levelMusic.setLooping(true);

        uiHover = Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/catalog/used/ui/menu/69-Hover.wav"));
        uiSelect = Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/catalog/used/ui/menu/ui_select.wav"));
        uiStart = Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/catalog/used/ui/menu/76-Start.wav"));
        uiPanelInOut = Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/catalog/used/ui/menu/75-MenuInOuts.wav"));
        uiLevelSelect = Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/catalog/used/ui/menu/73-MP4select.wav"));
        achievementNotice = Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/catalog/used/ui/system/46-achievement notice.wav"));
        achievementWinner = Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/catalog/used/level/results/51-winner.wav"));
        levelWin = Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/catalog/used/level/results/51-winner.wav"));
        levelLose = Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/catalog/used/level/results/222-sidedeath pro tools filt.wav"));

        menuAmbience = new Sound[] {
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/catalog/used/menu/ambience/1-Dawnsong.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/catalog/used/menu/ambience/2-bee1.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/catalog/used/menu/ambience/3-bee2.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/catalog/used/menu/ambience/4-bird clip.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/catalog/used/menu/ambience/5-birdie.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/catalog/used/menu/ambience/6-birdie2.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/catalog/used/menu/ambience/7-birdie3.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/catalog/used/menu/ambience/8-blackbird1.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/catalog/used/menu/ambience/9-blackbird2.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/catalog/used/menu/ambience/10-blackbird3.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/catalog/used/menu/ambience/11-blackbird4.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/catalog/used/menu/ambience/12-distant frogs.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/catalog/used/menu/ambience/13-more frogs.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/catalog/used/menu/ambience/14-morningbird.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/catalog/used/menu/ambience/15-nightgale1.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/catalog/used/menu/ambience/16-nightgale2.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/catalog/used/menu/ambience/17-nightgale3.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/catalog/used/menu/ambience/18-nightgale4.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/catalog/used/menu/ambience/19-nightgale5.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/catalog/used/menu/ambience/20-nightgale6.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/catalog/used/menu/ambience/21-nightgale7.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/catalog/used/menu/ambience/22-nightgale8.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/catalog/used/menu/ambience/23-nightgaledub.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/catalog/used/menu/ambience/24-nightgaledub2.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/catalog/used/menu/ambience/25-robin1.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/catalog/used/menu/ambience/26-robin2.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/catalog/used/menu/ambience/27-smaller birds.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/catalog/used/menu/ambience/28-sparrow.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/catalog/used/menu/ambience/29-splash.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/catalog/used/menu/ambience/30-ujellus.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/catalog/used/menu/ambience/31-day pad.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/catalog/used/menu/ambience/32-vibra hit.wav"))
        };

        levelAmbience = new Sound[] {
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/catalog/used/level/ambience/2-bird.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/catalog/used/level/ambience/3-birdflock.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/catalog/used/level/ambience/4-birdsvarifi.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/catalog/used/level/ambience/5-crow.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/catalog/used/level/ambience/6-electric.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/catalog/used/level/ambience/7-electric2.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/catalog/used/level/ambience/8-motor.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/catalog/used/level/ambience/9-motor2.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/catalog/used/level/ambience/10-treefall1.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/catalog/used/level/ambience/11-treefall2.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/catalog/used/level/ambience/12-treefall3.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/catalog/used/level/ambience/13-treefallreverse.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/catalog/used/level/ambience/1-electric1.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/catalog/used/level/ambience/2-electric2.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/catalog/used/level/ambience/3-electric3.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/catalog/used/level/ambience/4-electric4.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/catalog/used/level/ambience/5-electro1.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/catalog/used/level/ambience/6-electrocreature.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/catalog/used/level/ambience/7-electrocreature2.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/catalog/used/level/ambience/8-nightpad24b48k.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/catalog/used/level/ambience/9-pulse1.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/catalog/used/level/ambience/10-pulsereverse.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/catalog/used/level/ambience/11-servo1.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/catalog/used/level/ambience/12-servo2.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/catalog/used/level/ambience/13-servo3.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/catalog/used/level/ambience/14-servo4.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/catalog/used/level/ambience/15-servo5.wav"))
        };
    }

    public static AudioManager get() {
        if (INSTANCE == null) INSTANCE = new AudioManager();
        return INSTANCE;
    }

    public void playMenuMusic() {
        enterMenuContext();
    }

    public void enterMenuContext() {
        menuContextDepth++;
        menuMusicWanted = true;
        pauseLevelMusic();
        applySoundSetting();
    }

    public void stopMenuMusic() {
        leaveMenuContext();
    }

    public void leaveMenuContext() {
        if (menuContextDepth > 0) {
            menuContextDepth--;
        }
        if (menuContextDepth == 0) {
            menuMusicWanted = false;
            menuMusic.pause();
        }
    }

    public void startLevelMusic() {
        levelMusicWanted = true;
        menuMusicWanted = false;
        menuMusic.pause();
        applySoundSetting();
    }

    public void pauseLevelMusic() {
        levelMusicWanted = false;
        if (levelMusic.isPlaying()) {
            levelMusic.pause();
        }
    }

    public void resumeLevelMusic() {
        levelMusicWanted = true;
        menuMusicWanted = false;
        menuMusic.pause();
        applySoundSetting();
    }

    public void stopLevelMusic() {
        levelMusicWanted = false;
        levelMusic.stop();
    }

    public void applySoundSetting() {
        float v = GameSettings.getMusicVolume();
        menuMusic.setVolume(v);
        levelMusic.setVolume(v * LEVEL_MUSIC_VOLUME);

        if (menuMusicWanted && v > 0f) {
            if (!menuMusic.isPlaying()) menuMusic.play();
        } else if (menuMusic.isPlaying()) {
            menuMusic.pause();
        }

        if (levelMusicWanted && v > 0f) {
            if (!levelMusic.isPlaying()) levelMusic.play();
        } else if (levelMusic.isPlaying()) {
            levelMusic.pause();
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

    public void playLevelWin(float volume) {
        stopLevelMusic();
        float v = GameSettings.getMusicVolume();
        if (v > 0f) levelWin.play(volume * v);
    }

    public void playLevelLose(float volume) {
        stopLevelMusic();
        float v = GameSettings.getMusicVolume();
        if (v > 0f) levelLose.play(volume * v);
    }

    public void playRandomMenuAmbience(float volume) {
        float v = GameSettings.getMusicVolume();
        if (v <= 0f || menuAmbience.length == 0) {
            return;
        }
        int index = MathUtils.random(menuAmbience.length - 1);
        menuAmbience[index].play(volume * v);
    }

    public void updateMenuAmbience(float delta) {
        if (menuContextDepth <= 0) {
            return;
        }
        menuAmbientElapsed += delta;
        if (menuAmbientElapsed < nextMenuAmbientAt) {
            return;
        }
        playRandomMenuAmbience(MENU_AMBIENT_VOLUME);
        nextMenuAmbientAt = menuAmbientElapsed + MathUtils.random(MENU_AMBIENT_MIN_DELAY, MENU_AMBIENT_MAX_DELAY);
    }

    public void updateLevelAmbience(float delta) {
        if (!levelMusicWanted) {
            return;
        }
        levelAmbientElapsed += delta;
        if (levelAmbientElapsed < nextLevelAmbientAt) {
            return;
        }
        playRandomLevelAmbience(LEVEL_AMBIENT_VOLUME);
        nextLevelAmbientAt = levelAmbientElapsed + MathUtils.random(LEVEL_AMBIENT_MIN_DELAY, LEVEL_AMBIENT_MAX_DELAY);
    }

    public void playRandomLevelAmbience(float volume) {
        float v = GameSettings.getMusicVolume();
        if (v <= 0f || levelAmbience.length == 0) {
            return;
        }
        int index = MathUtils.random(levelAmbience.length - 1);
        levelAmbience[index].play(volume * v);
    }

    public void dispose() {
        menuMusic.dispose();
        levelMusic.dispose();
        uiHover.dispose();
        uiSelect.dispose();
        uiStart.dispose();
        uiPanelInOut.dispose();
        uiLevelSelect.dispose();
        achievementNotice.dispose();
        achievementWinner.dispose();
        levelWin.dispose();
        levelLose.dispose();
        for (Sound sound : menuAmbience) {
            sound.dispose();
        }
        for (Sound sound : levelAmbience) {
            sound.dispose();
        }
        INSTANCE = null;
    }
}
