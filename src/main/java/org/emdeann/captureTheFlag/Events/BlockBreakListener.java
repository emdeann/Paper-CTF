package org.emdeann.captureTheFlag.Events;

import java.util.Optional;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.emdeann.captureTheFlag.GameManager;
import org.emdeann.captureTheFlag.Team;
import org.emdeann.captureTheFlag.TeamManager;

public class BlockBreakListener implements Listener {

  private final GameManager gameManager;
  private final TeamManager teamManager;

  public BlockBreakListener(GameManager gameManager, TeamManager teamManager) {
    this.gameManager = gameManager;
    this.teamManager = teamManager;
  }

  /**
   * Prevents block breaking and allows flags to be picked up when broken by a player on a different
   * team.
   */
  @EventHandler
  public void onBlockBreak(BlockBreakEvent event) {
    Optional<Team> flagTeam =
        teamManager.getObtainableFlag(event.getBlock(), event.getPlayer(), false);
    flagTeam.ifPresent(team -> gameManager.onFlagPickup(event.getPlayer(), team));
    // The game does not allow block breaks
    event.setCancelled(true);
  }
}
