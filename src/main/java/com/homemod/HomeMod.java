package com.homemod;

import com.homemod.commands.*;
import com.homemod.data.HomeStorage;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;

public class HomeMod implements ModInitializer {

    @Override
    public void onInitialize() {

        // Enregistrement des commandes (cela ne touche pas aux fichiers)
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            SetHomeCommand.register(dispatcher);
            DelHomeCommand.register(dispatcher);
            HomeCommand.register(dispatcher);
            TPACommand.register(dispatcher);
            TPRCommand.register(dispatcher);
            TPAcceptCommand.register(dispatcher);
            ListHomeCommand.register(dispatcher);
        });

        // Initialisation des fichiers homes uniquement quand le serveur est prêt
        ServerLifecycleEvents.SERVER_STARTED.register((MinecraftServer server) -> {
            HomeStorage.init(server);
        });
    }
}
