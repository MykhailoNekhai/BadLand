package ua.uni.platform.auth;

import okhttp3.OkHttpClient;

import java.util.concurrent.TimeUnit;

/**
 * Shared OkHttpClient for all Firebase services.
 * OkHttpClient is thread-safe and designed to be reused — one instance per app.
 */
final class FirebaseHttpClient {
    static final OkHttpClient INSTANCE = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build();

    private FirebaseHttpClient() {}
}
