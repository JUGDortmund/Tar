package de.maredit.tar.models;

public class LastingVacation {

  private double vacationDays;
  private double vacationDaysLastYear;

  public LastingVacation(double vacationDays, double vacationDaysLastYear) {
    this.vacationDays = vacationDays;
    this.vacationDaysLastYear = vacationDaysLastYear;
  }

  public double getVacationDays() {
    return vacationDays;
  }

  public double getVacationDaysLastYear() {
    return vacationDaysLastYear;
  }
  
  public void reduceVacationDaysLastYear(double days) {
    vacationDaysLastYear -= days;
  }

  public void reduceVacationDays(double days) {
    vacationDays -= days;
  }

  @Override
  public String toString() {
    return "LastingVacation [vacationDays=" + vacationDays + ", vacationDaysLastYear="
        + vacationDaysLastYear + "]";
  }
}
