package org.emdeann.captureTheFlag;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

public class OutputManager {
  private final Scoreboard scoreboard;
  private final TeamManager teamManager;
  private final Objective scoreObjective;
  private static final Map<CTFTeam, String> teamColorCodes =
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
              teamColorCodes.get(team.getTeamColor()) + getTeamDisplayName(team) + ":");
      teamScore.setScore(team.getScore());
    }
  }

  /** Presents the scoreboard and sends a message to participating players when the game starts. */
  public void onGameStart() {
    sendMessageToPlayers("The game has started!");
    for (Team team : teamManager.getTeams()) {
      this.updateScore(team);
      team.getPlayers().forEach(player -> player.setScoreboard(this.scoreboard));
    }
  }

  /** Removes the scoreboard from view when the game is stopped */
  public void onGameStop() {
    Scoreboard dummy = Bukkit.getScoreboardManager().getNewScoreboard();
    for (Team team : teamManager.getTeams()) {
      team.getPlayers().forEach(player -> player.setScoreboard(dummy));
    }
  }

  /**
   * Sends a message to all participating players.
   *
   * @param message the message to send
   */
  private void sendMessageToPlayers(String message) {
    for (Team team : teamManager.getTeams()) {
      team.getPlayers().forEach(player -> player.sendMessage(Component.text(message)));
    }
  }

  /**
   * Updates the score entry on the leaderboard for a particular team.
   *
   * @param team the team to update
   */
  private void updateScore(Team team) {
    this.scoreObjective
        .getScore(teamColorCodes.get(team.getTeamColor()) + getTeamDisplayName(team) + ":")
        .setScore(team.getScore());
  }

  /**
   * Gets a team's name, formatted in title case.
   *
   * @param team the team
   * @return the team's name, ready to be displayed
   */
  private String getTeamDisplayName(Team team) {
    return toTitleCase(String.valueOf(team.getTeamColor()));
  }

  /**
   * Display output when a team's flag is picked up.
   *
   * @param player the player who picked up the flag
   * @param flagTeam the team which the flag belongs to
   */
  public void onFlagPickup(Player player, Team flagTeam) {
    sendMessageToPlayers(
        player.getName() + " has picked up " + getTeamDisplayName(flagTeam) + "'s flag!");
  }

  /**
   * Display output when a team's flag is dropped.
   *
   * @param player the player who dropped the flag
   * @param flagTeam the team which the flag belongs to
   */
  public void onFlagDrop(Player player, Team flagTeam) {
    sendMessageToPlayers(
        player.getName() + " has dropped " + getTeamDisplayName(flagTeam) + "'s flag!");
  }

  /**
   * Display output when a flag was returned.
   *
   * @param player the player who returned the flag
   */
  public void onFlagReturn(Player player, Team flagTeam) {
    sendMessageToPlayers(
        player.getName() + " returned " + getTeamDisplayName(flagTeam) + "'s flag!");
  }

  /**
   * Display output when a flag has been captured.
   *
   * @param captureTeam the team capturing the flag
   * @param flagTeam the team whose flag was captured
   */
  public void onFlagCapture(Team captureTeam, Team flagTeam) {
    sendMessageToPlayers(
        getTeamDisplayName(captureTeam)
            + " has captured "
            + getTeamDisplayName(flagTeam)
            + "'s flag!");
    this.updateScore(captureTeam);
  }

  /**
   * Display output when a single team has won the game
   *
   * @param winTeam the team that won
   */
  public void onTeamWin(Team winTeam) {
    sendMessageToPlayers(getTeamDisplayName(winTeam) + " has won the game!");
  }

  /**
   * Display output when the game ends in a draw
   *
   * @param drawTeams the teams which tied
   */
  public void onDraw(List<Team> drawTeams) {
    String teamNames =
        drawTeams.stream().map(this::getTeamDisplayName).collect(Collectors.joining(", "));

    sendMessageToPlayers("The game has ended in a draw between " + teamNames + "!");
  }

  /** Display output when time runs out in the game */
  public void onTimeOut() {
    sendMessageToPlayers("The timer is up!");
  }

  /**
   * Updates scoreboard and displays necessary output when the timer ticks down.
   *
   * @param timer the current time in seconds left on the timer
   */
  public void onTimerTick(int timer) {
    if (timer <= 0) {
      return;
    }

    this.scoreObjective.getScore("Time Left:").setScore(timer);

    if (timer % 60 == 0) {
      int minutesLeft = timer / 60;
      String minuteStr = minutesLeft > 1 ? " minutes" : " minute";
      sendMessageToPlayers(timer / 60 + minuteStr + " left!");
    } else if ((timer <= 30 && timer % 5 == 0) || timer <= 5) {
      String secondsStr = timer > 1 ? " seconds" : " second";
      sendMessageToPlayers("The game ends in " + timer + secondsStr + "!");
    }
  }

  /**
   * Gets the current score in component form. Teams in an inactive game will display scores of
   * zero.
   *
   * @return formatted version of the current score
   */
  public TextComponent getScoreText() {
    TextComponent scoreText = Component.text("Scores:");
    for (Team team : teamManager.getTeams()) {
      scoreText =
          scoreText
              .appendNewline()
              .append(
                  Component.text(
                      teamColorCodes.get(team.getTeamColor()) + getTeamDisplayName(team)))
              .append(Component.text(": " + team.getScore()));
    }

    return scoreText;
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
