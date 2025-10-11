package org.emdeann.captureTheFlag.Events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.emdeann.captureTheFlag.GameManager;

public class PlayerRemoveListener implements Listener {
  private final GameManager gameManager;

  public PlayerRemoveListener(GameManager gameManager) {
    this.gameManager = gameManager;
  }

  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent event) {
    gameManager.onPlayerRemove(event.getPlayer());
  }

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    gameManager.onPlayerRemove(event.getPlayer());
  }

  @EventHandler
  public void onPlayerKick(PlayerKickEvent event) {
    gameManager.onPlayerRemove(event.getPlayer());
  }
}
