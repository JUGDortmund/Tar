package de.maredit.tar.models.enums;

/**
 * Created by czillmann on 07.05.15.
 */
public enum FormMode {
  NEW("new"),
  EDIT("edit-vacation"),
  VIEW("edit-vacation"),
  SUBSTITUTE_APPROVAL("approve-vacation"),
  MANAGER_APPROVAL("approve-vacation");

  public final String value;

  FormMode (String value) {
    this.value = value;
  }

  public String get() {
    return this.value;
  }
}
