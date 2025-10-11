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

public class SetFlagCommand {
  public static LiteralArgumentBuilder<CommandSourceStack> createCommand(TeamManager teamManager) {
    LiteralArgumentBuilder<CommandSourceStack> commandTree = Commands.literal("setflag");
    for (CTFTeam teamColor : CTFTeam.values()) {
      commandTree =
              commandTree.then(
                      Commands.literal(String.valueOf(teamColor).toLowerCase(Locale.ROOT))
                              .requires(sender -> sender.getExecutor() instanceof Player player && player.isOp())
                              .executes(ctx -> runSetFlagCommand(ctx, teamManager, teamColor)));
    }
    return commandTree;
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
