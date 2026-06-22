package ua.uni.bootstrap;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import ua.uni.gameplay.achievements.AchievementManager;
import ua.uni.platform.auth.FirebaseAuthService;
import ua.uni.platform.auth.FirebaseConfig;
import ua.uni.platform.auth.FirebaseStorageService;
import ua.uni.platform.auth.FirestoreService;
import ua.uni.platform.auth.PlayerDataSyncService;
import ua.uni.platform.auth.SessionManager;
import ua.uni.platform.auth.TokenRefreshService;
import ua.uni.platform.online.CoopMatchState;
import ua.uni.platform.online.CoopMatchStateHolder;
import ua.uni.platform.online.NakamaClient;
import ua.uni.platform.online.NakamaMatchService;
import ua.uni.platform.online.NakamaSessionService;
import ua.uni.platform.online.NakamaSocket;
import ua.uni.platform.online.OnlineConfig;
import ua.uni.platform.online.OnlineSessionStore;
import ua.uni.platform.online.gameplay.GameplayReservationService;
import ua.uni.platform.online.gameplay.StaticGameplayReservationService;

public final class GameServices {
    private final Game game;
    private final SpriteBatch batch;
    private final FirebaseAuthService authService;
    private final FirestoreService firestoreService;
    private final FirebaseStorageService storageService;
    private final SessionManager sessionManager;
    private final PlayerDataSyncService syncService;
    private final TokenRefreshService tokenRefreshService;
    private final OnlineConfig onlineConfig;
    private final NakamaSessionService nakamaSessionService;
    private final NakamaMatchService nakamaMatchService;
    private final GameplayReservationService reservationService;
    private final AchievementManager achievementManager;
    private final CoopMatchStateHolder coopState;

    GameServices(Game game, SpriteBatch batch) {
        this.game = game;
        this.batch = batch;

        FirebaseConfig firebaseConfig = FirebaseConfig.loadFromResources();
        this.authService = new FirebaseAuthService(firebaseConfig);
        this.firestoreService = new FirestoreService(firebaseConfig);
        this.storageService = new FirebaseStorageService(firebaseConfig);
        this.sessionManager = new SessionManager();

        OnlineConfig online = OnlineConfig.loadFromResources();
        this.onlineConfig = online;
        NakamaClient nakamaClient = new NakamaClient(online);
        com.heroiclabs.nakama.Client onlineClient = nakamaClient.createClient();
        OnlineSessionStore onlineSessionStore = new OnlineSessionStore();
        this.nakamaSessionService = new NakamaSessionService(onlineClient, online, onlineSessionStore);
        this.nakamaMatchService = new NakamaMatchService(
                new NakamaSocket(onlineClient, online.getHost(), online.getSocketPort(), online.isSsl()));
        this.reservationService = new StaticGameplayReservationService(online);
        this.achievementManager = new AchievementManager();
        this.coopState = new CoopMatchStateHolder();

        this.tokenRefreshService = new TokenRefreshService(sessionManager, authService);
        this.syncService = new PlayerDataSyncService(
                sessionManager, tokenRefreshService, firestoreService, storageService, achievementManager);
    }

    public FirebaseAuthService auth() {
        return authService;
    }

    public FirestoreService firestore() {
        return firestoreService;
    }

    public FirebaseStorageService storage() {
        return storageService;
    }

    public SessionManager session() {
        return sessionManager;
    }

    public PlayerDataSyncService sync() {
        return syncService;
    }

    public OnlineConfig onlineConfig() {
        return onlineConfig;
    }

    public NakamaSessionService nakamaSession() {
        return nakamaSessionService;
    }

    public NakamaMatchService nakamaMatch() {
        return nakamaMatchService;
    }

    public GameplayReservationService reservation() {
        return reservationService;
    }

    public AchievementManager achievements() {
        return achievementManager;
    }

    public CoopMatchStateHolder coopState() {
        return coopState;
    }

    public SpriteBatch batch() {
        return batch;
    }

    public void setScreen(Screen screen) {
        game.setScreen(screen);
    }

    public String getValidIdToken() {
        return tokenRefreshService.getValidToken();
    }

    public Screen createStartupScreen() {
        return new ua.uni.presentation.screen.LoadingScreen(this,
            () -> {
                if (sessionManager.hasSession()) {
                    syncService.bootstrapFromCloud();
                    syncService.syncProfileHeartbeat();
                }
            },
            () -> {
                if (sessionManager.hasSession()) {
                    return new ua.uni.presentation.screen.menu.main.Menu(this);
                }
                return new ua.uni.presentation.screen.login.LoginMenu(this);
            }
        );
    }

    // Convenience: kept for internal use in Plevel before full migration
    public CoopMatchState getCoopMatchState() {
        return coopState.get();
    }

    public void setCoopMatchState(CoopMatchState state) {
        coopState.set(state);
    }

    public void clearCoopMatchState() {
        coopState.clear();
    }

    public void dispose() {
        syncService.shutdown();
    }
}
