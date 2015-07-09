package de.maredit.tar.data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import de.maredit.tar.models.enums.HalfDayTimeFrame;
import de.maredit.tar.models.enums.State;

@Document
public class Vacation {

  @Id
  private String id;

  @NotNull(message="{date.from}")
  @DateTimeFormat(iso = ISO.DATE, pattern = "dd.MM.yyyy")
  private LocalDate from;

  @NotNull(message="{date.to}")
  @DateTimeFormat(iso = ISO.DATE, pattern = "dd.MM.yyyy")
  private LocalDate to;

  @NotNull
  @DateTimeFormat(iso = ISO.DATE, pattern = "dd.MM.yyyy")
  private LocalDateTime created;

  @DBRef
  @NotNull
  private User user;

  @DBRef
  private User substitute;

  @DBRef
  @NotNull(message="{manager.notnull}")
  private User manager;

  private double days;

  @NotNull
  private State state;

  @DBRef
  private User author;
  
  private String appointmentId;

  private boolean halfDay;

  private HalfDayTimeFrame timeframe;

  @DBRef
  private ManualEntry manualEntry;

  public Vacation() {
    this.created = LocalDateTime.now();
    this.state = State.WAITING_FOR_APPROVEMENT;
  }

  public Vacation(User user, LocalDate from, LocalDate to, User substitute, User manager,
      double days) {
    this.user = user;
    this.from = from;
    this.to = to;
    this.substitute = substitute;
    this.manager = manager;
    this.days = days;
    this.state = substitute != null ? State.REQUESTED_SUBSTITUTE : State.WAITING_FOR_APPROVEMENT;
    this.created = LocalDateTime.now();
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


  public double getDays() {
    return days;
  }

  public void setDays(double days) {
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

  public LocalDateTime getCreated() {
    return created;
  }

  public void setCreated(LocalDateTime created) {
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

  public void setAuthor(User user) {
    this.author = user;
  }
  
  public User getAuthor() {
    return author;
  }

  public String getAppointmentId() {
    return appointmentId;
  }

  public void setAppointmentId(String appointmentId) {
    this.appointmentId = appointmentId;
  }

  public boolean isHalfDay() {
    return halfDay;
  }

  public void setHalfDay(boolean halfDay) {
    this.halfDay = halfDay;
  }

  public HalfDayTimeFrame getTimeframe() {
    return timeframe;
  }

  public void setTimeframe(HalfDayTimeFrame timeframe) {
    this.timeframe = timeframe;
  }

  public ManualEntry getManualEntry() {
    return manualEntry;
  }

  public void setManualEntry(ManualEntry manualEntry) {
    this.manualEntry = manualEntry;
  }

  @Override
  public String toString() {
    return "Vacation [from=" + from + ", to=" + to + ", created=" + created + ", user=" + user
        + ", substitute=" + substitute + ", manager=" + manager + ", days=" + days + ", state=" + state + ", manualEntry=" + manualEntry + "]";
  }
}
