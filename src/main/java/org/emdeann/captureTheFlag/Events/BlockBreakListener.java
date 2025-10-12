package org.emdeann.captureTheFlag.Events;

import java.util.Optional;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.emdeann.captureTheFlag.GameManager;
import org.emdeann.captureTheFlag.Team;

public class BlockBreakListener implements Listener {

  private final GameManager gameManager;

  public BlockBreakListener(GameManager gameManager) {
    this.gameManager = gameManager;
  }

  /**
   * Prevents block breaking and allows flags to be picked up when broken by a player on a different
   * team.
   */
  @EventHandler
  public void onBlockBreak(BlockBreakEvent event) {
    Optional<Team> flagTeam = gameManager.getObtainableFlag(event.getPlayer(), event.getBlock());
    flagTeam.ifPresent(team -> gameManager.onFlagPickup(event.getPlayer(), team));
  }
}
