package de.maredit.tar.services;

import de.maredit.tar.services.mail.Attachment;

import org.springframework.stereotype.Service;
import de.maredit.tar.services.mail.MailObject;

@Service
public interface MailService {

  public void sendMail(MailObject mail, Attachment... attachments);

  void sendSimpleMail(MailObject mail);

}
