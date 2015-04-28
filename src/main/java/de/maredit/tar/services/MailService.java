package de.maredit.tar.services;

import de.maredit.tar.models.Vacation;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;

@Service
public interface MailService {

  public void sendMail(SimpleMailMessage mailMessage);

  public void sendMail(MimeMessage mailMessage);

  public void sendSimpleMail(Vacation vacation);

  public void sendMimeMail(Vacation vacation);
}
