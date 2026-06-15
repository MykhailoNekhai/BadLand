package ua.uni.platform.online.gameplay.protocol;

import ua.uni.platform.online.gameplay.MatchPhase;

public class MatchEndMessage {
    private final String type = "match_end";
    private MatchPhase phase;
    private String title;
    private String message;

    public String getType() {
        return type;
    }

    public MatchPhase getPhase() {
        return phase;
    }

    public void setPhase(MatchPhase phase) {
        this.phase = phase;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
