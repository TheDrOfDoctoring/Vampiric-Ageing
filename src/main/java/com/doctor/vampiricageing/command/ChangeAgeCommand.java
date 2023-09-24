package com.doctor.vampiricageing.command;

import com.doctor.vampiricageing.VampiricAgeing;
import com.doctor.vampiricageing.capabilities.AgeingCapability;
import com.doctor.vampiricageing.capabilities.CapabilityHelper;
import com.doctor.vampiricageing.capabilities.VampiricAgeingCapabilityManager;
import com.doctor.vampiricageing.config.CommonConfig;
import com.doctor.vampiricageing.config.HunterAgeingConfig;
import com.doctor.vampiricageing.config.WerewolvesAgeingConfig;
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
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class ChangeAgeCommand extends BasicCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {

        return Commands.literal("age")
                .requires(context -> context.hasPermission(PERMISSION_LEVEL_CHEAT))
                        .then(Commands.argument("age", IntegerArgumentType.integer(0))
                                .executes(context -> setAge(context, IntegerArgumentType.getInteger(context, "age"), Lists.newArrayList(context.getSource().getPlayerOrException())))
                                .then(Commands.argument("player", EntityArgument.entities())
                                        .executes(context -> setAge(context, IntegerArgumentType.getInteger(context, "age"), EntityArgument.getPlayers(context, "player")))));
    }
    @SuppressWarnings("SameReturnValue")
    private static int setAge(@NotNull CommandContext<CommandSourceStack> context, int age, @NotNull Collection<ServerPlayer> players) {
        for (ServerPlayer player : players) {
                int level = FactionPlayerHandler.getOpt(player).map(FactionPlayerHandler::getCurrentLevel).orElse(0);
                if (age < 6 && age >= 0 && level > 0) {
                    VampiricAgeingCapabilityManager.getAge(player).ifPresent(ageCap -> ageCap.setAge(age));
                    VampiricAgeingCapabilityManager.syncAgeCap(player);
                    context.getSource().sendSuccess(() -> Component.translatable("command.vampiricageing.base.age.success", player.getName(), age), true);
                } else if(age > 5 || age < 0) {
                    context.getSource().sendFailure(players.size() > 1 ? Component.translatable("command.vampiricageing.failed_to_execute.players.age", player.getDisplayName()) : Component.translatable("command.vampiricageing.failed_to_execute.age"));
                } else if(Helper.isHunter(player) && !HunterAgeingConfig.hunterAgeing.get()) {
                    if(ModList.get().isLoaded(VampiricAgeing.WEREWOLVES_MODID) && WerewolvesAgeingConfig.werewolfAgeing.get()) {
                        context.getSource().sendFailure(players.size() > 1 ? Component.translatable("command.vampiricageing.failed_to_execute.players.wrong_faction_werewolves_enabled", player.getDisplayName()) : Component.translatable("command.vampiricageing.failed_to_execute.wrong_faction_werewolves_enabled"));
                    } else {
                        context.getSource().sendFailure(players.size() > 1 ? Component.translatable("command.vampiricageing.failed_to_execute.players.wrong_faction", player.getDisplayName()) : Component.translatable("command.vampiricageing.failed_to_execute.wrong_faction"));
                    }
                } else if(CapabilityHelper.isWerewolfCheckMod(player) && WerewolvesAgeingConfig.werewolfAgeing.get() ) {
                    if(HunterAgeingConfig.hunterAgeing.get()) {
                        context.getSource().sendFailure(players.size() > 1 ? Component.translatable("command.vampiricageing.failed_to_execute.players.wrong_faction_hunters_enabled", player.getDisplayName()) : Component.translatable("command.vampiricageing.failed_to_execute.wrong_faction_hunters_enabled"));
                    } else {
                        context.getSource().sendFailure(players.size() > 1 ? Component.translatable("command.vampiricageing.failed_to_execute.players.wrong_faction", player.getDisplayName()) : Component.translatable("command.vampiricageing.failed_to_execute.wrong_faction"));
                    }
                } else {
                    context.getSource().sendFailure(players.size() > 1 ? Component.translatable("command.vampiricageing.failed_to_execute.players", player.getDisplayName()) : Component.translatable("command.vampiricageing.failed_to_execute"));
                }
            }
        return 0;
    }

}
