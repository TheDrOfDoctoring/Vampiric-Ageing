package com.doctor.vampiricageing.command;

import com.doctor.vampiricageing.VampiricAgeing;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;


public class VampiricAgeingCommands {
    public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext) {
        dispatcher.register(LiteralArgumentBuilder.<CommandSourceStack>literal("vampiricageing").then(ChangeAgeCommand.register()));
    }
}
