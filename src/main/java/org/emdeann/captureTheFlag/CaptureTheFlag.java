package org.emdeann.captureTheFlag;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;
import org.emdeann.captureTheFlag.Commands.JoinTeamCommand;
import org.emdeann.captureTheFlag.Commands.LeaveTeamCommand;
import org.emdeann.captureTheFlag.Commands.SetFlagCommand;

public final class CaptureTheFlag extends JavaPlugin {

  @Override
  public void onEnable() {
    TeamManager teamManager = new TeamManager();
    LiteralCommandNode<CommandSourceStack> ctfCommands =
        Commands.literal("ctf")
            .then(JoinTeamCommand.createCommand(teamManager))
            .then(LeaveTeamCommand.createCommand(teamManager))
            .then(SetFlagCommand.createCommand(teamManager))
            .build();

    this.getLifecycleManager()
        .registerEventHandler(
            LifecycleEvents.COMMANDS, commands -> commands.registrar().register(ctfCommands));
  }

  @Override
  public void onDisable() {
    // Plugin shutdown logic
  }
}
