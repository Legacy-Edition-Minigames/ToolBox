package net.kyrptonaught.ToolBox;

import com.google.gson.*;
import net.kyrptonaught.ToolBox.configs.BranchConfig;
import net.kyrptonaught.ToolBox.configs.BranchesConfig;

import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ConfigLoader {
    public static Gson gson = new Gson().newBuilder()
            .setLenient()
            .serializeNulls()
            .setPrettyPrinting()
            .registerTypeHierarchyAdapter(Path.class, new PathConverter())
            .create();

    public static BranchesConfig parseBranches(String json) {
        return gson.fromJson(json, BranchesConfig.class);
    }

    public static BranchConfig parseToolboxConfig(String json) {
        return gson.fromJson(json, BranchConfig.class);
    }

    public static InstalledServerInfo parseToolboxInstall(String json) {
        return gson.fromJson(json, InstalledServerInfo.class);
    }

    public static String serializeToolboxInstall(Object config) {
        return gson.toJson(config);
    }

    public static class PathConverter implements JsonDeserializer<Path>, JsonSerializer<Path> {
        @Override
        public Path deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return Paths.get(jsonElement.getAsString());
        }

        @Override
        public JsonElement serialize(Path path, Type type, JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive(path.toString().replaceAll("\\\\", "/"));
        }
    }
}
