package org.emdeann.captureTheFlag;

import java.util.ArrayList;
import java.util.Optional;
import javax.annotation.Nullable;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class Team {
  private final ArrayList<Player> players;
  private final CTFTeam teamColor;
  private @Nullable Flag flag;

  public Team(CTFTeam teamColor) {
    this.players = new ArrayList<>();
    this.teamColor = teamColor;
  }

  public void addPlayer(Player player) {
    players.add(player);
  }

  public void removePlayer(Player player) {
    players.remove(player);
  }

  public boolean hasPlayer(Player player) {
    return players.contains(player);
  }

  public int playerCount() {
    return players.size();
  }

  public void setFlag(Location location) {
    this.flag =
        new Flag(location, teamColor == CTFTeam.RED ? Material.RED_WOOL : Material.BLUE_WOOL);
  }

  public Optional<Flag> getFlag() {
    return Optional.ofNullable(flag);
  }
}
