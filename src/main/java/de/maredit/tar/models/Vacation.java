package de.maredit.tar.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import java.time.LocalDate;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class Vacation {

  @Id
  private String id;

  @NotNull
  @DateTimeFormat(iso = ISO.DATE, pattern = "dd.MM.yyyy")
  private LocalDate from;

  @NotNull
  @DateTimeFormat(iso = ISO.DATE, pattern = "dd.MM.yyyy")
  private LocalDate to;

  @NotNull
  @DateTimeFormat(iso = ISO.DATE, pattern = "dd.MM.yyyy")
  private LocalDate created;

  @DBRef
  @NotNull
  private User user;

  @DBRef
  private User substitute;

  @DBRef
  @NotNull
  private User manager;

  @Min(0)
  private int days;

  @Min(0)
  private int daysLeft;

  @NotNull
  private State state;

  public Vacation() {
    this.created = LocalDate.now();
    this.state = State.WAITING_FOR_APPROVEMENT;
  }

  public Vacation(User user, LocalDate from, LocalDate to, User substitute, User manager,
                  int days, int daysLeft) {
    this.user = user;
    this.from = from;
    this.to = to;
    this.substitute = substitute;
    this.manager = manager;
    this.days = days;
    this.daysLeft = daysLeft;

    this.state =
        substitute != null ? State.REQUESTED_SUBSTITUTE : State.WAITING_FOR_APPROVEMENT;
    this.created = LocalDate.now();
  }

  public State getState() {
    return state;
  }

  public void setState(State state) {
    this.state = state;
  }

  public int getDaysLeft() {
    return daysLeft;
  }

  public void setDaysLeft(int amountLeft) {
    this.daysLeft = amountLeft;
  }

  public int getDays() {
    return days;
  }

  public void setDays(int days) {
    this.days = days;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public User getManager() {
    return manager;
  }

  public void setManager(User manager) {
    this.manager = manager;
  }

  public User getSubstitute() {
    return substitute;
  }

  public void setSubstitute(User substitute) {
    this.substitute = substitute;
    this.state =
        substitute != null ? State.REQUESTED_SUBSTITUTE : State.WAITING_FOR_APPROVEMENT;
  }

  public LocalDate getTo() {
    return to;
  }

  public void setTo(LocalDate to) {
    this.to = to;
  }

  public LocalDate getFrom() {
    return from;
  }

  public void setFrom(LocalDate from) {
    this.from = from;
  }

  public LocalDate getCreated() {
    return created;
  }

  public void setCreated(LocalDate created) {
    this.created = created;
  }
}
