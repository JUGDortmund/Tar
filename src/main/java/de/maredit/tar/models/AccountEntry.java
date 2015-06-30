package de.maredit.tar.models;

import java.time.LocalDateTime;



public interface AccountEntry {

  public abstract double getDays();

  public abstract LocalDateTime getCreated();

  public abstract User getAuthor();

}
