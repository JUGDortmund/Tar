package de.maredit.tar.models;

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
  
  public User getUser() {
    return user;
  }
  
  public Date getFrom() {
    return from;
  }
  
  public Date getTo() {
    return to;
  }
  
  public void setUser(User user) {
    this.user = user;
  }
  
  public void setFrom(Date from) {
    this.from = from;
  }
  
  public void setTo(Date to) {
    this.to = to;
  }

  public State getState() {
    return state;
  }

  public void setState(State state) {
    this.state = state;
  }
}