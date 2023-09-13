package com.doctor.vampiricageing.command;

import com.doctor.vampiricageing.capabilities.AgeingCapability;
import com.doctor.vampiricageing.capabilities.VampiricAgeingCapabilityManager;
import com.doctor.vampiricageing.config.CommonConfig;
import com.google.common.collect.Lists;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class ChangeAgeCommand extends BasicCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {

        return Commands.literal("age")
                .requires(context -> context.hasPermission(PERMISSION_LEVEL_CHEAT))
                        .then(Commands.argument("age", IntegerArgumentType.integer(0))
                                .executes(context -> setAge(context, IntegerArgumentType.getInteger(context, "age"), Lists.newArrayList(context.getSource().getPlayerOrException())))
                                .then(Commands.argument("player", EntityArgument.entities())
                                        .executes(context -> setAge(context, IntegerArgumentType.getInteger(context, "level"), EntityArgument.getPlayers(context, "player")))));
    }
    @SuppressWarnings("SameReturnValue")
    private static int setAge(@NotNull CommandContext<CommandSourceStack> context, int age, @NotNull Collection<ServerPlayer> players) {
        for (ServerPlayer player : players) {
                int level = FactionPlayerHandler.getOpt(player).map(FactionPlayerHandler::getCurrentLevel).orElse(0);
                if (age < 6 && age >= 0 && level > 0 && !Helper.isHunter(player)) {
                    VampiricAgeingCapabilityManager.getAge(player).ifPresent(ageCap -> ageCap.setAge(age));
                    VampiricAgeingCapabilityManager.syncAgeCap(player);
                    context.getSource().sendSuccess(Component.translatable("command.vampiricageing.base.age.success", player.getName(), age), true);
                } else{
                    context.getSource().sendFailure(players.size() > 1 ? Component.translatable("command.vampiricageing.failed_to_execute.players", player.getDisplayName()) : Component.translatable("command.vampiricageing.failed_to_execute"));
                }
            }
        return 0;
    }

}
