package de.maredit.tar.models;

import java.util.List;

public class AccountModel {

  private String id;
  
  private User user;
  private Integer year;
  private double totalVacationDays;
  private double previousYearOpenVacationDays;
  private double pendingVacationDays;
  private double approvedVacationDays;
  private List<? extends AccountEntry> entries;
  private UserVacationAccount account;
  private double openVacationDays;
  
  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }
  public User getUser() {
    return user;
  }
  public void setUser(User user) {
    this.user = user;
  }
  public Integer getYear() {
    return year;
  }
  public void setYear(Integer year) {
    this.year = year;
  }
  public double getTotalVacationDays() {
    return totalVacationDays;
  }
  public void setTotalVacationDays(double totalVacationDays) {
    this.totalVacationDays = totalVacationDays;
  }
  public double getPreviousYearOpenVacationDays() {
    return previousYearOpenVacationDays;
  }
  public void setPreviousYearOpenVacationDays(double previousYearOpenVacationDays) {
    this.previousYearOpenVacationDays = previousYearOpenVacationDays;
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
  public List<? extends AccountEntry> getEntries() {
    return entries;
  }
  public void setEntries(List<? extends AccountEntry> entries) {
    this.entries = entries;
  }
  public UserVacationAccount getAccount() {
    return account;
  }
  public void setAccount(UserVacationAccount account) {
    this.account = account;
  }
  public void setOpenVacationDays(double openVacationDays) {
    this.openVacationDays = openVacationDays;
  }
  public double getOpenVacationDays() {
    return openVacationDays;
  }
}
