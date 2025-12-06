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
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

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
        for (Map.Entry<String, Home> entry : homes.entrySet()) {
            String name = entry.getKey();
            Home home = entry.getValue();

            final int homeColor; // <-- variable finale pour le lambda
            ResourceKey<Level> dim = home.dimension;
            if (dim == Level.NETHER) {
                homeColor = 0xFF0000; // rouge
            } else if (dim == Level.END) {
                homeColor = 0xAA00FF; // Violet
            } else {
                homeColor = 0x00FF00; // vert
            }

            if (!first) {
                message.append(Component.literal(", ").withStyle(style -> style.withColor(TextColor.fromRgb(0xAAAAAA))));
            }
            message.append(Component.literal(name).withStyle(style -> style.withColor(TextColor.fromRgb(homeColor))));
            first = false;
        }
        player.displayClientMessage(message, false);
        return 1;
    }
}
