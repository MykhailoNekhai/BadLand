package ua.uni.audio.services;

import com.badlogic.gdx.audio.Sound;

public final class AudioManager {
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
        if (INSTANCE == null) {
            INSTANCE = new AudioManager();
        }
        return INSTANCE;
    }

    public void enterMusicContext(MusicContext context) {
        musicService.enter(context);
    }

    public void leaveMusicContext(MusicContext context) {
        musicService.leave(context);
    }

    public void playMusic(MusicContext context) {
        musicService.play(context);
    }

    public void pauseMusic(MusicContext context) {
        musicService.pause(context);
    }

    public void stopMusic(MusicContext context) {
        musicService.stop(context);
    }

    public void playLoginMusic() {
        enterMusicContext(MusicContext.LOGIN);
    }

    public void enterLoginContext() {
        enterMusicContext(MusicContext.LOGIN);
    }

    public void stopLoginMusic() {
        leaveMusicContext(MusicContext.LOGIN);
    }

    public void leaveLoginContext() {
        leaveMusicContext(MusicContext.LOGIN);
    }

    public void playMenuMusic() {
        enterMusicContext(MusicContext.MENU);
    }

    public void enterMenuContext() {
        enterMusicContext(MusicContext.MENU);
    }

    public void stopMenuMusic() {
        leaveMusicContext(MusicContext.MENU);
    }

    public void leaveMenuContext() {
        leaveMusicContext(MusicContext.MENU);
    }

    public void startLevelMusic() {
        playMusic(MusicContext.LEVEL);
    }

    public void pauseLevelMusic() {
        pauseMusic(MusicContext.LEVEL);
    }

    public void resumeLevelMusic() {
        playMusic(MusicContext.LEVEL);
    }

    public void stopLevelMusic() {
        stopMusic(MusicContext.LEVEL);
    }

    public void applySoundSetting() {
        musicService.applySoundSetting();
    }

    public void playHover() {
        soundService.playHover();
    }

    public void playExplosionSound(float volume) {
        float v = GameSettings.getMusicVolume();
        if (v > 0f) explosionSound.play(volume * v);
    }

    public void playSelect(float volume) {
        soundService.playSelect(volume);
    }

    public void playStart(float volume) {
        soundService.playStart(volume);
    }

    public void playPanelInOut(float volume) {
        soundService.playPanelInOut(volume);
    }

    public void playLevelSelect(float volume) {
        soundService.playLevelSelect(volume);
    }

    public void playAchievementNotice(float volume) {
        soundService.playAchievementNotice(volume);
    }

    public void playAchievementWinner(float volume) {
        soundService.playAchievementWinner(volume);
    }

    public void playLevelWin(float volume) {
        stopMusic(MusicContext.LEVEL);
        soundService.playLevelWin(volume);
    }

    public void playLevelLose(float volume) {
        stopMusic(MusicContext.LEVEL);
        soundService.playLevelLose(volume);
    }

    public void playRandomMenuAmbience(float volume) {
        soundService.playRandomMenuAmbience(volume);
    }

    public void playRandomCloneSound(float volume) {
        soundService.playRandomCloneSound(volume);
    }

    public void playBiggerSound(float volume) {
        soundService.playBiggerSound(volume);
    }

    public void playSmallerSound(float volume) {
        soundService.playSmallerSound(volume);
    }

    public void playSlowerSound(float volume) {
        soundService.playSlowerSound(volume);
    }

    public void playFastSound(float volume) {
        soundService.playFastSound(volume);
    }

    public void playImpactSound(float impulse, String material) {
        soundService.playImpactSound(impulse, material);
    }

    public void playSquishSound() {
        soundService.playSquishSound();
    }

    public void playSideDeathSound() {
        soundService.playSideDeathSound();
    }

    public Sound getCircsawLoopSound() {
        return soundService.getCircsawLoopSound();
    }

    public void updateMenuAmbience(float delta) {
        soundService.updateMenuAmbience(delta, musicService.isContextActive(MusicContext.MENU));
    }

    public void updateLevelAmbience(float delta) {
        soundService.updateLevelAmbience(delta, musicService.isWanted(MusicContext.LEVEL));
    }

    public void playRandomLevelAmbience(float volume) {
        soundService.playRandomLevelAmbience(volume);
    }

    public void dispose() {
        musicService.dispose();
        soundService.dispose();
        INSTANCE = null;
    }
}
