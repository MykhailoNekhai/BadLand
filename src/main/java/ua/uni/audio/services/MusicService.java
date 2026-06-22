package ua.uni.audio.services;

import java.util.EnumMap;
import java.util.Map;
import ua.uni.audio.music.LevelMusic;
import ua.uni.audio.music.LoginMusic;
import ua.uni.audio.music.MenuMusic;
import ua.uni.audio.music.PMusic;
import ua.uni.core.config.GameSettings;

public final class MusicService {
    private final EnumMap<MusicContext, PMusic> tracks = new EnumMap<>(MusicContext.class);
    private final EnumMap<MusicContext, Integer> contextDepths = new EnumMap<>(MusicContext.class);

    public MusicService() {
        register(MusicContext.LOGIN, new LoginMusic());
        register(MusicContext.MENU, new MenuMusic());
        register(MusicContext.LEVEL, new LevelMusic());
    }

    public void enter(MusicContext context) {
        contextDepths.put(context, contextDepth(context) + 1);
        playExclusive(context);
    }

    public void leave(MusicContext context) {
        int depth = contextDepth(context);
        if (depth <= 1) {
            contextDepths.put(context, 0);
            pause(context);
            return;
        }
        contextDepths.put(context, depth - 1);
    }

    public void play(MusicContext context) {
        playExclusive(context);
    }

    public void pause(MusicContext context) {
        track(context).pause();
    }

    public void stop(MusicContext context) {
        contextDepths.put(context, 0);
        track(context).stop();
    }

    public void applySoundSetting() {
        float volume = GameSettings.getMusicVolume();
        for (PMusic track : tracks.values()) {
            track.applyVolume(volume);
        }
    }

    public boolean isContextActive(MusicContext context) {
        return contextDepth(context) > 0;
    }

    public boolean isWanted(MusicContext context) {
        return track(context).isWanted();
    }

    public void dispose() {
        for (PMusic track : tracks.values()) {
            track.dispose();
        }
        tracks.clear();
        contextDepths.clear();
    }

    private void register(MusicContext context, PMusic track) {
        tracks.put(context, track);
        contextDepths.put(context, 0);
    }

    private void playExclusive(MusicContext context) {
        for (Map.Entry<MusicContext, PMusic> entry : tracks.entrySet()) {
            if (entry.getKey() == context) {
                entry.getValue().play(GameSettings.getMusicVolume());
            } else {
                entry.getValue().pause();
            }
        }
    }

    private PMusic track(MusicContext context) {
        PMusic track = tracks.get(context);
        if (track == null) {
            throw new IllegalArgumentException("Unknown music context: " + context);
        }
        return track;
    }

    private int contextDepth(MusicContext context) {
        return contextDepths.getOrDefault(context, 0);
    }
}
