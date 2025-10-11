package org.emdeann.captureTheFlag;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.*;

public class OutputManager {
  private final Scoreboard scoreboard;
  private final TeamManager teamManager;
  private static final Map<CTFTeam, String> teamColors =
      Map.of(
          CTFTeam.RED, "§4",
          CTFTeam.BLUE, "§1");

  public OutputManager(TeamManager teamManager) {
    this.teamManager = teamManager;
    this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
    Collection<Team> teams = teamManager.getTeams();
    Objective objective =
        scoreboard.registerNewObjective(
            "scores",
            Criteria.DUMMY,
            Component.text("Capture The Flag!").color(TextColor.color(0x8300FF)));
    objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    for (Team team : teams) {
      String teamName = toTitleCase(String.valueOf(team.getTeamColor()));

      Score teamScore = objective.getScore(teamColors.get(team.getTeamColor()) + teamName + ":");
      teamScore.setScore(team.getScore());
    }
  }

  /** Present the scoreboard to participating players when the game starts. */
  public void onGameStart() {
    for (Team team : teamManager.getTeams()) {
      team.getPlayers().forEach(player -> player.setScoreboard(this.scoreboard));
    }
  }

  /** Remove the scoreboard from view when the game is stopped */
  public void onGameStop() {
    Scoreboard dummy = Bukkit.getScoreboardManager().getNewScoreboard();
    for (Team team : teamManager.getTeams()) {
      team.getPlayers().forEach(player -> player.setScoreboard(dummy));
    }
  }

  /**
   * Returns a given string with the first letter uppercase and the others lowercase
   *
   * @param str the string to convert
   * @return the string, in title case
   */
  private String toTitleCase(String str) {
    return str.substring(0, 1).toUpperCase(Locale.ROOT) + str.substring(1).toLowerCase(Locale.ROOT);
  }
}
