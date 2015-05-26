package de.maredit.tar.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.time.LocalTime;

import javax.validation.constraints.NotNull;

public class TimelineItem {
  @Id
  private String id;

  @NotNull
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "dd.MM.yyyy")
  private LocalDateTime created;

  @DBRef
  @NotNull
  private User author;

  @DBRef
  @NotNull
  private Vacation vacation;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
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

  public Vacation getVacation() {
    return vacation;
  }

  public void setVacation(Vacation vacation) {
    this.vacation = vacation;
  }

  public CommentItem getCommentItem(){
    return (CommentItem)this;
  }

  public StateItem getStateItem(){
    return (StateItem)this;
  }

  public ProtocolItem getProtocolItem(){
    return (ProtocolItem)this;
  }

  public LocalTime getCreatedTime(){
    return this.created.toLocalTime();
  }
}
