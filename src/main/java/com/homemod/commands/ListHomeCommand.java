package com.homemod.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.homemod.data.HomeManager;
import com.homemod.data.HomeManager.Home;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;

import java.util.Map;

public class ListHomeCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("homelist")
            .executes(context -> execute(context))
        );
    }

    private static int execute(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();

        Map<String, Home> homes = HomeManager.getAllHomes(player);

        if (homes.isEmpty()) {
            player.displayClientMessage(Component.literal("Vous n'avez aucun home défini."), false);
            return 0;
        }

        MutableComponent message = Component.literal("Vos homes : ");

        boolean first = true;
        for (String name : homes.keySet()) {
            if (!first) {
                message.append(Component.literal(", ").withStyle(style -> style.withColor(TextColor.fromRgb(0xAAAAAA))));
            }
            message.append(Component.literal(name).withStyle(style -> style.withColor(TextColor.fromRgb(0x00FF00))));
            first = false;
        }

        player.displayClientMessage(message, false);
        return 1;
    }
}
