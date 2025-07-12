package com.thedrofdoctoring.vampiricageing.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.thedrofdoctoring.vampiricageing.capabilities.AgeingManager;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class ChangeRankProgressCommand extends BasicCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {

        return Commands.literal("increase_rank_progress")
                .requires(context -> context.hasPermission(PERMISSION_LEVEL_CHEAT))
                .then(Commands.argument("rank_progress", IntegerArgumentType.integer(0))
                        .executes(context -> increaseRankProgress(context, IntegerArgumentType.getInteger(context, "rank_progress"), Lists.newArrayList(context.getSource().getPlayerOrException())))
                        .then(Commands.argument("player", EntityArgument.entities())
                                .executes(context -> increaseRankProgress(context, IntegerArgumentType.getInteger(context, "rank_Progress"), EntityArgument.getPlayers(context, "player")))));
    }
    @SuppressWarnings("SameReturnValue")
    private static int increaseRankProgress(@NotNull CommandContext<CommandSourceStack> context, int rankProgress, @NotNull Collection<ServerPlayer> players) {
        for (ServerPlayer player : players) {
            int level = FactionPlayerHandler.get(player).getCurrentLevel();
            AgeingManager manager = AgeingManager.getAge(player);
            int age = manager.getAge();
            if (age < 6 && age >= 0 && level > 0) {

                manager.increaseRankPoints(rankProgress);
                manager.sync(false);

                context.getSource().sendSuccess(() -> Component.translatable("command.vampiricageing.base.rank_progress_sucess", player.getName(), rankProgress), true);
            } else if(age > 5 || age < 0) {
                context.getSource().sendFailure(players.size() > 1 ? Component.translatable("command.vampiricageing.failed_to_execute.players.invalid_age", player.getDisplayName()) : Component.translatable("command.vampiricageing.failed_to_execute.invalid_age"));
            } else if(manager.getType() == null) {
                context.getSource().sendFailure(players.size() > 1 ? Component.translatable("command.vampiricageing.failed_to_execute.players.wrong_faction", player.getDisplayName()) : Component.translatable("command.vampiricageing.failed_to_execute.player_wrong_faction"));
            } else {
                context.getSource().sendFailure(players.size() > 1 ? Component.translatable("command.vampiricageing.failed_to_execute.players", player.getDisplayName()) : Component.translatable("command.vampiricageing.failed_to_execute"));
            }
        }
        return 0;
    }
}
