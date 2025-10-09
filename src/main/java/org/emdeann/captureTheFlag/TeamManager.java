package org.emdeann.captureTheFlag;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Map;

public class TeamManager {
    private final Map<CTFTeam, ArrayList<Player>> teams;

    public TeamManager() {
        teams = Map.of(
            CTFTeam.RED, new ArrayList<>(),
            CTFTeam.BLUE, new ArrayList<>()
        );
    }

    public void addPlayerToTeam(Player player, CTFTeam team) {
        teams.get(team).add(player);
    }

    public ArrayList<Player> getPlayers(CTFTeam team) {
        return teams.get(team);
    }
}
