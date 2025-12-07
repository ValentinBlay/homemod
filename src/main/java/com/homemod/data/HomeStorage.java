package com.homemod.data;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import net.minecraft.server.MinecraftServer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.phys.Vec3;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class HomeStorage {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static Path storageFile;

    public static Map<String, Map<String, HomeManager.Home>> loadedHomes = new HashMap<>();

    public static void init(MinecraftServer server) {
        Path dir = server.getWorldPath(LevelResource.ROOT).resolve("homemod");
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        storageFile = dir.resolve("homes.json");
        load();
    }

    public static void load() {
        if (storageFile == null || !Files.exists(storageFile)) {
            loadedHomes = new HashMap<>();
            return;
        }

        try (Reader reader = Files.newBufferedReader(storageFile, StandardCharsets.UTF_8)) {
            Type type = new TypeToken<Map<String, Map<String, SavedHome>>>() {}.getType();
            Map<String, Map<String, SavedHome>> jsonMap = GSON.fromJson(reader, type);
            loadedHomes = new HashMap<>();

            if (jsonMap != null) {
                for (String player : jsonMap.keySet()) {
                    Map<String, HomeManager.Home> playerHomes = new HashMap<>();
                    for (String name : jsonMap.get(player).keySet()) {
                        SavedHome sh = jsonMap.get(player).get(name);
                        ResourceKey<Level> dim = getDimensionFromString(sh.dimension);
                        Vec3 pos = new Vec3(sh.x, sh.y, sh.z);
                        playerHomes.put(name, new HomeManager.Home(pos, dim));
                    }
                    loadedHomes.put(player, playerHomes);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        if (storageFile == null) return;

        Map<String, Map<String, SavedHome>> jsonMap = new HashMap<>();

        for (String player : loadedHomes.keySet()) {
            Map<String, SavedHome> playerMap = new HashMap<>();
            for (String name : loadedHomes.get(player).keySet()) {
                HomeManager.Home home = loadedHomes.get(player).get(name);
                String dim = getStringFromDimension(home.dimension);
                playerMap.put(name, new SavedHome(home.position.x, home.position.y, home.position.z, dim));
            }
            jsonMap.put(player, playerMap);
        }

        try (Writer writer = Files.newBufferedWriter(storageFile, StandardCharsets.UTF_8)) {
            GSON.toJson(jsonMap, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Classe intermédiaire pour le JSON
    private static class SavedHome {
        double x, y, z;
        String dimension;

        public SavedHome(double x, double y, double z, String dimension) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.dimension = dimension;
        }
    }

    // Convertir String -> Dimension
    private static ResourceKey<Level> getDimensionFromString(String dim) {
        return switch (dim.toLowerCase()) {
            case "overworld", "minecraft:overworld" -> Level.OVERWORLD;
            case "the_nether", "minecraft:the_nether" -> Level.NETHER;
            case "the_end", "minecraft:the_end" -> Level.END;
            default -> throw new IllegalArgumentException("Dimension inconnue: " + dim);
        };
    }

    // Convertir Dimension -> String
    private static String getStringFromDimension(ResourceKey<Level> dim) {
        if (dim == Level.OVERWORLD) return "minecraft:overworld";
        if (dim == Level.NETHER) return "minecraft:the_nether";
        if (dim == Level.END) return "minecraft:the_end";
        return "minecraft:overworld"; // fallback
    }
}
