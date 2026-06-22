package ua.uni.audio.services;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import ua.uni.core.config.GameSettings;

public final class AudioManager {
    private static final float AMBIENT_FREQUENCY_BOOST = 0.64f;
    private static final float MENU_AMBIENT_VOLUME = 0.45f;
    private static final float MENU_AMBIENT_FIRST_DELAY = 4.5f * AMBIENT_FREQUENCY_BOOST;
    private static final float MENU_AMBIENT_MIN_DELAY = 5.5f * AMBIENT_FREQUENCY_BOOST;
    private static final float MENU_AMBIENT_MAX_DELAY = 10.5f * AMBIENT_FREQUENCY_BOOST;
    private static final float LEVEL_MUSIC_VOLUME = 0.42f;
    private static final float LEVEL_AMBIENT_VOLUME = 0.16f;
    private static final float LEVEL_AMBIENT_FIRST_DELAY = 5.0f * AMBIENT_FREQUENCY_BOOST;
    private static final float LEVEL_AMBIENT_MIN_DELAY = 6.5f * AMBIENT_FREQUENCY_BOOST;
    private static final float LEVEL_AMBIENT_MAX_DELAY = 11.5f * AMBIENT_FREQUENCY_BOOST;

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
    private final Sound[] cloneSounds;
    private final Sound biggerSound;
    private final Sound smallerSound;
    private final Sound slowerSound;
    private final Sound fastSound;
    private final Sound[] flySounds;
    private final Sound circsawLoopSound;
    private final Sound[] impactMetalSounds;
    private final Sound[] impactWoodSounds;
    private final Sound[] impactRockSounds;
    private final Sound[] impactRubberSounds;
    private final Sound squishSound;
    private final Sound sideDeathSound;
    private final Sound explosionSound;

    private boolean menuMusicWanted;
    private boolean levelMusicWanted;
    private int menuContextDepth;
    private float menuAmbientElapsed;
    private float nextMenuAmbientAt = MENU_AMBIENT_FIRST_DELAY;
    private float levelAmbientElapsed;
    private float nextLevelAmbientAt = LEVEL_AMBIENT_FIRST_DELAY;
    private float flySoundCooldown = 0f;
    private long lastImpactTime = 0;
    private long lastDeathTime = 0;

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

        cloneSounds = new Sound[] {
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/extracted/BadlandBank/56-clone1.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/extracted/BadlandBank/57-clone2.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/extracted/BadlandBank/58-clone3.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/extracted/BadlandBank/59-clone4.wav"))
        };

        biggerSound = Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/extracted/BadlandBank/52-BIGGERZ_03.wav"));
        smallerSound = Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/extracted/BadlandBank/64-smaller NEW.wav"));
        slowerSound = Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/extracted/BadlandBank/63-slower.wav"));
        fastSound = Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/extracted/BadlandBank/66-timefast.wav"));

        flySounds = new Sound[] {
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/extracted/BadlandBank/214-fly1 pro tools filt1.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/extracted/BadlandBank/215-fly1 pro tools filt2.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/extracted/BadlandBank/216-fly1 pro tools filt3.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/extracted/BadlandBank/217-fly1.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/extracted/BadlandBank/218-fly2 pro tools filt1.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/extracted/BadlandBank/219-fly2 pro tools filt2.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/extracted/BadlandBank/220-fly2 pro tools filt3.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/extracted/BadlandBank/221-fly2.wav"))
        };

        circsawLoopSound = Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/extracted/BadlandBank/160-Circsaw loop.wav"));

        impactMetalSounds = new Sound[] {
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/extracted/BadlandBank/201-PLhitM1.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/extracted/BadlandBank/202-PLhitM2.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/extracted/BadlandBank/203-PLhitM3.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/extracted/BadlandBank/204-PLhitM4.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/extracted/BadlandBank/205-PLhitM5.wav"))
        };

        impactWoodSounds = new Sound[] {
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/extracted/BadlandBank/207-PLhitW1.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/extracted/BadlandBank/208-PLhitW2.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/extracted/BadlandBank/209-PLhitW3.wav"))
        };

        impactRockSounds = new Sound[] {
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/extracted/BadlandBank/86-rockshort1.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/extracted/BadlandBank/87-rockshort2.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/extracted/BadlandBank/88-rockshort3.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/extracted/BadlandBank/89-rockshort4.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/extracted/BadlandBank/90-rockshort5.wav")),
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/extracted/BadlandBank/91-rockshort6.wav"))
        };

