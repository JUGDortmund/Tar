package de.maredit.tar.models;

import java.time.LocalDateTime;

public class AccountManualEntry implements AccountEntry {

  private String type;
  private String year;
  private Vacation vacationReference;
  private double days;
  private String description;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getYear() {
    return year;
  }

  public void setYear(String year) {
    this.year = year;
  }

  public Vacation getVacationReference() {
    return vacationReference;
  }

  public void setVacationReference(Vacation vacationReference) {
    this.vacationReference = vacationReference;
  }

  public double getDays() {
    return days;
  }

  public LocalDateTime getCreated() {
    return null;
  }

  public User getAuthor() {
    return null;
  }

  public void setDays(double days) {
    this.days = days;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }
}
