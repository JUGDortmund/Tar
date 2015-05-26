package de.maredit.tar.models;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.format.DateTimeFormatter;

/**
 * Created by czillmann on 29.04.15.
 */

public class CalendarEvent {

  private static final Logger LOG = LogManager.getLogger(CalendarEvent.class);

  private String start;
  private String end;

  private String title;

  private String userName;
  private String userFirstName;
  private String userLastName;

  private String substituteFirstName;
  private String substituteLastName;

  private String managerFirstName;
  private String managerLastName;

  private String state;
  private Boolean allDay;

  public CalendarEvent(Vacation vacation) {
    if(vacation.getUser() != null) {
      this.setTitle("Urlaub " + vacation.getUser().getUsername().substring(0, 3));
      this.setUserName(vacation.getUser().getUsername());
      this.setUserFirstName(vacation.getUser().getFirstname());
      this.setUserLastName(vacation.getUser().getLastname());
    }else {
      LOG.error("Provided user is null for vacation ID {}", vacation.getId());
    }
    if (vacation.getManager() != null) {
      this.setManagerFirstName(vacation.getManager().getFirstname());
      this.setManagerLastName(vacation.getManager().getLastname());
    } else {
      LOG.error("Provided manager is null for vacation ID {}", vacation.getId());
    }
    this.setStart(vacation.getFrom().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    this.setEnd(vacation.getTo().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

    this.setState(vacation.getState().get());
    if (vacation.getSubstitute() != null) {
      this.setSubstituteFirstName(vacation.getSubstitute().getFirstname());
      this.setSubstituteLastName(vacation.getSubstitute().getLastname());
    }
    this.allDay = true;
  }

  public String getStart() {
    return start;
  }

  public void setStart(String start) {
    this.start = start;
  }

  public String getEnd() {
    return end;
  }

  public void setEnd(String end) {
    this.end = end;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getUserFirstName() {
    return userFirstName;
  }

  public void setUserFirstName(String userFirstName) {
    this.userFirstName = userFirstName;
  }

  public String getUserLastName() {
    return userLastName;
  }

  public void setUserLastName(String userLastName) {
    this.userLastName = userLastName;
  }

  public String getSubstituteFirstName() {
    return substituteFirstName;
  }

  public void setSubstituteFirstName(String substituteFirstName) {
    this.substituteFirstName = substituteFirstName;
  }

  public String getSubstituteLastName() {
    return substituteLastName;
  }

  public void setSubstituteLastName(String substituteLastName) {
    this.substituteLastName = substituteLastName;
  }

  public String getManagerFirstName() {
    return managerFirstName;
  }

  public void setManagerFirstName(String managerFirstName) {
    this.managerFirstName = managerFirstName;
  }

  public String getManagerLastName() {
    return managerLastName;
  }

  public void setManagerLastName(String managerLastName) {
    this.managerLastName = managerLastName;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public Boolean isAllDay() {
    return allDay;
  }

  public void setAllDay(Boolean allDay) {
    this.allDay = allDay;
  }
}
