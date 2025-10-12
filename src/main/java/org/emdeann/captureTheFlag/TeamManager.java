package org.emdeann.captureTheFlag;

import java.util.*;
import org.bukkit.Location;
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
    teams.values().forEach(team -> team.removePlayer(player));
  }

  /**
   * Gets the team which a player belong to.
   *
   * @param player the player to check
   * @return the team which the player belongs to
   * @throws NoSuchElementException if the player is not participating
   */
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
    teams.values().forEach(Team::placeFlagAtBase);
  }

  /** Removes each team's flag at the end of the game. */
  public void removeFlags() {
    teams.values().forEach(Team::removeFlag);
  }

  /** Resets each team's base at the end of the game. */
  public void resetBases() {
    teams.values().forEach(Team::resetBase);
  }

  /**
   * Determines if the teams are prepared for the game to start. This is true if all teams have
   * players and a flag set.
   *
   * @return if the game can be started
   */
  public boolean canStartGame() {
    return teams.values().stream().allMatch(team -> team.playerCount() > 0 && team.hasFlag());
  }

  /**
   * @return a collection of all teams in the game.
   */
  public Collection<Team> getTeams() {
    return teams.values();
  }

  /**
   * @return the score leaders of the game. if there is no draw, will be of length 1
   */
  public List<Team> getWinners() {
    int maxScore =
        teams.values().stream()
            .max(Comparator.comparingInt(Team::getScore))
            .orElseThrow()
            .getScore();
    return teams.values().stream().filter(team -> team.getScore() == maxScore).toList();
  }

  /**
   * Gets the base location for the team the player is on
   *
   * @param player the player to check
   * @return the base location of the player's team
   */
  public Location getBaseLocation(Player player) {
    return getPlayerTeam(player).getFlagLocation().orElseThrow();
  }

  /** Reset teams for a new game */
  public void reset() {
    this.removeFlags();
    this.resetBases();
    this.resetScores();
  }

  private void resetScores() {
    teams.values().forEach(Team::resetScore);
  }
}
