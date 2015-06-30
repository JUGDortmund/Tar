package de.maredit.tar.models;

public class AdjustmentModel {

  private String typ;
  private String year;
  private Vacation vacationReference;
  private int days;
  private String comment;

  public String getTyp() {
    return typ;
  }

  public void setTyp(String typ) {
    this.typ = typ;
  }

  public String getYear() {
    return year;
  }

  public void setYear(String year) {
    this.year = year;
  }

  public Vacation getVacationReference() {
    return vacationReference;
  }

  public void setVacationReference(Vacation vacationReference) {
    this.vacationReference = vacationReference;
  }

  public int getDays() {
    return days;
  }

  public void setDays(int days) {
    this.days = days;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }
}
