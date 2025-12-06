package com.homemod.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.homemod.data.HomeManager;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;

public class DelHomeCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("delhome")
            .then(Commands.argument("name", StringArgumentType.word())
            .executes(context -> execute(context, StringArgumentType.getString(context, "name")))));
    }

    private static int execute(CommandContext<CommandSourceStack> context, String name) {
        ServerPlayer player = context.getSource().getPlayer();
        boolean removed = HomeManager.delHome(player, name);

        if (removed) {
            player.displayClientMessage(Component.literal("Home '" + name + "' supprimé."), false);
        } else {
            player.displayClientMessage(Component.literal("Home '" + name + "' inexistant."), false);
        }
        return removed ? 1 : 0;
    }
}
