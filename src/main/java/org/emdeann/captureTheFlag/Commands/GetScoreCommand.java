package org.emdeann.captureTheFlag.Commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.emdeann.captureTheFlag.OutputManager;

public class GetScoreCommand {
  public static LiteralArgumentBuilder<CommandSourceStack> createCommand(
      OutputManager outputManager) {
    return Commands.literal("score").executes(ctx -> runGetScoreCommand(ctx, outputManager));
  }

  private static int runGetScoreCommand(
      CommandContext<CommandSourceStack> ctx, OutputManager outputManager) {
    ctx.getSource().getSender().sendMessage(outputManager.getScoreText());
    return Command.SINGLE_SUCCESS;
  }
}
