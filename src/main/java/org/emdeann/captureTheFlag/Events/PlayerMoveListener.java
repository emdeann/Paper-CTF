package org.emdeann.captureTheFlag.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.emdeann.captureTheFlag.GameManager;

public class PlayerMoveListener implements Listener {
  private final GameManager gameManager;

  public PlayerMoveListener(GameManager gameManager) {
    this.gameManager = gameManager;
  }

  /** Returns the flag if it is touched by a player on the same team. */
  @EventHandler
  public void onPlayerMove(PlayerMoveEvent event) {
    Player player = event.getPlayer();

    gameManager
        .getReturnableFlagTeam(player)
        .ifPresent(team -> gameManager.onFlagReturn(team, player));

    if (gameManager.canCaptureFlag(player)) {
      gameManager.onFlagCapture(player);
    }
  }
}
