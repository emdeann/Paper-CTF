package org.emdeann.captureTheFlag;

import java.util.ArrayList;
import java.util.Optional;
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

  /**
   * @return the flag of the team, if it has been set
   */
  private Optional<Flag> getFlag() {
    return Optional.ofNullable(flag);
  }

  public void placeFlag() {
    this.getFlag().ifPresent(Flag::place);
  }

  public void placeFlag(Location location) {
    this.getFlag().ifPresent(flag -> flag.place(location));
  }

  public void removeFlag() {
    this.getFlag().ifPresent(Flag::remove);
  }

  public boolean flagIsObtainable(Block block, boolean returnFlag) {
    return this.getFlag()
        .map(
            flag -> flag.getLocation().getBlock().equals(block) && !(returnFlag && flag.isAtBase()))
        .orElse(false);
  }

  public void pickUpFlag() {
    this.getFlag().ifPresent(Flag::pickUp);
  }

  public void returnFlag() {
    this.getFlag().ifPresent(Flag::returnToBase);
  }

  public boolean hasFlag() {
    return this.getFlag().isPresent();
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
  public ArrayList<Player> getPlayers() {
    return players;
  }
}
