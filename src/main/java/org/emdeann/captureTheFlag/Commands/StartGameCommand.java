package org.emdeann.captureTheFlag.Commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.emdeann.captureTheFlag.GameManager;

public class StartGameCommand {
  public static LiteralArgumentBuilder<CommandSourceStack> createCommand(GameManager gameManager) {
    return Commands.literal("start").executes(ctx -> runStartGameCommand(ctx, gameManager));
  }

  private static int runStartGameCommand(
      CommandContext<CommandSourceStack> ctx, GameManager gameManager) {
    if (!gameManager.startGame()) {
      ctx.getSource().getSender().sendPlainMessage("Unable to start game!");
    }
    return Command.SINGLE_SUCCESS;
  }
}
