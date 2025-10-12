package org.emdeann.captureTheFlag;

import static org.emdeann.captureTheFlag.Constants.UNIT_VECTOR_Y;

import io.papermc.paper.util.Tick;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.emdeann.captureTheFlag.Events.BlockBreakListener;
import org.emdeann.captureTheFlag.Events.PlayerMoveListener;
import org.emdeann.captureTheFlag.Events.PlayerRemoveListener;
import org.emdeann.captureTheFlag.Events.PlayerRespawnListener;
import org.emdeann.captureTheFlag.Runnables.TimerRunnable;

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
  private final Map<Player, Team> flagCarriers;
  private @Nullable BukkitTask gameTimer;

  private static final int SCORE_TO_WIN = 3;
  private static final int GAME_TIMER_SECONDS = 600;
  private static final float RETURN_DISTANCE = 1.1f;
  private static final ItemStack[] DEFAULT_ITEMS =
      new ItemStack[] {
        new ItemStack(Material.STONE_SWORD),
        new ItemStack(Material.STONE_PICKAXE),
        new ItemStack(Material.COOKED_BEEF, 4),
        new ItemStack(Material.STONE, 32)
      };
  private static final ItemStack[] DEFAULT_ARMOR =
      new ItemStack[] {
        new ItemStack(Material.LEATHER_BOOTS),
        new ItemStack(Material.LEATHER_LEGGINGS),
        new ItemStack(Material.LEATHER_CHESTPLATE),
        new ItemStack(Material.LEATHER_HELMET)
      };

  public GameManager(CaptureTheFlag plugin, TeamManager teamManager, OutputManager outputManager) {
    this.plugin = plugin;
    this.teamManager = teamManager;
    this.outputManager = outputManager;
    this.flagCarriers = new HashMap<>();
    this.listeners =
        new Listener[] {
          new BlockBreakListener(this),
          new PlayerRemoveListener(this),
          new PlayerMoveListener(this),
          new PlayerRespawnListener(this, teamManager),
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
        new TimerRunnable(this, outputManager, GAME_TIMER_SECONDS)
            .runTaskTimer(this.plugin, 0, Tick.tick().fromDuration(Duration.ofSeconds(1)));

    gameActive = true;
    this.flagCarriers.clear();
    this.playerSetup();
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

  /**
   * Handles when a flag should be captured by a player. The player must be actively carrying a
   * flag.
   *
   * @param capturePlayer the player capturing the flag
   */
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

  public void onPlayerRespawn(Player player) {
    this.setPlayerInventory(player);
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
    this.teamManager.resetBases();
    this.outputManager.onGameStop();
    for (Listener listener : this.listeners) {
      HandlerList.unregisterAll(listener);
    }

    return true;
  }

  /**
   * Removes a player as a flag carrier by updating {@link GameManager#flagCarriers} and removing
   * the glowing effect.
   *
   * @param carrier the flag carrier to remove
   */
  private void removeCarrier(Player carrier) {
    this.flagCarriers.remove(carrier);
    carrier.removePotionEffect(PotionEffectType.GLOWING);
  }

  /**
   * If the block provided is a flag returnable by the player, its team is returned.
   *
   * @param player the player attempting to obtain the flag
   * @return the team of the returnable flag, if it exists
   */
  public Optional<Team> getReturnableFlag(Player player) {
    if (!teamManager.isParticipating(player)) {
      return Optional.empty();
    }

    return teamManager.getTeams().stream()
        .filter(team -> team.hasPlayer(player) && teamFlagIsReturnable(team, player))
        .findFirst();
  }

  /**
   * If the block provided is a flag collectable by the player, its team is returned.
   *
   * @param player the player attempting to obtain the flag
   * @param block the block to check for the flag
   * @return the team of the obtainable flag, if it exists
   */
  public Optional<Team> getObtainableFlag(Player player, Block block) {
    if (!teamManager.isParticipating(player)) {
      return Optional.empty();
    }

    return teamManager.getTeams().stream()
        .filter(
            team ->
                !team.hasPlayer(player)
                    && team.getFlagLocation().orElseThrow().getBlock().equals(block))
        .findFirst();
  }

  /**
   * Determines if the specified player is near enough to their own base to capture a flag.
   *
   * @param player the player to check
   * @return if the player has a flag and is near enough to their base to capture a flag
   */
  public boolean canCaptureFlag(Player player) {
    if (!this.flagCarriers.containsKey(player)) {
      return false;
    }
    return teamManager.getTeams().stream()
        .anyMatch(team -> team.hasPlayer(player) && team.isNearBase(player));
  }

  /**
   * Returns whether the player is close enough to return the given team's flag.
   *
   * @param team the team to check
   * @param player the player to check
   * @return if the player is close enough to the flag to return it
   * @throws java.util.NoSuchElementException if called when the team provided does not have a flag
   *     set
   */
  private boolean teamFlagIsReturnable(Team team, Player player) {
    if (team.flagAtBase()) {
      return false;
    }

    Vector flagLoc = team.getFlagLocation().orElseThrow().toVector();
    Vector playerLoc = player.getLocation().toVector();

    // Consider the player as touching the block if they or their head is near enough
    return flagLoc.distanceSquared(playerLoc) < RETURN_DISTANCE
        || flagLoc.distanceSquared(playerLoc.add(UNIT_VECTOR_Y)) < RETURN_DISTANCE;
  }

  private void playerSetup() {
    for (Team team : teamManager.getTeams()) {
      for (Player player : team.getPlayers()) {
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setGameMode(GameMode.SURVIVAL);
        player.clearActivePotionEffects();
        this.setPlayerInventory(player);
        player.teleport(team.getFlagLocation().orElseThrow().add(UNIT_VECTOR_Y));
      }
    }
  }

  private void setPlayerInventory(Player player) {
    player.getInventory().clear();
    player.getInventory().addItem(DEFAULT_ITEMS);
    player.getInventory().setArmorContents(DEFAULT_ARMOR);
  }
}
