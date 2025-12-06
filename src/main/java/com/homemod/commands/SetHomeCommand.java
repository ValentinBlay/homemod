package com.homemod.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.homemod.data.HomeManager;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

public class SetHomeCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("sethome")
            .then(Commands.argument("name", StringArgumentType.word())
            .executes(context -> execute(context, StringArgumentType.getString(context, "name")))));
    }

    private static int execute(CommandContext<CommandSourceStack> context, String name) {
        ServerPlayer player = context.getSource().getPlayer();
        HomeManager.setHome(player, name); // le message de succès est déjà géré dans HomeManager
        return 1;
    }
}