        impactRubberSounds = new Sound[] {
                Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/extracted/BadlandBank/206-PLhitR1.wav"))
        };

        squishSound = Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/extracted/BadlandBank/223-squish.wav"));
        sideDeathSound = Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/extracted/BadlandBank/222-sidedeath pro tools filt.wav"));
        explosionSound = Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/extracted/BadlandBank/144-Explosion 1.wav"));
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

    public void playExplosionSound(float volume) {
        float v = GameSettings.getMusicVolume();
        if (v > 0f) explosionSound.play(volume * v);
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

    public void playRandomCloneSound(float volume) {
        float v = GameSettings.getMusicVolume();
        if (v <= 0f || cloneSounds.length == 0) {
            return;
        }
        int index = MathUtils.random(cloneSounds.length - 1);
        cloneSounds[index].play(volume * v);
    }

    public void playBiggerSound(float volume) {
        float v = GameSettings.getMusicVolume();
        if (v > 0f) biggerSound.play(volume * v);
    }

    public void playSmallerSound(float volume) {
        float v = GameSettings.getMusicVolume();
        if (v > 0f) smallerSound.play(volume * v);
    }

    public void playSlowerSound(float volume) {
        float v = GameSettings.getMusicVolume();
        if (v > 0f) slowerSound.play(volume * v);
    }

    public void playFastSound(float volume) {
        float v = GameSettings.getMusicVolume();
        if (v > 0f) fastSound.play(volume * v);
    }

    public void playImpactSound(float impulse, String material) {
        long currentTime = com.badlogic.gdx.utils.TimeUtils.millis();
        if (currentTime - lastImpactTime < 50) return; // небольшая задержка, чтобы 50 клонов не оглушили за 1 кадр
        lastImpactTime = currentTime;

        Sound[] targetSounds;
        if ("metal".equals(material)) targetSounds = impactMetalSounds;
        else if ("rock".equals(material)) targetSounds = impactRockSounds;
        else if ("wood".equals(material)) targetSounds = impactWoodSounds;
        else targetSounds = impactRubberSounds;

        if (targetSounds.length == 0) return;

        float v = GameSettings.getMusicVolume();
        if (v > 0f) {
            // Импульс может быть от 2 до 50+, делаем нелинейную громкость
            float volume = Math.min(impulse / 15f, 1.0f) * v * 0.8f;
            int index = MathUtils.random(targetSounds.length - 1);
            targetSounds[index].play(volume);
        }
    }

    public void playSquishSound() {
        long currentTime = com.badlogic.gdx.utils.TimeUtils.millis();
        if (currentTime - lastDeathTime < 50) return;
        lastDeathTime = currentTime;
        float v = GameSettings.getMusicVolume();
        if (v > 0f) squishSound.play(v);
    }

    public void playSideDeathSound() {
        long currentTime = com.badlogic.gdx.utils.TimeUtils.millis();
        if (currentTime - lastDeathTime < 50) return;
        lastDeathTime = currentTime;
        float v = GameSettings.getMusicVolume();
        if (v > 0f) sideDeathSound.play(v);
    }

    public Sound getCircsawLoopSound() {
        return circsawLoopSound;
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

        // Таймер взмахов крыльев уменьшается всегда
        if (flySoundCooldown > 0f) {
            flySoundCooldown -= delta;
        }

        if (Gdx.input.isKeyPressed(GameSettings.getMoveUp())) {
            if (flySoundCooldown <= 0f) {
                float v = GameSettings.getMusicVolume();
                if (v > 0f && flySounds.length > 0) {
                    int index = MathUtils.random(flySounds.length - 1);
                    flySounds[index].play(0.4f * v);
                }
                // Более строгая задержка между звуками (250-350 миллисекунд)
                flySoundCooldown = MathUtils.random(0.25f, 0.35f); 
            }
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
        for (Sound sound : cloneSounds) {
            sound.dispose();
        }
        biggerSound.dispose();
        smallerSound.dispose();
        slowerSound.dispose();
        fastSound.dispose();
        for (Sound sound : flySounds) {
            sound.dispose();
        }
        for (Sound sound : impactMetalSounds) sound.dispose();
        for (Sound sound : impactWoodSounds) sound.dispose();
        for (Sound sound : impactRockSounds) sound.dispose();
        for (Sound sound : impactRubberSounds) sound.dispose();
        circsawLoopSound.dispose();
        squishSound.dispose();
        sideDeathSound.dispose();
        INSTANCE = null;
    }
}
