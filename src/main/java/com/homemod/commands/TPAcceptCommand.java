package com.homemod.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.homemod.data.TPManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;

public class TPAcceptCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("tpaccept")
            .executes(context -> execute(context))
        );
    }

    private static int execute(CommandContext<CommandSourceStack> context) {
        ServerPlayer target = context.getSource().getPlayer();

        boolean accepted = false;

        // TPA
        if (TPManager.acceptTPA(target)) {
            accepted = true;
        }
        // TPR
        else if (TPManager.acceptTPR(target)) {
            accepted = true;
        }

        if (!accepted) {
            target.displayClientMessage(Component.literal("Aucune demande de téléportation en attente."), false);
            return 0;
        }

        return 1;
    }
}
