package org.emdeann.captureTheFlag.Commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;
import org.emdeann.captureTheFlag.TeamManager;

public class LeaveTeamCommand {

  public static LiteralArgumentBuilder<CommandSourceStack> createCommand(TeamManager teamManager) {
    return Commands.literal("leave").executes(ctx -> runLeaveTeamCommand(ctx, teamManager));
  }

  private static int runLeaveTeamCommand(
      CommandContext<CommandSourceStack> ctx, TeamManager teamManager) {
    if (!(ctx.getSource().getExecutor() instanceof Player player)) {
      return Command.SINGLE_SUCCESS;
    }

    if (!teamManager.isParticipating(player)) {
      player.sendPlainMessage("You are not on a team!");
      return Command.SINGLE_SUCCESS;
    }

    teamManager.removePlayer(player);
    player.sendPlainMessage("You left your team!");
    return Command.SINGLE_SUCCESS;
  }
}
