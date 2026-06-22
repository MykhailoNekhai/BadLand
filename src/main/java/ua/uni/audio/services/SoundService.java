package ua.uni.audio.services;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.TimeUtils;
import ua.uni.core.config.GameSettings;

public final class SoundService {
    private static final float AMBIENT_FREQUENCY_BOOST = 0.64f;
    private static final float MENU_AMBIENT_VOLUME = 0.45f;
    private static final float MENU_AMBIENT_FIRST_DELAY = 4.5f * AMBIENT_FREQUENCY_BOOST;
    private static final float MENU_AMBIENT_MIN_DELAY = 5.5f * AMBIENT_FREQUENCY_BOOST;
    private static final float MENU_AMBIENT_MAX_DELAY = 10.5f * AMBIENT_FREQUENCY_BOOST;
    private static final float LEVEL_AMBIENT_VOLUME = 0.16f;
    private static final float LEVEL_AMBIENT_FIRST_DELAY = 5.0f * AMBIENT_FREQUENCY_BOOST;
    private static final float LEVEL_AMBIENT_MIN_DELAY = 6.5f * AMBIENT_FREQUENCY_BOOST;
    private static final float LEVEL_AMBIENT_MAX_DELAY = 11.5f * AMBIENT_FREQUENCY_BOOST;
    private static final String USED_AUDIO_ROOT = "game-resourses/audio/catalog/used/";

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

    private float menuAmbientElapsed;
    private float nextMenuAmbientAt = MENU_AMBIENT_FIRST_DELAY;
    private float levelAmbientElapsed;
    private float nextLevelAmbientAt = LEVEL_AMBIENT_FIRST_DELAY;
    private float flySoundCooldown;
    private long lastImpactTime;
    private long lastDeathTime;

