package com.homemod.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.commands.arguments.EntityArgument;

public class TPRCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("tpr")
            .then(Commands.argument("target", EntityArgument.player())
                .executes(context -> {
                    ServerPlayer requester = context.getSource().getPlayer();
                    ServerPlayer target = EntityArgument.getPlayer(context, "target");

                    if (target == null) {
                        requester.displayClientMessage(Component.literal("Joueur introuvable."), false);
                        return 0;
                    }

                    // Enregistre la demande
                    com.homemod.data.TPManager.requestTPR(requester, target);

                    requester.displayClientMessage(Component.literal("Demande de téléportation vers " + target.getName().getString() + " envoyée."), false);
                    target.displayClientMessage(Component.literal(requester.getName().getString() + " souhaite se téléporter vers vous. Tapez /tpaccept pour accepter."), false);

                    // Son de notification
                    target.level().playSound(null, target.getX(), target.getY(), target.getZ(),
                        SoundEvents.NOTE_BLOCK_PLING, SoundSource.PLAYERS, 1.0f, 1.0f);

                    return 1;
                })
            )
        );
    }
}
