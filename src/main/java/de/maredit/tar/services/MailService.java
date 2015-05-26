package de.maredit.tar.services;

import org.springframework.stereotype.Service;

import de.maredit.tar.services.mail.MailObject;

@Service
public interface MailService {

  void sendMail(MailObject mail);

  void sendSimpleMail(MailObject mail);

}
