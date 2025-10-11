package org.emdeann.captureTheFlag.Events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.emdeann.captureTheFlag.GameManager;
import org.emdeann.captureTheFlag.TeamManager;

public class PlayerRespawnListener implements Listener {
  private final GameManager gameManager;
  private final TeamManager teamManager;

  public PlayerRespawnListener(GameManager gameManager, TeamManager teamManager) {
    this.gameManager = gameManager;
    this.teamManager = teamManager;
  }

  @EventHandler
  public void onPlayerRespawn(PlayerRespawnEvent event) {
    event.setRespawnLocation(
        teamManager.getBaseLocation(event.getPlayer()).add(GameManager.UNIT_VECTOR_Y));
    gameManager.onPlayerRespawn(event.getPlayer());
  }
}
