package org.emdeann.captureTheFlag;

import java.util.Map;
import java.util.Optional;
import org.bukkit.Location;
import org.bukkit.block.Block;
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

  public Optional<Team> getPlayerTeam(Player player) {
    for (Team team : teams.values()) {
      if (team.hasPlayer(player)) {
        return Optional.of(team);
      }
    }

    return Optional.empty();
  }

  public boolean isParticipating(Player player) {
    return teams.values().stream().anyMatch(team -> team.hasPlayer(player));
  }

  public void setFlagLocationForTeam(Location location, CTFTeam team) {
    teams.get(team).setFlag(location);
  }

  public void placeFlags() {
    for (Team team : teams.values()) {
      team.getFlag().ifPresent(Flag::place);
    }
  }

  public void removeFlags() {
    for (Team team : teams.values()) {
      team.getFlag().ifPresent(Flag::remove);
    }
  }

  public Optional<Flag> getObtainableFlag(Block block, Player player, boolean returnFlag) {
    if (!isParticipating(player)) {
      return Optional.empty();
    }

    return teams.values().stream()
        .filter(team -> team.hasPlayer(player) == returnFlag)
        .map(Team::getFlag)
        .flatMap(Optional::stream)
        .filter(flag -> flag.getLocation().getBlock().equals(block))
        .findFirst();
  }

  public boolean canStartGame() {
    return teams.values().stream()
        .allMatch(team -> team.playerCount() > 0 && team.getFlag().isPresent());
  }
}
