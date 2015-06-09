package de.maredit.tar.models;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CalendarHolidayEvent {

  private static final Logger LOG = LogManager.getLogger(CalendarHolidayEvent.class);
  
  private String title;
  private String start;
  private String end;
  private String state;
  
  public CalendarHolidayEvent(UserHoliday userHoliday){
    this.setTitle(userHoliday.getDescription());
    this.setStart(userHoliday.getDate().toString());
    this.setEnd(userHoliday.getDate().toString());
    this.setState("Feiertag");
}

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
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

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }
}
