package de.maredit.tar.models;

import org.springframework.data.mongodb.core.mapping.DBRef;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by czillmann on 19.05.15.
 */
@Document
public class UserVacationAccount {

  @Id
  private String id;
  @DBRef
  private User user;
  private Integer year;
  private double totalVacationDays;
  private Double previousYearOpenVacationDays;
  private LocalDate expiryDate;
  @DBRef
  private List<Vacation> vacations;
  private double pendingVacationDays;
  private double approvedVacationDays;

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

  public Double getPreviousYearOpenVacationDays() {
    return previousYearOpenVacationDays;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public int getYear() {
    return year;
  }

  public void setYear(int year) {
    this.year = year;
  }

  public double getTotalVacationDays() {
    return totalVacationDays;
  }

  public void setTotalVacationDays(double totalVacationDays) {
    this.totalVacationDays = totalVacationDays;
  }

  public LocalDate getExpiryDate() {
    return expiryDate;
  }

  public void setExpiryDate(LocalDate expiryDate) {
    this.expiryDate = expiryDate;
  }

  public void setPreviousYearOpenVacationDays(Double previousYearOpenVacationDays) {
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

  public double getOpenVacationDays() {
    return totalVacationDays - approvedVacationDays - pendingVacationDays + (previousYearOpenVacationDays == null ? 0 : previousYearOpenVacationDays);
  }

  public void addVacation(Vacation vacation) {
    if (vacations == null) {
      vacations = new ArrayList<>();
    }
    vacations.add(vacation);
  }
}
