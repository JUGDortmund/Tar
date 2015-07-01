package de.maredit.tar.models;

import de.maredit.tar.models.enums.CalendarEntryType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.format.DateTimeFormatter;

/**
 * Created by czillmann on 29.04.15.
 */

public class CalendarEvent {

  public static final Logger LOG = LogManager.getLogger(CalendarEvent.class);
  public static final String START_HALF_DAY_HOLIDAY_MORNING = " 08:00:00";
  public static final String END_HALF_DAY_HOLIDAY_MORNING = " 12:00:00";
  public static final String START_HALF_DAY_HOLIDAY_AFTERNOON = " 13:00:00";
  public static final String END_HALF_DAY_HOLIDAY_AFTERNOON = " 17:00:00";
  public static final String VACATION_TYPE = "Urlaub";

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
  private String vacationType;

  private CalendarEntryType type;
  private String state;
  private Boolean allDay;

  public CalendarEvent(Vacation vacation) {
    if (vacation.getUser() != null) {
      this.setTitle(VACATION_TYPE + " "+ vacation.getUser().getUsername().substring(0, 3));
      this.setUserName(vacation.getUser().getUsername());
      this.setUserFirstName(vacation.getUser().getFirstname());
      this.setUserLastName(vacation.getUser().getLastname());
    } else {
      LOG.error("Provided user is null for vacation ID {}", vacation.getId());
    }
    if (vacation.getManager() != null) {
      this.setManagerFirstName(vacation.getManager().getFirstname());
      this.setManagerLastName(vacation.getManager().getLastname());
    } else {
      LOG.error("Provided manager is null for vacation ID {}", vacation.getId());
    }

    this.setState(vacation.getState().get());
    if (vacation.getSubstitute() != null) {
      this.setSubstituteFirstName(vacation.getSubstitute().getFirstname());
      this.setSubstituteLastName(vacation.getSubstitute().getLastname());
    }
    this.setType(CalendarEntryType.VACATION);
    this.setVacationType(VACATION_TYPE);
    this.allDay = !vacation.isHalfDay();

    String startTime = "";
    String endTime = "";

    if ( vacation.isHalfDay() ) {
      switch (vacation.getTimeframe()) {
        case AFTERNOON:
          startTime = START_HALF_DAY_HOLIDAY_AFTERNOON;
          endTime = END_HALF_DAY_HOLIDAY_AFTERNOON;
          break;
        case MORNING:
          startTime = START_HALF_DAY_HOLIDAY_MORNING;
          endTime = END_HALF_DAY_HOLIDAY_MORNING;
          break;
        default:
          startTime = "";
          endTime = "";
      }
    }

    this.setStart(vacation.getFrom().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + startTime);
    this.setEnd(vacation.getTo().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + endTime);
  }

  public CalendarEvent(Holiday holiday) {
    this.setTitle(holiday.getDescription());

    this.setState(holiday.getDescription());
    this.setType(CalendarEntryType.HOLIDAY);
    this.setStart(holiday.getDate().toString());
    this.setEnd(holiday.getDate().toString());
    if (holiday.getValence() == 1.0) {
      this.setStart(holiday.getDate().toString());
      this.setEnd(holiday.getDate().toString());
      this.setAllDay(true);
    } else {
      this.setStart(holiday.getDate().toString() + " " + START_HALF_DAY_HOLIDAY_AFTERNOON);
      this.setEnd(holiday.getDate().toString() + " " + END_HALF_DAY_HOLIDAY_AFTERNOON);
      this.setAllDay(false);
    }
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

  public CalendarEntryType getType() {
    return type;
  }

  public void setType(CalendarEntryType type) {
    this.type = type;
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

  public String getVacationType() {
    return vacationType;
  }

  public void setVacationType(String vacationType) {
    this.vacationType = vacationType;
  }
}
