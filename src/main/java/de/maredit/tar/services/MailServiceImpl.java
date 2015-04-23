package de.maredit.tar.services;

import javax.mail.internet.MimeMessage;

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
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import de.maredit.tar.models.User;
import de.maredit.tar.models.Vacation;

@Configuration
@EnableConfigurationProperties(MailProperties.class)
public class MailServiceImpl implements MailService {

	private static final Logger LOGGER = LogManager
			.getLogger(MailServiceImpl.class);

	@Autowired
	private MailProperties mailProperties;

	@Autowired
	private SpringTemplateEngine templateEngine;

	private MailSender javaMailSender;

	public void setJavaMailSender(MailSender mailSender) {
		this.javaMailSender = mailSender;
	}

	@Override
	public void sendMail(SimpleMailMessage mailMessage) {
		SimpleMailMessage msg = new SimpleMailMessage(mailMessage);
		try {
			this.javaMailSender.send(msg);
		} catch (MailException ex) {
			handleMailException(msg, ex);
		}
	}

	private void handleMailException(SimpleMailMessage msg, MailException ex) {
		if ("dev".equals(mailProperties.getProperties().get("environment"))) {
			LOGGER.debug(
					"Could not connect to SMTP-Server [host: {}, port:{}] in develop environment. "
							+ "Tried to send the following email:\n {}",
					mailProperties.getHost(), mailProperties.getPort(),
					msg.toString());
		} else {
			LOGGER.error(ex.getMessage());
		}
	}

	@Override
	public void sendMail(MimeMessage mimeMessage) {
		((JavaMailSenderImpl) this.javaMailSender).send(mimeMessage);
	}

	@Bean
	public MailSender javaMailSender() {
		if (javaMailSender == null) {
			JavaMailSenderImpl jMailSender = new JavaMailSenderImpl();
			if (mailProperties != null) {
				jMailSender.setHost(mailProperties.getHost());
				jMailSender.setPort(mailProperties.getPort());
				jMailSender.setUsername(mailProperties.getUsername());
				jMailSender.setPassword(mailProperties.getPassword());
			} else {
				LOGGER.error("mailProperties == null!. Apparently it has not been properly autowired!");
			}
			this.javaMailSender = jMailSender;
		}
		return javaMailSender;
	}

	@Override
	public void sendMail(Vacation vacation) {
		LOGGER.debug("Preparing email for Vacation: {}", vacation.toString());
		SimpleMailMessage message = new SimpleMailMessage();
		vacation.setUser(createDummyUser("Albert"));
		vacation.setManager(createDummyUser("Einstein"));
		vacation.setSubstitute(createDummyUser("Newton"));
		message.setSubject("Urlaubsantrag");
		message.setTo(new String[] { vacation.getUser().getMail(),
				vacation.getManager().getMail(),
				vacation.getSubstitute().getMail() });
		message.setText(prepareMailBody(vacation));
		sendMail(message);
	}

	private String prepareMailBody(Vacation vacation) {
		Context ctx = new Context();
		ctx.setVariable("employee", vacation.getUser().getFirstName());
		ctx.setVariable("manager", vacation.getManager().getFirstName());
		ctx.setVariable("substitute", vacation.getSubstitute().getFirstName());
		ctx.setVariable("fromDate", vacation.getFrom());
		ctx.setVariable("toDate", vacation.getTo());
		ctx.setVariable("totalDays", vacation.getDays());
		ctx.setVariable("leftDays", vacation.getDaysLeft());

		return templateEngine.process("mailForm", ctx);
	}

	/*
	 * TODO: to be removed after the frontend form delivers valid user
	 * (Mitarbeiter), substitute (Vertreter) and manager (züständiger Vertreter)
	 */
	private User createDummyUser(String name) {
		User user = new User();
		user.setFirstName(name);
		user.setMail(name + "@maredit.de");
		return user;
	}
}