    public SoundService() {
        uiHover = usedSound("ui/menu/69-Hover.wav");
        uiSelect = usedSound("ui/menu/ui_select.wav");
        uiStart = usedSound("ui/menu/76-Start.wav");
        uiPanelInOut = usedSound("ui/menu/75-MenuInOuts.wav");
        uiLevelSelect = usedSound("ui/menu/73-MP4select.wav");
        achievementNotice = usedSound("ui/system/46-achievement notice.wav");
        achievementWinner = usedSound("level/results/51-winner.wav");
        levelWin = usedSound("level/results/51-winner.wav");
        levelLose = usedSound("level/results/222-sidedeath pro tools filt.wav");

        menuAmbience = new Sound[] {
                usedSound("menu/ambience/1-Dawnsong.wav"),
                usedSound("menu/ambience/2-bee1.wav"),
                usedSound("menu/ambience/3-bee2.wav"),
                usedSound("menu/ambience/4-bird clip.wav"),
                usedSound("menu/ambience/5-birdie.wav"),
                usedSound("menu/ambience/6-birdie2.wav"),
                usedSound("menu/ambience/7-birdie3.wav"),
                usedSound("menu/ambience/8-blackbird1.wav"),
                usedSound("menu/ambience/9-blackbird2.wav"),
                usedSound("menu/ambience/10-blackbird3.wav"),
                usedSound("menu/ambience/11-blackbird4.wav"),
                usedSound("menu/ambience/12-distant frogs.wav"),
                usedSound("menu/ambience/13-more frogs.wav"),
                usedSound("menu/ambience/14-morningbird.wav"),
                usedSound("menu/ambience/15-nightgale1.wav"),
                usedSound("menu/ambience/16-nightgale2.wav"),
                usedSound("menu/ambience/17-nightgale3.wav"),
                usedSound("menu/ambience/18-nightgale4.wav"),
                usedSound("menu/ambience/19-nightgale5.wav"),
                usedSound("menu/ambience/20-nightgale6.wav"),
                usedSound("menu/ambience/21-nightgale7.wav"),
                usedSound("menu/ambience/22-nightgale8.wav"),
                usedSound("menu/ambience/23-nightgaledub.wav"),
                usedSound("menu/ambience/24-nightgaledub2.wav"),
                usedSound("menu/ambience/25-robin1.wav"),
                usedSound("menu/ambience/26-robin2.wav"),
                usedSound("menu/ambience/27-smaller birds.wav"),
                usedSound("menu/ambience/28-sparrow.wav"),
                usedSound("menu/ambience/29-splash.wav"),
                usedSound("menu/ambience/30-ujellus.wav"),
                usedSound("menu/ambience/31-day pad.wav"),
                usedSound("menu/ambience/32-vibra hit.wav")
        };

        levelAmbience = new Sound[] {
                usedSound("level/ambience/2-bird.wav"),
                usedSound("level/ambience/3-birdflock.wav"),
                usedSound("level/ambience/4-birdsvarifi.wav"),
                usedSound("level/ambience/5-crow.wav"),
                usedSound("level/ambience/6-electric.wav"),
                usedSound("level/ambience/7-electric2.wav"),
                usedSound("level/ambience/8-motor.wav"),
                usedSound("level/ambience/9-motor2.wav"),
                usedSound("level/ambience/10-treefall1.wav"),
                usedSound("level/ambience/11-treefall2.wav"),
                usedSound("level/ambience/12-treefall3.wav"),
                usedSound("level/ambience/13-treefallreverse.wav"),
                usedSound("level/ambience/1-electric1.wav"),
                usedSound("level/ambience/2-electric2.wav"),
                usedSound("level/ambience/3-electric3.wav"),
                usedSound("level/ambience/4-electric4.wav"),
                usedSound("level/ambience/5-electro1.wav"),
                usedSound("level/ambience/6-electrocreature.wav"),
                usedSound("level/ambience/7-electrocreature2.wav"),
                usedSound("level/ambience/8-nightpad24b48k.wav"),
                usedSound("level/ambience/9-pulse1.wav"),
                usedSound("level/ambience/10-pulsereverse.wav"),
                usedSound("level/ambience/11-servo1.wav"),
                usedSound("level/ambience/12-servo2.wav"),
                usedSound("level/ambience/13-servo3.wav"),
                usedSound("level/ambience/14-servo4.wav"),
                usedSound("level/ambience/15-servo5.wav")
        };

        cloneSounds = new Sound[] {
                usedSound("level/player/clone/56-clone1.wav"),
                usedSound("level/player/clone/57-clone2.wav"),
                usedSound("level/player/clone/58-clone3.wav"),
                usedSound("level/player/clone/59-clone4.wav")
        };

        biggerSound = usedSound("level/player/powerups/52-BIGGERZ_03.wav");
        smallerSound = usedSound("level/player/powerups/64-smaller NEW.wav");
        slowerSound = usedSound("level/player/powerups/63-slower.wav");
        fastSound = usedSound("level/player/powerups/66-timefast.wav");

        flySounds = new Sound[] {
                usedSound("level/player/fly/214-fly1 pro tools filt1.wav"),
                usedSound("level/player/fly/215-fly1 pro tools filt2.wav"),
                usedSound("level/player/fly/216-fly1 pro tools filt3.wav"),
                usedSound("level/player/fly/217-fly1.wav"),
                usedSound("level/player/fly/218-fly2 pro tools filt1.wav"),
                usedSound("level/player/fly/219-fly2 pro tools filt2.wav"),
                usedSound("level/player/fly/220-fly2 pro tools filt3.wav"),
                usedSound("level/player/fly/221-fly2.wav")
        };

        circsawLoopSound = usedSound("level/objects/circsaw/160-Circsaw loop.wav");

        impactMetalSounds = new Sound[] {
                usedSound("level/impacts/metal/201-PLhitM1.wav"),
                usedSound("level/impacts/metal/202-PLhitM2.wav"),
                usedSound("level/impacts/metal/203-PLhitM3.wav"),
                usedSound("level/impacts/metal/204-PLhitM4.wav"),
                usedSound("level/impacts/metal/205-PLhitM5.wav")
        };

        impactWoodSounds = new Sound[] {
                usedSound("level/impacts/wood/207-PLhitW1.wav"),
                usedSound("level/impacts/wood/208-PLhitW2.wav"),
                usedSound("level/impacts/wood/209-PLhitW3.wav")
        };

        impactRockSounds = new Sound[] {
                usedSound("level/impacts/rock/86-rockshort1.wav"),
                usedSound("level/impacts/rock/87-rockshort2.wav"),
                usedSound("level/impacts/rock/88-rockshort3.wav"),
                usedSound("level/impacts/rock/89-rockshort4.wav"),
                usedSound("level/impacts/rock/90-rockshort5.wav"),
                usedSound("level/impacts/rock/91-rockshort6.wav")
        };

        impactRubberSounds = new Sound[] {
                usedSound("level/impacts/rubber/206-PLhitR1.wav")
        };

        squishSound = usedSound("level/player/death/223-squish.wav");
        sideDeathSound = usedSound("level/player/death/222-sidedeath pro tools filt.wav");
    }

    public void playHover() {
        play(uiHover, 0.35f);
    }

    public void playSelect(float volume) {
        play(uiSelect, volume);
    }

    public void playStart(float volume) {
        play(uiStart, volume);
    }

    public void playPanelInOut(float volume) {
        play(uiPanelInOut, volume);
    }

    public void playLevelSelect(float volume) {
        play(uiLevelSelect, volume);
    }

    public void playAchievementNotice(float volume) {
        play(achievementNotice, volume);
    }

    public void playAchievementWinner(float volume) {
        play(achievementWinner, volume);
    }

    public void playLevelWin(float volume) {
        play(levelWin, volume);
    }

    public void playLevelLose(float volume) {
        play(levelLose, volume);
    }

    public void playRandomMenuAmbience(float volume) {
        playRandom(menuAmbience, volume);
    }

    public void playRandomLevelAmbience(float volume) {
        playRandom(levelAmbience, volume);
    }

