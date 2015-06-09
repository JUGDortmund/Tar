package de.maredit.tar.models;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class UserHoliday {

  private String date;
  private double valence;
  private String description;

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
}
