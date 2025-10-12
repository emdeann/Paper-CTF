package org.emdeann.captureTheFlag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import javax.annotation.Nullable;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/** Represents a single team in a CTF game. */
public class Team {
  private final ArrayList<Player> players;
  private final CTFTeam teamColor;
  private @Nullable Flag flag;
  private int score;
  private @Nullable Location baseLocation;
  private Material originalMaterialAtBase;

  private static final int CAPTURE_DISTANCE = 3;

  public Team(CTFTeam teamColor) {
    this.players = new ArrayList<>();
    this.teamColor = teamColor;
    this.score = 0;
    this.originalMaterialAtBase = Material.AIR;
  }

  /**
   * Adds a player to the team.
   *
   * @param player the player to add
   */
  public void addPlayer(Player player) {
    players.add(player);
  }

  /**
   * Removes a player from the team.
   *
   * @param player the player to remove
   */
  public void removePlayer(Player player) {
    players.remove(player);
  }

  /**
   * Checks if this team has a particular player.
   *
   * @param player the player to check
   * @return if the player is on this team
   */
  public boolean hasPlayer(Player player) {
    return players.contains(player);
  }

  /**
   * @return the number of players on this team
   */
  public int playerCount() {
    return players.size();
  }

  /**
   * Creates a {@link Flag} object and sets its location to the one provided. This also sets the
   * "base" location for the team.
   *
   * @param location the location to use
   */
  public void setBaseLocation(Location location) {
    this.baseLocation = location;
    this.originalMaterialAtBase = location.getBlock().getType();
    this.flag =
        new Flag(location, teamColor == CTFTeam.RED ? Material.RED_WOOL : Material.BLUE_WOOL);
  }

  /**
   * Returns if a player is within capture distance of this team's base.
   *
   * @param player the player to check
   * @return if the player is near this team's base
   */
  public boolean isNearBase(Player player) {
    return this.baseLocation != null
        && (player.getLocation().toVector().distanceSquared(this.baseLocation.toVector())
            < CAPTURE_DISTANCE * CAPTURE_DISTANCE);
  }

  /** Place this team's flag at the base location, as long as it has been set. */
  public void placeFlagAtBase() {
    if (flag != null && baseLocation != null) {
      flag.place(baseLocation);
    }
  }

  /**
   * Place this team's flag at the specified location. The flag must be created through {@link
   * Team#setBaseLocation} first.
   *
   * @param location the location at which to place the flag
   */
  public void placeFlag(Location location) {
    if (flag != null) {
      flag.place(location);
    }
  }

  /** Removes the flag from the ground */
  public void removeFlag() {
    if (flag != null) {
      flag.remove();
    }
  }

  /**
   * Resets the block at this team's base to what it was originally. Important to remove bedrock if
   * game ends while a flag is picked up.
   */
  public void resetBase() {
    if (baseLocation != null) {
      this.baseLocation.getBlock().setType(this.originalMaterialAtBase);
    }
  }

  public Optional<Location> getFlagLocation() {
    if (flag == null) {
      return Optional.empty();
    }

    return Optional.of(flag.getLocation().clone());
  }

  /**
   * Picks the flag up by removing it. If the flag was at the team's base, replaces it with bedrock
   * to demonstrate that it is missing.
   */
  public void pickUpFlag() {
    if (flag == null) {
      return;
    }

    if (flagAtBase()) {
      flag.getLocation().getBlock().setType(Material.BEDROCK);
    } else {
      flag.remove();
    }
  }

  /** Removes the flag from its current location and places it at the team's base. */
  public void returnFlag() {
    this.removeFlag();
    this.placeFlag(this.baseLocation);
  }

  /**
   * @return if the flag and base exist, and the flag is at the base location
   */
  public boolean flagAtBase() {
    return this.flag != null
        && this.baseLocation != null
        && this.flag.getLocation().toVector().equals(baseLocation.toVector());
  }

  /**
   * @return if the flag has been created through {@link Team#setBaseLocation}
   */
  public boolean hasFlag() {
    return this.flag != null;
  }

  /**
   * @return this team's score
   */
  public int getScore() {
    return score;
  }

  /** Increment this team's score when a capture occurs. */
  public void incrementScore() {
    score++;
  }

  /**
   * @return the color of this team
   */
  public CTFTeam getTeamColor() {
    return this.teamColor;
  }

  /**
   * @return the players on this team
   */
  public Collection<Player> getPlayers() {
    return Collections.unmodifiableCollection(players);
  }

  /** Set this team's score to zero */
  public void resetScore() {
    this.score = 0;
  }
}
