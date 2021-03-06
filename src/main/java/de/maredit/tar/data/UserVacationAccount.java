package de.maredit.tar.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

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
  private double previousYearOpenVacationDays;
  private LocalDate expiryDate;
  @DBRef
  private Set<Vacation> vacations;
  @DBRef
  private Set<ManualEntry> manualEntries;

  public UserVacationAccount() {
    vacations = new HashSet<Vacation>();
    manualEntries = new HashSet<ManualEntry>();
  }
  
  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Set<Vacation> getVacations() {
    return vacations;
  }

  public void setVacations(Set<Vacation> vacations) {
    this.vacations = vacations;
  }

  public Set<ManualEntry> getManualEntries() {
    return manualEntries;
  }

  public void setManualEntries(Set<ManualEntry> manualEntries) {
    this.manualEntries = manualEntries;
  }

  public double getPreviousYearOpenVacationDays() {
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

  public void setPreviousYearOpenVacationDays(double previousYearOpenVacationDays) {
    this.previousYearOpenVacationDays = previousYearOpenVacationDays;
  }

  public void addVacation(Vacation vacation) {
    if (vacations == null) {
      vacations = new HashSet<>();
    }
    if (!vacations.add(vacation)) {
      vacations.remove(vacation);
      vacations.add(vacation);
    }
  }

  public void addManualEntry(ManualEntry manualEntry) {
    if (manualEntries == null) {
      manualEntries = new HashSet<>();
    }
    if (!manualEntries.add(manualEntry)) {
      manualEntries.remove(manualEntry);
      manualEntries.add(manualEntry);
    }
  }
}
