package org.emdeann.captureTheFlag;

import java.util.ArrayList;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class Team {
  private final ArrayList<Player> players;
  private Location flagLocation;
  private final CTFTeam teamColor;

  public Team(CTFTeam teamColor) {
    this.players = new ArrayList<>();
    this.flagLocation = null;
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

  public void setFlagLocation(Location location) {
    if (this.flagLocation != null) {
      this.flagLocation.getBlock().setType(Material.AIR);
    }
    this.flagLocation = location;
    this.flagLocation
        .getBlock()
        .setType(teamColor == CTFTeam.RED ? Material.RED_WOOL : Material.BLUE_WOOL);
  }
}
