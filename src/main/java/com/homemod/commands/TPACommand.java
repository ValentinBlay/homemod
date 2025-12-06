package com.homemod.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TPACommand {

    // Map pour stocker les demandes de téléportation : targetUUID -> requesterUUID
    public static final Map<UUID, UUID> tpaRequests = new HashMap<>();

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("tpa")
            .then(Commands.argument("target", StringArgumentType.word())
                .executes(context -> {
                    ServerPlayer requester = context.getSource().getPlayer();
                    ServerPlayer target = context.getSource().getServer().getPlayerList()
                        .getPlayer(StringArgumentType.getString(context, "target"));

                    if (target == null) {
                        requester.displayClientMessage(Component.literal("Joueur introuvable."), false);
                        return 0;
                    }

                    tpaRequests.put(target.getUUID(), requester.getUUID());

                    requester.displayClientMessage(Component.literal("Demande de téléportation envoyée à " + target.getName().getString()), false);
                    target.displayClientMessage(Component.literal(requester.getName().getString() + " souhaite se téléporter à vous. Tapez /tpaccept pour accepter."), false);

                    // Petit son de notification pour le joueur ciblé
                    target.level().playSound(null, target.getX(), target.getY(), target.getZ(),
                        SoundEvents.NOTE_BLOCK_PLING, SoundSource.PLAYERS, 1.0f, 1.0f);

                    return 1;
                })));
    }
}
