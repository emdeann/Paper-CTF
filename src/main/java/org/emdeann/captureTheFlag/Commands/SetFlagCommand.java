package org.emdeann.captureTheFlag.Commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;
import org.emdeann.captureTheFlag.CTFTeam;
import org.emdeann.captureTheFlag.TeamManager;

public class SetFlagCommand {
  public static LiteralArgumentBuilder<CommandSourceStack> createCommand(TeamManager teamManager) {
    return Commands.literal("setflag")
        .then(
            Commands.literal("red")
                .requires(sender -> sender.getExecutor() instanceof Player player && player.isOp())
                .executes(ctx -> runSetFlagCommand(ctx, teamManager, CTFTeam.RED)))
        .then(
            Commands.literal("blue")
                .requires(sender -> sender.getExecutor() instanceof Player player && player.isOp())
                .executes(ctx -> runSetFlagCommand(ctx, teamManager, CTFTeam.BLUE)));
  }

  public static int runSetFlagCommand(
      CommandContext<CommandSourceStack> ctx, TeamManager teamManager, CTFTeam team) {
    if (!(ctx.getSource().getExecutor() instanceof Player player)) {
      return Command.SINGLE_SUCCESS;
    }

    teamManager.setFlagLocationForTeam(player.getLocation(), team);
    player.sendPlainMessage("Flag location set!");
    return Command.SINGLE_SUCCESS;
  }
}
