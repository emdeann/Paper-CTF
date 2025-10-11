package org.emdeann.captureTheFlag.Events;

import java.util.Optional;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.emdeann.captureTheFlag.Flag;
import org.emdeann.captureTheFlag.GameManager;
import org.emdeann.captureTheFlag.TeamManager;

public class BlockBreakListener implements Listener {

  private final GameManager gameManager;
  private final TeamManager teamManager;

  public BlockBreakListener(GameManager gameManager, TeamManager teamManager) {
    this.gameManager = gameManager;
    this.teamManager = teamManager;
  }

  @EventHandler
  public void onBlockBreak(BlockBreakEvent event) {
    Optional<Flag> flag = teamManager.getObtainableFlag(event.getBlock(), event.getPlayer(), false);
    flag.ifPresent(value -> gameManager.onFlagPickup(event.getPlayer(), value));
    // The game does not allow block breaks
    event.setCancelled(true);
  }
}
