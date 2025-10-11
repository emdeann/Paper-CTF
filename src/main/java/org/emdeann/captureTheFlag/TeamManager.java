package org.emdeann.captureTheFlag;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * Manager class for team-related functions. Instantiated by {@link GameManager} and reused between
 * games.
 */
public class TeamManager {
  private final Map<CTFTeam, Team> teams;

  public TeamManager() {
    teams =
        Map.of(
            CTFTeam.RED, new Team(CTFTeam.RED),
            CTFTeam.BLUE, new Team(CTFTeam.BLUE));
  }

  /**
   * Adds a player to the specified team. Assumes that the player is not already on another team.
   *
   * @param player the player being assigned to a team
   * @param team the team to add the player to
   */
  public void addPlayerToTeam(Player player, CTFTeam team) {
    teams.get(team).addPlayer(player);
  }

  /**
   * Removes a player from their team. If a player is somehow on multiple teams, removes them from
   * all teams.
   *
   * @param player the player to remove
   */
  public void removePlayer(Player player) {
    for (Team team : teams.values()) {
      team.removePlayer(player);
    }
  }

  public Team getPlayerTeam(Player player) {
    return teams.values().stream().filter(team -> team.hasPlayer(player)).findFirst().orElseThrow();
  }

  /**
   * Determines if a player is on any team.
   *
   * @param player the player to check
   * @return whether the player is assigned to a team
   */
  public boolean isParticipating(Player player) {
    return teams.values().stream().anyMatch(team -> team.hasPlayer(player));
  }

  /**
   * Sets the flag location for the specified team. The flag will spawn at the provided location
   * when the game starts.
   *
   * <p>The location is also considered the "base" of the team for flag returns and player spawns.
   *
   * @param location The location to use
   * @param team The team for which to set the flag location
   */
  public void setFlagLocationForTeam(Location location, CTFTeam team) {
    teams.get(team).setBaseLocation(location);
  }

  /** Places each team's flag at their base location. */
  public void placeFlags() {
    for (Team team : teams.values()) {
      team.getFlag().ifPresent(Flag::place);
    }
  }

  /** Removes each team's flag at the end of the game. */
  public void removeFlags() {
    for (Team team : teams.values()) {
      team.getFlag().ifPresent(Flag::remove);
    }
  }

  /**
   * Determines if the specified player is near enough to their own base to capture a flag.
   *
   * <p>Does not check if the player is actually carrying a flag.
   *
   * @param player the player to check
   * @return if the player is near enough to their base to capture a flag
   */
  public boolean canCaptureFlag(Player player) {
    return teams.values().stream()
        .anyMatch(team -> team.hasPlayer(player) && team.isNearBase(player));
  }

  /**
   * If the block provided is a flag collectable or returnable by the player, it is returned.
   *
   * @param block the block to check
   * @param player the player attempting to obtain the flag
   * @param returnFlag true when testing if the flag is returnable (i.e. if the player matches the
   *     team of the flag)
   * @return the obtainable flag, if it exists
   */
  public Optional<Flag> getObtainableFlag(Block block, Player player, boolean returnFlag) {
    if (!isParticipating(player)) {
      return Optional.empty();
    }

    return teams.values().stream()
        .filter(team -> team.hasPlayer(player) == returnFlag)
        .map(Team::getFlag)
        .flatMap(Optional::stream)
        .filter(
            flag -> flag.getLocation().getBlock().equals(block) && !(returnFlag && flag.isAtBase()))
        .findFirst();
  }

  /**
   * Determines if the teams are prepared for the game to start. This is true if all teams have
   * players and a flag set.
   *
   * @return if the game can be started
   */
  public boolean canStartGame() {
    return teams.values().stream()
        .allMatch(team -> team.playerCount() > 0 && team.getFlag().isPresent());
  }

  /**
   * @return a collection of all teams in the game.
   */
  public Collection<Team> getTeams() {
    return teams.values();
  }
}
