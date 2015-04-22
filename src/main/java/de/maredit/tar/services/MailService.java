package de.maredit.tar.services;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

@Service
public interface MailService {

	public void sendMail(SimpleMailMessage mailMessage);
}
