package com.homemod;

import com.homemod.commands.SetHomeCommand;
import com.homemod.commands.DelHomeCommand;
import com.homemod.commands.HomeCommand;
import com.homemod.commands.TPACommand;
import com.homemod.commands.TPRCommand;
import com.homemod.commands.TPAcceptCommand;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class HomeMod implements ModInitializer {

    @Override
    public void onInitialize() {
        // Enregistrement des commandes lors de l'initialisation
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            SetHomeCommand.register(dispatcher);
            DelHomeCommand.register(dispatcher);
            HomeCommand.register(dispatcher);
            TPACommand.register(dispatcher);
            TPRCommand.register(dispatcher);
            TPAcceptCommand.register(dispatcher);
        });
    }
}
