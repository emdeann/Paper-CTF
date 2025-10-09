package org.emdeann.captureTheFlag;

import java.util.Map;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TeamManager {
  private final Map<CTFTeam, Team> teams;

  public TeamManager() {
    teams =
        Map.of(
            CTFTeam.RED, new Team(CTFTeam.RED),
            CTFTeam.BLUE, new Team(CTFTeam.BLUE));
  }

  public void addPlayerToTeam(Player player, CTFTeam team) {
    teams.get(team).addPlayer(player);
  }

  public void removePlayer(Player player) {
    for (Team team : teams.values()) {
      team.removePlayer(player);
    }
  }

  public boolean isParticipating(Player player) {
    return teams.values().stream().anyMatch(team -> team.hasPlayer(player));
  }

  public void setFlagLocationForTeam(Location location, CTFTeam team) {
    teams.get(team).setFlagLocation(location);
  }
}
