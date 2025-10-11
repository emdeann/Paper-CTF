package org.emdeann.captureTheFlag.Commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import java.util.Locale;
import org.bukkit.entity.Player;
import org.emdeann.captureTheFlag.CTFTeam;
import org.emdeann.captureTheFlag.TeamManager;

public class JoinTeamCommand {

  public static LiteralArgumentBuilder<CommandSourceStack> createCommand(TeamManager teamManager) {
    LiteralArgumentBuilder<CommandSourceStack> commandTree = Commands.literal("join");
    for (CTFTeam teamColor : CTFTeam.values()) {
      commandTree =
          commandTree.then(
              Commands.literal(String.valueOf(teamColor).toLowerCase(Locale.ROOT))
                  .requires(sender -> sender.getExecutor() instanceof Player)
                  .executes(ctx -> runJoinTeamCommand(ctx, teamManager, teamColor)));
    }
    return commandTree;
  }

  private static int runJoinTeamCommand(
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
