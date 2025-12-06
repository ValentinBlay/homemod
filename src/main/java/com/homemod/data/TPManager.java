package com.homemod.data;

import net.minecraft.server.level.ServerPlayer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TPManager {

    // key = demandeur, value = cible
    private static final Map<UUID, UUID> tpaRequests = new HashMap<>();
    private static final Map<UUID, UUID> tprRequests = new HashMap<>();

    public static void requestTPA(ServerPlayer from, ServerPlayer to) {
        tpaRequests.put(from.getUUID(), to.getUUID());
    }

    public static void requestTPR(ServerPlayer from, ServerPlayer to) {
        tprRequests.put(from.getUUID(), to.getUUID());
    }

    public static boolean acceptTPA(ServerPlayer target, ServerPlayer requester) {
        if (tpaRequests.get(requester.getUUID()) != null &&
            tpaRequests.get(requester.getUUID()).equals(target.getUUID())) {
            tpaRequests.remove(requester.getUUID());
            requester.teleportTo(target.getX(), target.getY(), target.getZ());
            return true;
        }
        return false;
    }

    public static boolean acceptTPR(ServerPlayer target, ServerPlayer requester) {
        if (tprRequests.get(requester.getUUID()) != null &&
            tprRequests.get(requester.getUUID()).equals(target.getUUID())) {
            tprRequests.remove(requester.getUUID());
            target.teleportTo(requester.getX(), requester.getY(), requester.getZ());
            return true;
        }
        return false;
    }
}
