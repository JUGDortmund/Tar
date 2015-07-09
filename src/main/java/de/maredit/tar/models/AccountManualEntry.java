package de.maredit.tar.models;

import de.maredit.tar.data.ManualEntry;
import de.maredit.tar.data.User;
import de.maredit.tar.data.Vacation;
import de.maredit.tar.models.enums.ManualEntryType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AccountManualEntry implements AccountEntry {

  private String id;
  private ManualEntryType type;
  private Vacation vacation;
  private double days;
  private String displayText;
  private User author;
  private double balance;
  private LocalDateTime created;
  private LocalDate from;
  private LocalDate to;

  public AccountManualEntry(ManualEntry manualEntry){
    id = manualEntry.getId();
    if(manualEntry.getType() == ManualEntryType.ADD){
      days = manualEntry.getDays();
    } else {
      days = -manualEntry.getDays();
    }
    type = manualEntry.getType();
    vacation = manualEntry.getVacation();
    displayText = manualEntry.getDescription();
    created = manualEntry.getCreated();
    author = manualEntry.getAuthor();
    from = null;
    to = null;
  }

  public boolean isStarred() {
    return false;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public ManualEntryType getType() {
    return type;
  }

  public void setType(ManualEntryType type) {
    this.type = type;
  }

  public Vacation getVacation() {
    return vacation;
  }

  public void setVacation(Vacation vacation) {
    this.vacation = vacation;
  }

  public LocalDate getFrom() {
    return null;
  }

  public LocalDate getTo() {
    return null;
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

  public String getDisplayText() {
    return displayText;
  }

  public void setDisplayText(String displayText) {
    this.displayText = displayText;
  }

  public double getBalance() {
    return balance;
  }

  public void setBalance(double balance) {
    this.balance = balance;
  }
}
