package de.maredit.tar.models;

public class AccountModel {

  private User user;
  private double totalVacationDays;
  private String id;
  private Double previousYearOpenVacationDays;
  private double approvedVacationDays;
  private double pendingVacationDays;

  public void setUser(User user) {
    this.user = user;
  }

  public void setTotalVacationDays(double totalVacationDays) {
    this.totalVacationDays = totalVacationDays;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setPreviousYearOpenVacationDays(Double previousYearOpenVacationDays) {
    this.previousYearOpenVacationDays = previousYearOpenVacationDays;
  }

  public void setApprovedVacationDays(double approvedVacationDays) {
    this.approvedVacationDays = approvedVacationDays;
  }

  public void setPendingVacationDays(double pendingVacationDays) {
    this.pendingVacationDays = pendingVacationDays;
  }

  public User getUser() {
    return user;
  }

  public double getTotalVacationDays() {
    return totalVacationDays;
  }

  public String getId() {
    return id;
  }

  public Double getPreviousYearOpenVacationDays() {
    return previousYearOpenVacationDays;
  }

  public double getApprovedVacationDays() {
    return approvedVacationDays;
  }

  public double getPendingVacationDays() {
    return pendingVacationDays;
  }

  public double getOpenVacationDays() {
    return totalVacationDays - approvedVacationDays - pendingVacationDays + (previousYearOpenVacationDays == null ? 0 : previousYearOpenVacationDays);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    AccountModel other = (AccountModel) obj;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    return true;
  }
}
