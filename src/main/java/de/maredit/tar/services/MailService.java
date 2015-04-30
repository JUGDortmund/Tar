package de.maredit.tar.services;

import de.maredit.tar.services.mail.MailObject;
import org.springframework.stereotype.Service;

@Service
public interface MailService {

  public void sendMail(MailObject mail);

  public void sendSimpleMail(MailObject mail);

}
