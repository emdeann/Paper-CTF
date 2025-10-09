package org.emdeann.captureTheFlag.Commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.emdeann.captureTheFlag.GameManager;

public class StopGameCommand {
  public static LiteralArgumentBuilder<CommandSourceStack> createCommand(GameManager gameManager) {
    return Commands.literal("stop").executes(ctx -> runStopGameCommand(ctx, gameManager));
  }

  public static int runStopGameCommand(
      CommandContext<CommandSourceStack> ctx, GameManager gameManager) {
    if (!gameManager.stopGame()) {
      ctx.getSource().getSender().sendPlainMessage("Unable to stop game");
    }
    return Command.SINGLE_SUCCESS;
  }
}
