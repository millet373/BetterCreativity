package net.ibubble.bettercreativity.config;

import com.google.gson.Gson;
import net.fabricmc.loader.api.FabricLoader;
import net.ibubble.bettercreativity.BetterCreativity;

import java.io.*;

public class ConfigManager {
    private static ConfigManager instance;

    public static ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    private File file;
    private ConfigObject config;

    private ConfigManager() {
        loadConfig();
    }

    public ConfigObject getConfig() {
        if (config == null) {
            config = new ConfigObject();
        }
        return config;
    }

    private void loadConfig() {
        if (file == null) {
            file = new File(FabricLoader.getInstance().getConfigDir().toFile(), BetterCreativity.MOD_ID + ".json");
        }
        if (!file.exists()) {
            config = new ConfigObject();
            return;
        }

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            config = new Gson().fromJson(bufferedReader, ConfigObject.class);
        } catch (FileNotFoundException e) {
            System.err.println("Failed to load Better Creativity config");
            e.printStackTrace();
        }
    }

    public void saveConfig() {
        if (file == null) {
            file = new File(FabricLoader.getInstance().getConfigDir().toFile(), BetterCreativity.MOD_ID + ".json");
        }

        String jsonString = new Gson().toJson(config);

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(jsonString);
        } catch (IOException e) {
            System.err.println("Failed to save Better Creativity config");
            e.printStackTrace();
        }
    }
}
