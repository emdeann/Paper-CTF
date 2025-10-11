package org.emdeann.captureTheFlag;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.emdeann.captureTheFlag.Events.BlockBreakListener;
import org.emdeann.captureTheFlag.Events.PlayerMoveListener;
import org.emdeann.captureTheFlag.Events.PlayerRemoveListener;

public class GameManager {
  private final CaptureTheFlag plugin;
  private final TeamManager teamManager;
  private boolean gameActive;
  private final Listener[] listeners;
  private Map<Player, Flag> flagCarriers;

  public GameManager(CaptureTheFlag plugin, TeamManager teamManager) {
    this.plugin = plugin;
    this.teamManager = teamManager;
    this.listeners =
        new Listener[] {
          new BlockBreakListener(this, teamManager),
          new PlayerRemoveListener(this),
          new PlayerMoveListener(this, teamManager),
        };
  }

  public boolean startGame() {
    if (gameActive || !teamManager.canStartGame()) {
      plugin.getLogger().warning("Attempted to start game with invalid state");
      return false;
    }

    gameActive = true;
    this.flagCarriers = new HashMap<>();
    teamManager.placeFlags();
    for (Listener listener : this.listeners) {
      plugin.getServer().getPluginManager().registerEvents(listener, plugin);
    }
    return true;
  }

  public void onPlayerRemove(Player player) {
    if (this.flagCarriers.containsKey(player)) {
      this.onFlagDrop(player);
    }
  }

  public void onFlagPickup(Player player, Flag flag) {
    this.flagCarriers.put(player, flag);
    player.addPotionEffect(
        new PotionEffect(PotionEffectType.GLOWING, PotionEffect.INFINITE_DURATION, 1));
    flag.pickUp();
  }

  public void onFlagDrop(Player player) {
    Flag flag = this.flagCarriers.get(player);
    this.flagCarriers.remove(player);
    player.removePotionEffect(PotionEffectType.GLOWING);
    flag.place(player.getLocation());
  }

  public void onFlagReturn(Flag flag) {
    flag.returnToBase();
  }

  public boolean stopGame() {
    if (!gameActive) {
      plugin.getLogger().warning("Attempted to end with no game running");
      return false;
    }

    gameActive = false;
    this.flagCarriers
        .keySet()
        .forEach(player -> player.removePotionEffect(PotionEffectType.GLOWING));
    this.flagCarriers.clear();
    this.teamManager.removeFlags();
    for (Listener listener : this.listeners) {
      HandlerList.unregisterAll(listener);
    }

    return true;
  }
}
