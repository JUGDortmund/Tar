package de.maredit.tar.services;

import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

public class MailService {

  private MailSender mailSender;
  private SimpleMailMessage templateMessage;

  public void setMailSender(MailSender mailSender) {
    this.mailSender = mailSender;
  }

  public void setTemplateMessage(SimpleMailMessage templateMessage) {
    this.templateMessage = templateMessage;
  }

  public void sendAMessage() {
    // Do the business calculations...

    // Call the collaborators to persist the order...

    // Create a thread safe "copy" of the template message and customize it
    SimpleMailMessage msg = new SimpleMailMessage(this.templateMessage);
    msg.setTo("foo@bar.com");
    msg.setText("Hello");
    try {
      this.mailSender.send(msg);
    } catch (MailException ex) {
      // simply log it and go on...
      System.err.println(ex.getMessage());
    }
  }
}
