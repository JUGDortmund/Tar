package de.maredit.tar.models.enums;

/**
 * Created by czillmann on 07.05.15.
 */
public enum FormMode {
  NEW("new"),
  EDIT("edit"),
  VIEW("view"),
  SUBSTITUTE("substitute"),
  MANAGER("manager");

  private final String value;

  FormMode (String value) {
    this.value = value;
  }

  public String get() {
    return this.value;
  }
}
