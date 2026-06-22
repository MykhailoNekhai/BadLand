package ua.uni.audio.music;

public final class LevelMusic extends PMusic {
    private static final String PATH = "game-resourses/audio/catalog/used/level/background/1-Amb24b48k.wav";
    private static final float VOLUME_MULTIPLIER = 0.42f;

    public LevelMusic() {
        super(PATH, VOLUME_MULTIPLIER, true);
    }
}
