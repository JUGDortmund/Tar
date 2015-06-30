package de.maredit.tar.models;

import de.maredit.tar.models.enums.HalfDayTimeFrame;
import de.maredit.tar.models.enums.State;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AccountVactionEntry implements AccountEntry {
  
  private String id;
  private double days;
  private LocalDateTime created;
  private LocalDate from;
  private LocalDate to;
  private HalfDayTimeFrame timeframe;
  private User author;
  private double balance;
  private State state;

  public AccountVactionEntry(Vacation vacation) {
    id = vacation.getId();
    days = vacation.getDays();
    created = vacation.getCreated();
    from = vacation.getFrom();
    to = vacation.getTo();
    timeframe = vacation.getTimeframe();
    author = vacation.getAuthor();
    state = vacation.getState();
  }

  @Override
  public double getDays() {
    return days;
  }

  @Override
  public LocalDateTime getCreated() {
    return created;
  }

  @Override
  public User getAuthor() {
    return author;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public LocalDate getFrom() {
    return from;
  }

  public void setFrom(LocalDate from) {
    this.from = from;
  }

  public LocalDate getTo() {
    return to;
  }

  public void setTo(LocalDate to) {
    this.to = to;
  }

  public HalfDayTimeFrame getTimeframe() {
    return timeframe;
  }

  public void setTimeframe(HalfDayTimeFrame timeframe) {
    this.timeframe = timeframe;
  }

  public void setDays(double days) {
    this.days = days;
  }

  public void setCreated(LocalDateTime created) {
    this.created = created;
  }

  public void setAuthor(User author) {
    this.author = author;
  }

  public double getBalance() {
    return balance;
  }

  public void setBalance(double balance) {
    this.balance = balance;
  }

  public State getState() {
    return state;
  }

  public void setState(State state) {
    this.state = state;
  }

}
