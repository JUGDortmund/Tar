package de.maredit.tar.models;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by phorninge on 09.06.15.
 */
@Document
public class UserHoliday {

  private String date;
  private double valence;
  private String description;
  
  public UserHoliday() {
  }
  
  public UserHoliday(UserHoliday userHoliday) {
    date = userHoliday.date;
    valence = userHoliday.valence;
    description = userHoliday.description;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public double getValence() {
    return valence;
  }

  public void setValence(double valence) {
    this.valence = valence;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public String toString() {
    return "UserHoliday [date=" + date + ", valence=" + valence + ", description=" + description
        + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((date == null) ? 0 : date.hashCode());
    long temp;
    temp = Double.doubleToLongBits(valence);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    UserHoliday other = (UserHoliday) obj;
    if (date == null) {
      if (other.date != null) return false;
    } else if (!date.equals(other.date)) return false;
    return Double.doubleToLongBits(valence) == Double.doubleToLongBits(other.valence);
  }
}
