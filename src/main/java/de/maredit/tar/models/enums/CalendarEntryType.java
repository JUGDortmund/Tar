package de.maredit.tar.models.enums;

/**
 * Created by phorninge on 10.06.15.
 */
public enum CalendarEntryType {

  HOLIDAY("holiday"), VACATION("vacation");

  public final String value;

  CalendarEntryType(String value) {
    this.value = value;
  }

  public String get() {
    return this.value;
  }
}
