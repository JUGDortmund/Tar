package de.maredit.tar.models;

import de.maredit.tar.data.User;

import java.time.LocalDateTime;



public interface AccountEntry {

  public abstract double getDays();

  public abstract LocalDateTime getCreated();

  public abstract User getAuthor();

}
