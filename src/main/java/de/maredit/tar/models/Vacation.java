package de.maredit.tar.models;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Reference;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="vacations")
public class Vacation {
  @Id
  private String id;
  
  @Reference
  @DBRef
  private User user;
  
  private Date from;
  private Date to;
  
  private State state;
  
  public Vacation() {
  }
 
  public Vacation(User user, LocalDate from, LocalDate to, State state) {
    this.user = user;
    this.from = Date.from(from.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    this.to = Date.from(to.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    this.state = state;
  }
  
  public User getUser() {
    return user;
  }
  
  public State getState() {
    return state;
  }
  
  public void setUser(User user) {
    this.user = user;
  }
  
  public void setState(State state) {
    this.state = state;
  }

  public Date getFrom() {
    return from;
  }

  public Date getTo() {
    return to;
  }

  public void setFrom(Date from) {
    this.from = from;
  }

  public void setTo(Date to) {
    this.to = to;
  }
}