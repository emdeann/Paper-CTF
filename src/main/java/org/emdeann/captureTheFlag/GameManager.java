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

/**
 * Manager for general game functions. Handles game state and events, such as pickups and captures.
 * Team-specific functionality is delegated to {@link TeamManager}.
 */
public class GameManager {
  private final CaptureTheFlag plugin;
  private final TeamManager teamManager;
  private OutputManager outputManager;
  private boolean gameActive;
  private final Listener[] listeners;
  private Map<Player, Flag> flagCarriers;

  public GameManager(CaptureTheFlag plugin, TeamManager teamManager, OutputManager outputManager) {
    this.plugin = plugin;
    this.teamManager = teamManager;
    this.outputManager = outputManager;
    this.listeners =
        new Listener[] {
          new BlockBreakListener(this, teamManager),
          new PlayerRemoveListener(this),
          new PlayerMoveListener(this, teamManager),
        };
  }

  /**
   * Starts the game if it is possible. For the game to start, it must not already be active and
   * {@link TeamManager#canStartGame} must return true.
   *
   * @return if the game started successfully
   */
  public boolean startGame() {
    if (gameActive || !teamManager.canStartGame()) {
      plugin.getLogger().warning("Attempted to start game with invalid state");
      return false;
    }

    gameActive = true;
    this.flagCarriers = new HashMap<>();
    this.outputManager.onGameStart();
    teamManager.placeFlags();
    for (Listener listener : this.listeners) {
      plugin.getServer().getPluginManager().registerEvents(listener, plugin);
    }
    return true;
  }

  /**
   * Handles when a player is removed from the game, such as through a kick or quit.
   *
   * @param player the player who was removed
   */
  public void onPlayerRemove(Player player) {
    if (this.flagCarriers.containsKey(player)) {
      this.onFlagDrop(player);
    }
  }

  /**
   * Handles when a flag should be picked up by a player.
   *
   * @param player the player picking up the flag
   * @param flag the flag being picked up
   */
  public void onFlagPickup(Player player, Flag flag) {
    this.flagCarriers.put(player, flag);
    player.addPotionEffect(
        new PotionEffect(PotionEffectType.GLOWING, PotionEffect.INFINITE_DURATION, 1));
    flag.pickUp();
  }

  /**
   * Handles when a player should drop a flag which they are holding. Assumes that the player is
   * holding a flag.
   *
   * @param player the player dropping the flag
   */
  public void onFlagDrop(Player player) {
    Flag flag = this.flagCarriers.get(player);
    this.flagCarriers.remove(player);
    player.removePotionEffect(PotionEffectType.GLOWING);
    flag.place(player.getLocation());
  }

  /**
   * Handles when a flag should be returned to its base.
   *
   * @param flag the flag being returned
   */
  public void onFlagReturn(Flag flag) {
    flag.returnToBase();
  }

  /**
   * Stops the game and resets the game state.
   *
   * @return if the game could be stopped (i.e. was active)
   */
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
    this.outputManager.onGameStop();
    for (Listener listener : this.listeners) {
      HandlerList.unregisterAll(listener);
    }

    return true;
  }
}
