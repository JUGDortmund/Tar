package de.maredit.tar.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
@EnableConfigurationProperties(MailProperties.class)
public class MailServiceImpl implements MailService {

	private static final Logger LOGGER = LogManager
			.getLogger(MailServiceImpl.class);

	@Autowired
	private MailProperties mailProperties;

	private MailSender javaMailSender;

	public void setJavaMailSender(MailSender mailSender) {
		this.javaMailSender = mailSender;
	}

	@Override
	public void sendMail(SimpleMailMessage mailMessage) {
		LOGGER.debug("Mail: {}", mailMessage.toString());

		SimpleMailMessage msg = new SimpleMailMessage(mailMessage);
		try {
			this.javaMailSender.send(msg);
		} catch (MailException ex) {
			LOGGER.error(ex.getMessage());
		}
	}

	@Bean
	public MailSender javaMailSender() {
		if (javaMailSender == null) {
			JavaMailSenderImpl jMailSender = new JavaMailSenderImpl();
			jMailSender.setHost(mailProperties.getHost());
			jMailSender.setPort(mailProperties.getPort());
			jMailSender.setUsername(mailProperties.getUsername());
			jMailSender.setPassword(mailProperties.getPassword());
			this.javaMailSender = jMailSender;
		}
		return javaMailSender;
	}
}
