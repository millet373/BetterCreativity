package millet373.bettercreativity.config;

import com.google.gson.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import millet373.bettercreativity.BetterCreativity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.io.*;
import java.lang.reflect.Type;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class ConfigManager {
    private static ConfigManager instance;

    public static ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    private final Gson gson;
    private File file;
    private ConfigObject config;

    private ConfigManager() {
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(KeyBinding[].class, new KeyBindingTypeAdapter())
                .create();
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
            config = gson.fromJson(bufferedReader, ConfigObject.class);
        } catch (FileNotFoundException e) {
            System.err.println("Failed to load Better Creativity config");
            e.printStackTrace();
        }
    }

    public void saveConfig() {
        if (file == null) {
            file = new File(FabricLoader.getInstance().getConfigDir().toFile(), BetterCreativity.MOD_ID + ".json");
        }

        String jsonString = gson.toJson(config);

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(jsonString);
        } catch (IOException e) {
            System.err.println("Failed to save Better Creativity config");
            e.printStackTrace();
        }
    }

    public static class KeyBindingTypeAdapter implements JsonSerializer<KeyBinding[]>, JsonDeserializer<KeyBinding[]> {
        @Override
        public JsonElement serialize(KeyBinding[] src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            for (KeyBinding binding : src) {
                object.add(binding.getTranslationKey(), new JsonPrimitive(binding.getBoundKeyTranslationKey()));
            }
            return object;
        }

        @Override
        public KeyBinding[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject object = json.getAsJsonObject();
            KeyBinding[] keyBindings = new KeyBinding[object.size()];
            int i = 0;
            for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
                InputUtil.Key key;
                try {
                    key = InputUtil.fromTranslationKey(entry.getValue().getAsString());
                } catch (IllegalStateException e) {
                    key = InputUtil.UNKNOWN_KEY;
                }
                keyBindings[i] = new KeyBinding(entry.getKey(), GLFW.GLFW_KEY_UNKNOWN, "category.bettercreativity");
                keyBindings[i].setBoundKey(key);
                i++;
            }
            return keyBindings;
        }
    }
}
