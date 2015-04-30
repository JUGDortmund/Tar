package de.maredit.tar.services;

import org.springframework.stereotype.Service;

import de.maredit.tar.services.mail.MailObject;

@Service
public interface MailService {

  public void sendMail(MailObject mail);

  public void sendSimpleMail(MailObject mail);

}
