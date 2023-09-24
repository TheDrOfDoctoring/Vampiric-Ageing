package com.doctor.vampiricageing.command;

import com.doctor.vampiricageing.VampiricAgeing;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;


public class VampiricAgeingCommands {
    public static void registerCommands(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(LiteralArgumentBuilder.<CommandSource>literal("vampiricageing").then(ChangeAgeCommand.register()));
    }
}
