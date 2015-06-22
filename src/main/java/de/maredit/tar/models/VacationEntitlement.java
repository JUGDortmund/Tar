package de.maredit.tar.models;

public class VacationEntitlement {

  private double days;
  private double daysLastYear;

  public VacationEntitlement(double days, double daysLastYear) {
    this.days = days;
    this.daysLastYear = daysLastYear;
  }

  public double getDays() {
    return days;
  }

  public double getDaysLastYear() {
    return daysLastYear;
  }
  
  public void reduceDaysLastYear(double days) {
    daysLastYear -= days;
  }

  public void reduceDays(double days) {
    this.days -= days;
  }

  @Override
  public String toString() {
    return "VacationEntitlement [days=" + days + ", daysLastYear=" + daysLastYear + "]";
  }

}
