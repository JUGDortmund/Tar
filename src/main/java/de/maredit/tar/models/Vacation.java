package de.maredit.tar.models;

import java.time.LocalDate;
import java.util.Objects;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import de.maredit.tar.models.enums.State;

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

  @DecimalMin("0.5")
  private float days;

  @Min(0)
  private float daysLeft;

  @NotNull
  private State state;

  private User author;

  public Vacation() {
    this.created = LocalDate.now();
    this.state = State.WAITING_FOR_APPROVEMENT;
  }

  public Vacation(User user, LocalDate from, LocalDate to, User substitute, User manager,
      float days, float daysLeft) {
    this.user = user;
    this.from = from;
    this.to = to;
    this.substitute = substitute;
    this.manager = manager;
    this.days = days;
    this.daysLeft = daysLeft;

    this.state = substitute != null ? State.REQUESTED_SUBSTITUTE : State.WAITING_FOR_APPROVEMENT;
    this.created = LocalDate.now();
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public State getState() {
    return state;
  }

  public void setState(State state) {
    this.state = state;
  }

  public float getDaysLeft() {
    return daysLeft;
  }

  public void setDaysLeft(float daysLeft) {
    this.daysLeft = daysLeft;
  }

  public float getDays() {
    return days;
  }

  public void setDays(float days) {
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
    this.state = substitute != null ? State.REQUESTED_SUBSTITUTE : State.WAITING_FOR_APPROVEMENT;
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

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Vacation other = (Vacation) obj;
    return Objects.equals(this.id, other.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(this.id);
  }

  @Override
  public String toString() {
    return "Vacation [from=" + from + ", to=" + to + ", created=" + created + ", user=" + user
        + ", substitute=" + substitute + ", manager=" + manager + ", days=" + days + ", daysLeft="
        + daysLeft + ", state=" + state + "]";
  }

  public void setAuthor(User user) {
    this.author = user;
  }
  
  public User getAuthor() {
    return author;
  }
}