    public void playRandomCloneSound(float volume) {
        playRandom(cloneSounds, volume);
    }

    public void playBiggerSound(float volume) {
        play(biggerSound, volume);
    }

    public void playSmallerSound(float volume) {
        play(smallerSound, volume);
    }

    public void playSlowerSound(float volume) {
        play(slowerSound, volume);
    }

    public void playFastSound(float volume) {
        play(fastSound, volume);
    }

    public void playImpactSound(float impulse, String material) {
        long currentTime = TimeUtils.millis();
        if (currentTime - lastImpactTime < 50) {
            return;
        }
        lastImpactTime = currentTime;

        Sound[] targetSounds;
        if ("metal".equals(material)) {
            targetSounds = impactMetalSounds;
        } else if ("rock".equals(material)) {
            targetSounds = impactRockSounds;
        } else if ("wood".equals(material)) {
            targetSounds = impactWoodSounds;
        } else {
            targetSounds = impactRubberSounds;
        }

        if (targetSounds.length == 0) {
            return;
        }

        float masterVolume = GameSettings.getMusicVolume();
        if (masterVolume > 0f) {
            float volume = Math.min(impulse / 15f, 1.0f) * masterVolume * 0.8f;
            targetSounds[MathUtils.random(targetSounds.length - 1)].play(volume);
        }
    }

    public void playSquishSound() {
        if (isDeathSoundThrottled()) {
            return;
        }
        play(squishSound, 1f);
    }

    public void playSideDeathSound() {
        if (isDeathSoundThrottled()) {
            return;
        }
        play(sideDeathSound, 1f);
    }

    public Sound getCircsawLoopSound() {
        return circsawLoopSound;
    }

    public void updateMenuAmbience(float delta, boolean menuContextActive) {
        if (!menuContextActive) {
            return;
        }
        menuAmbientElapsed += delta;
        if (menuAmbientElapsed < nextMenuAmbientAt) {
            return;
        }
        playRandomMenuAmbience(MENU_AMBIENT_VOLUME);
        nextMenuAmbientAt = menuAmbientElapsed + MathUtils.random(MENU_AMBIENT_MIN_DELAY, MENU_AMBIENT_MAX_DELAY);
    }

    public void updateLevelAmbience(float delta, boolean levelMusicWanted) {
        if (!levelMusicWanted) {
            return;
        }

        if (flySoundCooldown > 0f) {
            flySoundCooldown -= delta;
        }

        if (Gdx.input.isKeyPressed(GameSettings.getMoveUp()) && flySoundCooldown <= 0f) {
            playRandom(flySounds, 0.4f);
            flySoundCooldown = MathUtils.random(0.25f, 0.35f);
        }

        levelAmbientElapsed += delta;
        if (levelAmbientElapsed < nextLevelAmbientAt) {
            return;
        }
        playRandomLevelAmbience(LEVEL_AMBIENT_VOLUME);
        nextLevelAmbientAt = levelAmbientElapsed + MathUtils.random(LEVEL_AMBIENT_MIN_DELAY, LEVEL_AMBIENT_MAX_DELAY);
    }

    public void dispose() {
        uiHover.dispose();
        uiSelect.dispose();
        uiStart.dispose();
        uiPanelInOut.dispose();
        uiLevelSelect.dispose();
        achievementNotice.dispose();
        achievementWinner.dispose();
        levelWin.dispose();
        levelLose.dispose();
        disposeAll(menuAmbience);
        disposeAll(levelAmbience);
        disposeAll(cloneSounds);
        biggerSound.dispose();
        smallerSound.dispose();
        slowerSound.dispose();
        fastSound.dispose();
        disposeAll(flySounds);
        disposeAll(impactMetalSounds);
        disposeAll(impactWoodSounds);
        disposeAll(impactRockSounds);
        disposeAll(impactRubberSounds);
        circsawLoopSound.dispose();
        squishSound.dispose();
        sideDeathSound.dispose();
    }

    private Sound sound(String internalPath) {
        return Gdx.audio.newSound(Gdx.files.internal(internalPath));
    }

    private Sound usedSound(String path) {
        return sound(USED_AUDIO_ROOT + path);
    }

    private void play(Sound sound, float volume) {
        float masterVolume = GameSettings.getMusicVolume();
        if (masterVolume > 0f) {
            sound.play(volume * masterVolume);
        }
    }

    private void playRandom(Sound[] sounds, float volume) {
        float masterVolume = GameSettings.getMusicVolume();
        if (masterVolume <= 0f || sounds.length == 0) {
            return;
        }
        sounds[MathUtils.random(sounds.length - 1)].play(volume * masterVolume);
    }

    private boolean isDeathSoundThrottled() {
        long currentTime = TimeUtils.millis();
        if (currentTime - lastDeathTime < 50) {
            return true;
        }
        lastDeathTime = currentTime;
        return false;
    }

    private void disposeAll(Sound[] sounds) {
        for (Sound sound : sounds) {
            sound.dispose();
        }
    }
}
