package ua.uni.audio.services;

import com.badlogic.gdx.audio.Sound;

public final class AudioManager {
    private static AudioManager INSTANCE;

    private final MusicService musicService;
    private final SoundService soundService;

    private AudioManager() {
        musicService = new MusicService();
        soundService = new SoundService();
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

    public void playExplosionSound(float volume) {
        soundService.playExplosionSound(volume);
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
