package de.maredit.tar.models.enums;

public enum State {
  REQUESTED_SUBSTITUTE("pending-substitute"), WAITING_FOR_APPROVEMENT("pending-approvement"), APPROVED("approved"), REJECTED(
      "rejected"), ERROR("error"), CANCELED("canceled");

  private final String value;

  State(String value) {
    this.value = value;
  }

  public String get() {
    return this.value;
  }
}
