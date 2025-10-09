package org.emdeann.captureTheFlag.Events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.emdeann.captureTheFlag.TeamManager;

public class OnBlockBreakEvent implements Listener {

  private final TeamManager teamManager;

  public OnBlockBreakEvent(TeamManager teamManager) {
    this.teamManager = teamManager;
  }

  @EventHandler
  public void onBlockBreak(BlockBreakEvent event) {
    event.setCancelled(true);
  }
}
