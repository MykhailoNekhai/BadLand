package ua.uni.utilite;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import ua.uni.config.ObjectConfig;

import java.util.HashMap;

public class ConfigLoader {

    private static HashMap<String, ObjectConfig> configs;

    public static void load() {
        Json json = new Json();
        configs = json.fromJson(HashMap.class, ObjectConfig.class, Gdx.files.internal("game-resourses/assetPhysicData/obstacles-config.json"));
        System.out.println("Data base is loaded, current number of objests is: " + configs.size());
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