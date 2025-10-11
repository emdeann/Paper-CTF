package org.emdeann.captureTheFlag;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

public class OutputManager {
  private final Scoreboard scoreboard;
  private final TeamManager teamManager;
  private final Objective scoreObjective;
  private static final Map<CTFTeam, String> teamColors =
      Map.of(
          CTFTeam.RED, "§4",
          CTFTeam.BLUE, "§1");

  public OutputManager(TeamManager teamManager) {
    this.teamManager = teamManager;
    this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
    Collection<Team> teams = teamManager.getTeams();
    this.scoreObjective =
        scoreboard.registerNewObjective(
            "scores",
            Criteria.DUMMY,
            Component.text("Capture The Flag!").color(TextColor.color(0x8300FF)));
    scoreObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
    for (Team team : teams) {
      Score teamScore =
          scoreObjective.getScore(
              teamColors.get(team.getTeamColor()) + getTeamDisplayName(team) + ":");
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

  private void sendMessageToPlayers(String message) {
    for (Team team : teamManager.getTeams()) {
      team.getPlayers().forEach(player -> player.sendMessage(Component.text(message)));
    }
  }

  private void updateScore(Team team) {
    this.scoreObjective
        .getScore(teamColors.get(team.getTeamColor()) + getTeamDisplayName(team) + ":")
        .setScore(team.getScore());
  }

  private String getTeamDisplayName(Team team) {
    return toTitleCase(String.valueOf(team.getTeamColor()));
  }

  public void onFlagPickup(Player player, Team flagTeam) {
    sendMessageToPlayers(
        player.getName() + " has picked up " + getTeamDisplayName(flagTeam) + "'s flag!");
  }

  public void onFlagDrop(Player player, Team flagTeam) {
    sendMessageToPlayers(
        player.getName() + " has dropped " + getTeamDisplayName(flagTeam) + "'s flag!");
  }

  public void onFlagReturn(Player player) {
    sendMessageToPlayers(player.getName() + " returned their team's flag!");
  }

  public void onFlagCapture(Team captureTeam, Team flagTeam) {
    sendMessageToPlayers(
        getTeamDisplayName(captureTeam)
            + " has captured "
            + getTeamDisplayName(flagTeam)
            + "'s flag!");
    this.updateScore(captureTeam);
  }

  public void onTeamWin(Team winTeam) {
    sendMessageToPlayers(getTeamDisplayName(winTeam) + " has won the game!");
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
