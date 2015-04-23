package de.maredit.tar.models;

public enum State {
  REQUESTED_SUBSTITUTE("pending"),
  WAITING_FOR_APPROVEMENT("pending"),
  APPROVED("approved"),
  REJECTED("rejected"),
  ERROR("error");
  
  private final String value;

  State (String value) {
      this.value = value;
  }

  public String get() {
      return this.value;
  }
}