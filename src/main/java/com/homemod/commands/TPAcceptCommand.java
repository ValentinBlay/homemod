package com.homemod.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.homemod.data.TPManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.commands.arguments.EntityArgument;

public class TPAcceptCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("tpaccept")
            .then(Commands.argument("player", EntityArgument.player())
            .executes(context -> execute(context, EntityArgument.getPlayer(context, "player")))));
    }

    private static int execute(CommandContext<CommandSourceStack> context, ServerPlayer requester) {
        ServerPlayer target = context.getSource().getPlayer();

        boolean accepted = false;

        // Vérifie si une demande TPA existe
        if (TPManager.acceptTPA(target, requester)) {
            target.displayClientMessage(Component.literal("Vous avez accepté la demande de téléportation de " + requester.getName().getString()), false);
            requester.displayClientMessage(Component.literal("Vous avez été téléporté vers " + target.getName().getString()), false);
            accepted = true;
        }
        // Vérifie si une demande TPR existe
        else if (TPManager.acceptTPR(target, requester)) {
            target.displayClientMessage(Component.literal("Vous avez accepté la demande de téléportation vers " + requester.getName().getString()), false);
            requester.displayClientMessage(Component.literal(target.getName().getString() + " est venu vers vous"), false);
            accepted = true;
        }

        if (!accepted) {
            target.displayClientMessage(Component.literal("Aucune demande de téléportation en attente de " + requester.getName().getString()), false);
            return 0;
        }

        return 1;
    }
}
