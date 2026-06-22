package ua.uni.platform.online;

public final class CoopMatchStateHolder {
    private CoopMatchState state;

    public CoopMatchState get() {
        return state;
    }

    public void set(CoopMatchState state) {
        this.state = state;
    }

    public void clear() {
        this.state = null;
    }
}
