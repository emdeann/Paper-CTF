package org.emdeann.captureTheFlag;

import java.util.ArrayList;
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
  public void setFlag(Location location) {
    this.flag =
        new Flag(location, teamColor == CTFTeam.RED ? Material.RED_WOOL : Material.BLUE_WOOL);
  }

  /**
   * @return the flag of the team, if it has been set
   */
  public Optional<Flag> getFlag() {
    return Optional.ofNullable(flag);
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
