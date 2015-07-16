package de.maredit.tar.models.enums;

/**
 * Created by czillmann on 06.07.15.
 */
public enum ManualEntryType {
  ADD("manualEntryType.add"),
  REDUCE("manualEntryType.reduce");

  public final String value;

  ManualEntryType(String value) {
    this.value = value;
  }

  public String get() {
    return this.value;
  }
}
