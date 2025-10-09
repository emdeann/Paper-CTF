package org.emdeann.captureTheFlag;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.emdeann.captureTheFlag.Events.OnBlockBreakEvent;

public class GameManager {
  private final CaptureTheFlag plugin;
  private final TeamManager teamManager;
  private boolean gameActive;
  private final Listener[] listeners;

  public GameManager(CaptureTheFlag plugin, TeamManager teamManager) {
    this.plugin = plugin;
    this.teamManager = teamManager;
    this.listeners = new Listener[] {new OnBlockBreakEvent(teamManager)};
  }

  public boolean startGame() {
    if (gameActive || !teamManager.canStartGame()) {
      plugin.getLogger().warning("Attempted to start game with invalid state");
      return false;
    }

    gameActive = true;
    for (Listener listener : this.listeners) {
      plugin.getServer().getPluginManager().registerEvents(listener, plugin);
    }
    return true;
  }

  public boolean stopGame() {
    if (!gameActive) {
      plugin.getLogger().warning("Attempted to end with no game running");
      return false;
    }

    gameActive = false;
    for (Listener listener : this.listeners) {
      HandlerList.unregisterAll(listener);
    }

    return true;
  }
}
