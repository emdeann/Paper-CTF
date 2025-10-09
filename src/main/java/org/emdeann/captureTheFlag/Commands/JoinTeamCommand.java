package org.emdeann.captureTheFlag.Commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;
import org.emdeann.captureTheFlag.CTFTeam;
import org.emdeann.captureTheFlag.TeamManager;

public class JoinTeamCommand {

  public static LiteralArgumentBuilder<CommandSourceStack> createCommand(TeamManager teamManager) {
    return Commands.literal("join")
        .then(
            Commands.literal("red")
                .requires(sender -> sender.getExecutor() instanceof Player)
                .executes(ctx -> addPlayerCommand(ctx, teamManager, CTFTeam.RED)))
        .then(
            Commands.literal("blue")
                .requires(sender -> sender.getExecutor() instanceof Player)
                .executes(ctx -> addPlayerCommand(ctx, teamManager, CTFTeam.BLUE)));
  }

  private static int addPlayerCommand(
      CommandContext<CommandSourceStack> ctx, TeamManager teamManager, CTFTeam team) {
    if (!(ctx.getSource().getExecutor() instanceof Player player)) {
      return Command.SINGLE_SUCCESS;
    }

    if (teamManager.isParticipating(player)) {
      player.sendPlainMessage("You are already on a team!");
      return Command.SINGLE_SUCCESS;
    }

    teamManager.addPlayerToTeam((Player) ctx.getSource().getSender(), team);
    ctx.getSource().getSender().sendPlainMessage("Joined team!");
    return Command.SINGLE_SUCCESS;
  }
}
