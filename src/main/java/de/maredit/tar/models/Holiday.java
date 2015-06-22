package de.maredit.tar.models;

import java.time.LocalDate;

public class Holiday {

  private LocalDate date;
  private String description;
  private double valence;

  public Holiday(LocalDate date, String description) {
    this.date = date;
    this.description = description;
    valence = 1;
  }
  
  public LocalDate getDate() {
    return date;
  }
  public void setDate(LocalDate date) {
    this.date = date;
  }
  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    this.description = description;
  }
  public double getValence() {
    return valence;
  }
  public void setValence(double valence) {
    this.valence = valence;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((date == null) ? 0 : date.hashCode());
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
    Holiday other = (Holiday) obj;
    if (date == null) {
      if (other.date != null)
        return false;
    } else if (!date.equals(other.date))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "Holiday [date=" + date + ", description=" + description + ", valence=" + valence + "]";
  }
}
