package de.maredit.tar.services;

import javax.mail.internet.MimeMessage;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import de.maredit.tar.models.Vacation;

@Service
public interface MailService {

	public void sendMail(SimpleMailMessage mailMessage);

	public void sendMail(MimeMessage mailMessage);
	
	public void sendMail(Vacation vacation);
}
