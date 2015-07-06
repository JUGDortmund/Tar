package de.maredit.tar.models;

import de.maredit.tar.data.User;
import de.maredit.tar.data.Vacation;
import de.maredit.tar.models.enums.ManualEntryType;

import java.time.LocalDateTime;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

public class AccountManualEntry implements AccountEntry {

  @NotNull
  private ManualEntryType type;

  @NotNull
  private String year;

  private Vacation vacation;

  @DecimalMin("0.5")
  private double days;

  @NotNull
  private String description;

  private User author;

  private LocalDateTime created;


  public ManualEntryType getType() {
    return type;
  }

  public void setType(ManualEntryType type) {
    this.type = type;
  }

  public String getYear() {
    return year;
  }

  public void setYear(String year) {
    this.year = year;
  }

  public Vacation getVacation() {
    return vacation;
  }

  public void setVacation(Vacation vacation) {
    this.vacation = vacation;
  }

  public double getDays() {
    return days;
  }

  public LocalDateTime getCreated() {
    return created;
  }

  public void setCreated(LocalDateTime created) {
    this.created = created;
  }

  public User getAuthor() {
    return author;
  }

  public void setAuthor(User author) {
    this.author = author;
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
