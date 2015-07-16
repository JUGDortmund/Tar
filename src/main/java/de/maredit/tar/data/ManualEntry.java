package de.maredit.tar.data;

import de.maredit.tar.models.enums.ManualEntryType;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Objects;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

@Document
public class ManualEntry {

  @Id
  private String id;

  @DBRef
  @NotNull
  private User user;

  @NotNull
  private ManualEntryType type;

  @NotNull
  private int year;

  @DBRef
  private Vacation vacation;

  @DecimalMin(value = "0.5", message = "{manualEntry.days}")
  private double days;

  @NotBlank(message = "{manualEntry.description}")
  private String description;

  @DBRef
  private User author;

  @NotNull
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "dd.MM.yyyy")
  private LocalDateTime created;

  public ManualEntry() {
    this.created = LocalDateTime.now();
    this.type = ManualEntryType.ADD;
  }

  public ManualEntry(User user, int year,
                     double days, ManualEntryType type,
                     String description, Vacation vacation, User author) {
    this.user = user;
    this.year = year;
    this.days = days;
    this.type = type;
    this.description = description;
    this.vacation = vacation;
    this.author = author;
    this.created = LocalDateTime.now();
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public ManualEntryType getType() {
    return type;
  }

  public void setType(ManualEntryType type) {
    this.type = type;
  }

  public int getYear() {
    return year;
  }

  public void setYear(int year) {
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

  public void setDays(double days) {
    this.days = days;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public User getAuthor() {
    return author;
  }

  public void setAuthor(User author) {
    this.author = author;
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
    final ManualEntry other = (ManualEntry) obj;
    return Objects.equals(this.id, other.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(this.id);
  }

  @Override
  public String toString() {
    return "ManualEntry [user=" + user + ", year=" + year + ", days=" + days
           + ", type=" + type.get() + ", description=" + description + ", created=" + created + "author=" + author+"]";
  }
}
