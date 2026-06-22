package ua.uni.presentation.screen.menu.account;

record RemoteProfileSnapshot(
        String nickname,
        String email,
        String accountCreated,
        String lastLogin
) {
    boolean hasUpdates() {
        return !nickname.isBlank() || !email.isBlank() || !accountCreated.isBlank() || !lastLogin.isBlank();
    }
}
