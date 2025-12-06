package com.homemod.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.homemod.data.HomeManager;
import com.homemod.data.HomeManager.Home;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;

import java.util.EnumSet;
import java.util.Set;


public class HomeCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("home")
            .then(Commands.argument("name", StringArgumentType.word())
                .executes(context -> execute(context, StringArgumentType.getString(context, "name"))))
            .executes(context -> execute(context, "home")) // /home sans argument → home "home"
        );
    }

    private static int execute(CommandContext<CommandSourceStack> context, String name) {
        ServerPlayer player = context.getSource().getPlayer();
        Home home = HomeManager.getHome(player, name);

        if (home == null) {
            player.displayClientMessage(Component.literal("Home '" + name + "' inexistant."), false);
            return 0;
        }

        Vec3 pos = home.position;

        // Récupération du serveur et niveaux
        ServerLevel currentLevel = context.getSource().getLevel();
        MinecraftServer server = currentLevel.getServer();
        ServerLevel targetLevel = server.getLevel(home.dimension);

        if (targetLevel == null) {
            player.displayClientMessage(Component.literal("Dimension du home introuvable."), false);
            return 0;
        }

        if (currentLevel != targetLevel) {
            Set emptyRelative = java.util.Collections.emptySet();
            player.teleportTo(
                targetLevel,
                pos.x, pos.y, pos.z,
                emptyRelative,
                player.getYRot(),
                player.getXRot(),
                false
            );
        } else {
            player.teleportTo(pos.x, pos.y, pos.z);
        }

        // Son de téléportation
        context.getSource().getLevel().playSound(
            null,
            player.getX(),
            player.getY(),
            player.getZ(),
            SoundEvents.ENDERMAN_TELEPORT,
            SoundSource.PLAYERS,
            1.0f,
            1.0f
        );

        // Message de confirmation
        player.displayClientMessage(Component.literal("Téléporté à home '" + name + "'."), false);
        return 1;
    }
}