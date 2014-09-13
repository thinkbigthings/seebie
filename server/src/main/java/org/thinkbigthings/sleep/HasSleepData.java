package org.thinkbigthings.sleep;

public interface HasSleepData {
  public SleepStatistics getSleepSession();
  public void setSleepSession(SleepStatistics update);
}
