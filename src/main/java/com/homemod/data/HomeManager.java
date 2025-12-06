package com.homemod.data;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;

public class HomeManager {

    private static final Map<String, Map<String, Home>> homes = new HashMap<>();

    public static class Home {
        public final Vec3 position;
        public final ResourceKey<Level> dimension;

        public Home(Vec3 position, ResourceKey<Level> dimension) {
            this.position = position;
            this.dimension = dimension;
        }
    }

    public static boolean delHome(ServerPlayer player, String name) {
        String playerName = player.getName().getString();
        Map<String, Home> playerHomes = homes.get(playerName);
        if (playerHomes != null && playerHomes.containsKey(name)) {
            playerHomes.remove(name);
            return true;
        }
        return false;
    }

    public static void setHome(ServerPlayer player, String name) {
        String playerName = player.getName().getString();
        homes.computeIfAbsent(playerName, k -> new HashMap<>());
        Map<String, Home> playerHomes = homes.get(playerName);

        if (playerHomes.size() >= 10 && !playerHomes.containsKey(name)) {
            player.displayClientMessage(Component.literal("Vous avez déjà 10 homes maximum."), false);
            return;
        }

        Home home = new Home(player.position(), player.level().dimension());
        playerHomes.put(name, home);
        player.displayClientMessage(Component.literal("Home '" + name + "' défini."), false);
    }

    public static Home getHome(ServerPlayer player, String name) {
        String playerName = player.getName().getString();
        Map<String, Home> playerHomes = homes.get(playerName);
        if (playerHomes != null) {
            return playerHomes.get(name);
        }
        return null;
    }
}
