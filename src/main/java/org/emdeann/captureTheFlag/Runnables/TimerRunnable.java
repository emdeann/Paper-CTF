package org.emdeann.captureTheFlag.Runnables;

import org.bukkit.scheduler.BukkitRunnable;
import org.emdeann.captureTheFlag.GameManager;
import org.emdeann.captureTheFlag.OutputManager;

public class TimerRunnable extends BukkitRunnable {
  private final OutputManager outputManager;
  private final GameManager gameManager;
  private int timer;

  public TimerRunnable(GameManager gameManager, OutputManager outputManager, int timerLength) {
    this.gameManager = gameManager;
    this.outputManager = outputManager;
    this.timer = timerLength;
  }

  @Override
  public void run() {
    if (timer > 0) {
      outputManager.onTimerTick(timer);
      timer--;
      return;
    }

    outputManager.onTimeOut();
    gameManager.stopGame(false);
  }
}
