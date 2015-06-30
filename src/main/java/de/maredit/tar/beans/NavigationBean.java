package de.maredit.tar.beans;

public class NavigationBean {
  
  public static final String VACATION_PAGE = "vacation";
  public static final String CALENDAR_PAGE = "calendar";
  public static final String OVERVIEW_PAGE = "overview";
  public static final String BOOKING_PAGE = "booking";

  private String activeComponent = VACATION_PAGE;

  public String getActiveComponent() {
    return activeComponent;
  }

  public void setActiveComponent(String activeComponent) {
    this.activeComponent = activeComponent;
  }
  
}
