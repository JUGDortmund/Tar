package de.maredit.tar.services.calendar;

import de.maredit.tar.services.mail.Attachment;

public class CalendarItem {

  private String appointmentId;

  private Attachment mailAttachment;
  
  public CalendarItem() {
  }
  
  public CalendarItem(String appointmentId) {
    this.appointmentId = appointmentId;
  }
  
  public String getAppointmentId() {
    return appointmentId;
  }

  public void setAppointmentId(String appointmentId) {
    this.appointmentId = appointmentId;
  }

  public Attachment getMailAttachment() {
    return mailAttachment;
  }

  public void setMailAttachment(Attachment mailAttachment) {
    this.mailAttachment = mailAttachment;
  }
}
