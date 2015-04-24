package de.maredit.tar.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import de.maredit.tar.models.User;
import de.maredit.tar.models.Vacation;

@Component
public class MailMessageComposer {

	private static final String MAIL_TEMPLATE = "mailForm";
	private static final String MAIL_SUBJECT = "Urlaubsantrag";

	private static final Logger LOG = LogManager
			.getLogger(MailMessageComposer.class);
	

	@Autowired
	public SpringTemplateEngine templateEngine;

	public MailMessageComposer() {
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

		return templateEngine.process(MAIL_TEMPLATE, ctx);
	}

	public SimpleMailMessage composeMail(Vacation vacation) {
		LOG.debug("Preparing email for Vacation: {}", vacation.toString());
		SimpleMailMessage message = new SimpleMailMessage();
		
		//TODO: Remove once a Vacation comes bundled with user, manager and substitute.
		vacation.setUser(createDummyUser("Albert"));
		vacation.setManager(createDummyUser("Einstein"));
		vacation.setSubstitute(createDummyUser("Newton"));
		
		message.setSubject(MAIL_SUBJECT);
		message.setTo(new String[] { vacation.getUser().getMail(),
				vacation.getManager().getMail(),
				vacation.getSubstitute().getMail() });
		message.setText(prepareMailBody(vacation));
		return message;
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