package org.emdeann.captureTheFlag.Events;

import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.emdeann.captureTheFlag.GameManager;
import org.emdeann.captureTheFlag.TeamManager;

public class PlayerMoveListener implements Listener {
  private final GameManager gameManager;
  private final TeamManager teamManager;

  public PlayerMoveListener(GameManager gameManager, TeamManager teamManager) {
    this.gameManager = gameManager;
    this.teamManager = teamManager;
  }

  @EventHandler
  public void onPlayerMove(PlayerMoveEvent event) {
    // TODO should work when touching any face of the flag
    teamManager
        .getObtainableFlag(
            event.getTo().getBlock().getRelative(BlockFace.DOWN), event.getPlayer(), true)
        .ifPresent(gameManager::onFlagReturn);
  }
}
