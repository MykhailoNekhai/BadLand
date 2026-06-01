package ua.uni.game;

import com.badlogic.gdx.Game;
import ua.uni.achivments.AchievementManager;
import ua.uni.auth.FirebaseAuthService;
import ua.uni.auth.FirebaseConfig;
import ua.uni.auth.FirestoreService;
import ua.uni.auth.SessionManager;
import ua.uni.config.GameSettings;
import ua.uni.web.login_menu.LoginMenu;
import ua.uni.web.main_menu.Menu;

public class MainGame extends Game {
    private FirebaseConfig firebaseConfig;
    private FirebaseAuthService authService;
    private FirestoreService firestoreService;
    private SessionManager sessionManager;
    private AchievementManager achievementManager;

    @Override
    public void create() {
        GameSettings.load();
        firebaseConfig = FirebaseConfig.loadFromResources();
        authService = new FirebaseAuthService(firebaseConfig);
        firestoreService = new FirestoreService(firebaseConfig);
        sessionManager = new SessionManager();
        achievementManager = new AchievementManager();
        if (sessionManager.hasSession()) {
            setScreen(new Menu(this));
        } else {
            setScreen(new LoginMenu(this));
        }
    }

    public FirebaseAuthService getAuthService() {
        return authService;
    }

    public FirestoreService getFirestoreService() {
        return firestoreService;
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    public AchievementManager getAchievementManager() {
        return achievementManager;
    }
}
