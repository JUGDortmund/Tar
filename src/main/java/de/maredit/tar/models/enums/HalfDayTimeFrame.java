package de.maredit.tar.models.enums;

/**
 * Created by pplewa on 17.06.15.
 */
public enum HalfDayTimeFrame {
  MORNING("timeframe.morning"),
  AFTERNOON("timeframe.afternoon");

  public final String value;

  HalfDayTimeFrame(String value) {
    this.value = value;
  }

  public String get() {
    return this.value;
  }
}
