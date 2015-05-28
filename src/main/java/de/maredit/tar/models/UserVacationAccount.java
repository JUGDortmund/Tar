package de.maredit.tar.models;

import java.util.List;

/**
 * Created by czillmann on 19.05.15.
 */
public class UserVacationAccount {

  private User user;
  private List<Vacation> vacations;
  private double openVacationDays;
  private double pendingVacationDays;
  private double approvedVacationDays;
  private double previousYearOpenVacationDays;

  public double getTotalDays() {
    double totalDays = getApprovedVacationDays() + getPendingVacationDays() + getOpenVacationDays() + getPreviousYearOpenVacationDays();
    return totalDays;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public List<Vacation> getVacations() {
    return vacations;
  }

  public void setVacations(List<Vacation> vacations) {
    this.vacations = vacations;
  }

  public double getOpenVacationDays() {
    return openVacationDays;
  }

  public void setOpenVacationDays(double openVacationDays) {
    this.openVacationDays = openVacationDays;
  }

  public double getPendingVacationDays() {
    return pendingVacationDays;
  }

  public void setPendingVacationDays(double pendingVacationDays) {
    this.pendingVacationDays = pendingVacationDays;
  }

  public double getApprovedVacationDays() {
    return approvedVacationDays;
  }

  public void setApprovedVacationDays(double approvedVacationDays) {
    this.approvedVacationDays = approvedVacationDays;
  }

  public double getPreviousYearOpenVacationDays() {
    return previousYearOpenVacationDays;
  }

  public void setPreviousYearOpenVacationDays(double previousYearOpenVacationDays) {
    this.previousYearOpenVacationDays = previousYearOpenVacationDays;
  }
}
