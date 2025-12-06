package com.homemod.data;

import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TPManager {

    private static final Map<UUID, TPDemand> tpaRequests = new HashMap<>();
    private static final Map<UUID, TPDemand> tprRequests = new HashMap<>();

    // Cooldown en ms (ex: 5 secondes)
    private static final long COOLDOWN = 5000;

    // Durée avant expiration de la demande (30s)
    private static final long EXPIRE = 30_000;

    public static class TPDemand {
        public final ServerPlayer requester;
        public final long timestamp;

        public TPDemand(ServerPlayer requester) {
            this.requester = requester;
            this.timestamp = System.currentTimeMillis();
        }

        public boolean isExpired() {
            return System.currentTimeMillis() - timestamp > EXPIRE;
        }
    }

    public static boolean requestTPA(ServerPlayer requester, ServerPlayer target) {
        TPDemand existing = tpaRequests.get(target.getUUID());
        if (existing != null && System.currentTimeMillis() - existing.timestamp < COOLDOWN) return false;

        tpaRequests.put(target.getUUID(), new TPDemand(requester));
        return true;
    }

    public static boolean requestTPR(ServerPlayer requester, ServerPlayer target) {
        TPDemand existing = tprRequests.get(target.getUUID());
        if (existing != null && System.currentTimeMillis() - existing.timestamp < COOLDOWN) return false;

        tprRequests.put(target.getUUID(), new TPDemand(requester));
        return true;
    }

    public static boolean acceptTPA(ServerPlayer target) {
        TPDemand demand = tpaRequests.get(target.getUUID());
        if (demand != null && !demand.isExpired()) {
            ServerPlayer requester = demand.requester;
            requester.teleportTo(target.getX(), target.getY(), target.getZ());
            target.displayClientMessage(
                net.minecraft.network.chat.Component.literal("Vous avez accepté la demande de téléportation de " + requester.getName().getString()), false
            );
            requester.displayClientMessage(
                net.minecraft.network.chat.Component.literal("Vous avez été téléporté vers " + target.getName().getString()), false
            );
            tpaRequests.remove(target.getUUID());
            return true;
        }
        tpaRequests.remove(target.getUUID());
        return false;
    }

    public static boolean acceptTPR(ServerPlayer target) {
        TPDemand demand = tprRequests.get(target.getUUID());
        if (demand != null && !demand.isExpired()) {
            ServerPlayer requester = demand.requester;
            target.teleportTo(requester.getX(), requester.getY(), requester.getZ());
            target.displayClientMessage(
                net.minecraft.network.chat.Component.literal("Vous avez accepté la demande de téléportation vers " + requester.getName().getString()), false
            );
            requester.displayClientMessage(
                net.minecraft.network.chat.Component.literal(target.getName().getString() + " est venu vers vous"), false
            );
            tprRequests.remove(target.getUUID());
            return true;
        }
        tprRequests.remove(target.getUUID());
        return false;
    }
}
