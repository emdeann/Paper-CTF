package org.emdeann.captureTheFlag;

import java.util.ArrayList;
import java.util.Map;
import org.bukkit.entity.Player;

public class TeamManager {
  private final Map<CTFTeam, ArrayList<Player>> teams;

  public TeamManager() {
    teams =
        Map.of(
            CTFTeam.RED, new ArrayList<>(),
            CTFTeam.BLUE, new ArrayList<>());
  }

  public void addPlayerToTeam(Player player, CTFTeam team) {
    teams.get(team).add(player);
  }

  public void removePlayer(Player player) {
    for (ArrayList<Player> team : teams.values()) {
      team.remove(player);
    }
  }

  public ArrayList<Player> getPlayers(CTFTeam team) {
    return teams.get(team);
  }

  public boolean isOnTeam(Player player, CTFTeam team) {
    return teams.get(team).contains(player);
  }

  public boolean isParticipating(Player player) {
    return teams.values().stream().anyMatch(team -> team.contains(player));
  }
}
