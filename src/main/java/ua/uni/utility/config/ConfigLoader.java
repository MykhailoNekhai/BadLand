package ua.uni.utility.config;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import ua.uni.core.config.ObjectConfig;
import ua.uni.core.logging.AppLogger;

import java.util.HashMap;

public class ConfigLoader {

    private static final String TAG = "ConfigLoader";
    private static HashMap<String, ObjectConfig> configs;

    public static void load() {
        Json json = new Json();
        configs = json.fromJson(HashMap.class, ObjectConfig.class, Gdx.files.internal("game-resourses/assetPhysicData/obstacles-config.json"));
        AppLogger.info(TAG, "Config loaded: " + configs.size() + " objects");
    }

    public static ObjectConfig get(String objectName) {
        if (configs == null) {
            throw new RuntimeException("No config file is found");
        }
        if (!configs.containsKey(objectName)) {
            throw new IllegalArgumentException("No object in config with name: " + objectName + "'!");
        }
        return configs.get(objectName);
    }
}
