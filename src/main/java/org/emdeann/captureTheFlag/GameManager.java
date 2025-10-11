package org.emdeann.captureTheFlag;

import io.papermc.paper.util.Tick;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
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
  private final OutputManager outputManager;
  private boolean gameActive;
  private final Listener[] listeners;
  private Map<Player, Team> flagCarriers;
  private @Nullable BukkitTask gameTimer;

  private static final int SCORE_TO_WIN = 3;

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

    this.gameTimer =
        new BukkitRunnable() {
          @Override
          public void run() {
            outputManager.onTimeOut();
            stopGame(false);
          }
        }.runTaskLater(this.plugin, Tick.tick().fromDuration(Duration.ofMinutes(10)));

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
   * @param flagTeam the team with the flag being picked up
   */
  public void onFlagPickup(Player player, Team flagTeam) {
    this.flagCarriers.put(player, flagTeam);
    player.addPotionEffect(
        new PotionEffect(PotionEffectType.GLOWING, PotionEffect.INFINITE_DURATION, 1));
    flagTeam.pickUpFlag();
    outputManager.onFlagPickup(player, flagTeam);
  }

  /**
   * Handles when a player should drop a flag which they are holding. Assumes that the player is
   * holding a flag.
   *
   * @param player the player dropping the flag
   */
  public void onFlagDrop(Player player) {
    Team flagTeam = this.flagCarriers.get(player);
    removeCarrier(player);
    flagTeam.placeFlag(player.getLocation());
    outputManager.onFlagDrop(player, flagTeam);
  }

  /**
   * Handles when a flag should be returned to its base.
   *
   * @param returnTeam the team with flag being returned
   */
  public void onFlagReturn(Team returnTeam, Player returner) {
    returnTeam.returnFlag();
    outputManager.onFlagReturn(returner);
  }

  public void onFlagCapture(Player capturePlayer) {
    Team flagTeam = this.flagCarriers.get(capturePlayer);
    removeCarrier(capturePlayer);
    flagTeam.returnFlag();
    Team captureTeam = teamManager.getPlayerTeam(capturePlayer);
    captureTeam.incrementScore();
    outputManager.onFlagCapture(captureTeam, flagTeam);
    if (captureTeam.getScore() >= SCORE_TO_WIN) {
      this.stopGame(false);
    }
  }

  /**
   * @param player the player to check
   * @return if the player is carrying a flag
   */
  public boolean playerHasFlag(Player player) {
    return this.flagCarriers.containsKey(player);
  }

  /**
   * Stops the game and resets the game state.
   *
   * @param force if true, reset the game without game end output
   * @return if the game could be stopped (i.e. was active)
   */
  public boolean stopGame(boolean force) {
    if (!gameActive) {
      plugin.getLogger().warning("Attempted to end with no game running");
      return false;
    }

    if (!force) {
      List<Team> winners = teamManager.getWinners();
      if (winners.size() == 1) {
        outputManager.onTeamWin(winners.getFirst());
      } else {
        outputManager.onDraw(winners);
      }
    }

    gameActive = false;
    if (gameTimer != null) {
      gameTimer.cancel();
    }
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

  private void removeCarrier(Player carrier) {
    this.flagCarriers.remove(carrier);
    carrier.removePotionEffect(PotionEffectType.GLOWING);
  }
}
