package de.maredit.tar.models;

import org.springframework.data.annotation.Id;

import java.time.LocalDate;

public class Vacation {

  @Id
  private String id;

  private LocalDate from;
  private LocalDate to;
  private LocalDate created;

  private User user;
  private User substitute;
  private User manager;

  private int days;
  private int daysLeft;

  private State state;

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
        substitute != null ? State.REQUESTED_REPRESENTATION : State.WAITING_FOR_APPROVEMENT;
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
    this.daysLeft = daysLeft;
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
