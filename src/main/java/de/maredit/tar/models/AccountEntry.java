package de.maredit.tar.models;

import de.maredit.tar.data.User;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface AccountEntry {

  public abstract String getDisplayText();

  public abstract LocalDate getFrom();

  public abstract LocalDate getTo();

  public abstract double getDays();

  public abstract double getBalance();

  public abstract void setBalance(double balance);

  public abstract LocalDateTime getCreated();

  public abstract User getAuthor();

  public abstract boolean isStarred();
}
