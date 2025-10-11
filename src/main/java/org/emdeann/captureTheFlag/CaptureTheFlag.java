package org.emdeann.captureTheFlag;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;
import org.emdeann.captureTheFlag.Commands.*;

public final class CaptureTheFlag extends JavaPlugin {
  private GameManager gameManager;

  @Override
  public void onEnable() {
    TeamManager teamManager = new TeamManager();
    OutputManager outputManager = new OutputManager(teamManager);
    this.gameManager = new GameManager(this, teamManager, outputManager);
    LiteralCommandNode<CommandSourceStack> ctfCommands =
        Commands.literal("ctf")
            .then(JoinTeamCommand.createCommand(teamManager))
            .then(LeaveTeamCommand.createCommand(teamManager))
            .then(SetFlagCommand.createCommand(teamManager))
            .then(StartGameCommand.createCommand(gameManager))
            .then(StopGameCommand.createCommand(gameManager))
            .then(GetScoreCommand.createCommand(outputManager))
            .build();

    this.getLifecycleManager()
        .registerEventHandler(
            LifecycleEvents.COMMANDS, commands -> commands.registrar().register(ctfCommands));
  }

  @Override
  public void onDisable() {
    gameManager.stopGame(true);
  }
}
