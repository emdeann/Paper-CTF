package org.emdeann.captureTheFlag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import javax.annotation.Nullable;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/** Represents a single team in a CTF game. */
public class Team {
  private final ArrayList<Player> players;
  private final CTFTeam teamColor;
  private @Nullable Flag flag;
  private int score;
  private @Nullable Location baseLocation;

  private static final int CAPTURE_DISTANCE = 3;

  public Team(CTFTeam teamColor) {
    this.players = new ArrayList<>();
    this.teamColor = teamColor;
    this.score = 0;
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

  public void placeFlag() {
    if (flag != null) {
      flag.place();
    }
  }

  public void placeFlag(Location location) {
    if (flag != null) {
      flag.place(location);
    }
  }

  public void removeFlag() {
    if (flag != null) {
      flag.remove();
    }
  }

  /**
   * Determines if this team's flag is obtainable, defined by the flag occupying the passed block.
   * If the flag is being returned, it must not already be at the team's base.
   *
   * @param block the block to check
   * @param returnFlag whether the flag is being returned
   * @return if the flag is obtainable at the block provided
   */
  public boolean flagIsObtainable(Block block, boolean returnFlag) {
    if (flag == null || returnFlag && flagAtBase()) {
      return false;
    }

    return flag.getLocation().getBlock().equals(block);
  }

  public void pickUpFlag() {
    if (flag == null) {
      return;
    }

    // If the flag is home, replace it with bedrock to demonstrate that it is missing
    if (flagAtBase()) {
      flag.getLocation().getBlock().setType(Material.BEDROCK);
    } else {
      flag.remove();
    }
  }

  public void returnFlag() {
    this.removeFlag();
    this.placeFlag(this.baseLocation);
  }

  public boolean flagAtBase() {
    return this.flag != null
        && this.baseLocation != null
        && this.flag.getLocation().toVector().equals(baseLocation.toVector());
  }

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
}
