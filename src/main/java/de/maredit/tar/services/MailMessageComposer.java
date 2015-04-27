package de.maredit.tar.services;

import de.maredit.tar.models.User;
import de.maredit.tar.models.Vacation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MailMessageComposer {

  private static final String MAIL_TEMPLATE = "mailForm";
  private static final String MAIL_SUBJECT = "Urlaubsantrag";

  private static final Logger LOG = LogManager.getLogger(MailMessageComposer.class);

  @Autowired
  public SpringTemplateEngine templateEngine;

  public MailMessageComposer() {}

  private String prepareMailBody(Vacation vacation) {
    Context ctx = new Context();
    ctx.setVariable("employee", vacation.getUser().getFirstName());
    ctx.setVariable("manager", vacation.getManager().getFullname());
    ctx.setVariable("substitute", vacation.getSubstitute() == null ? "" : vacation.getSubstitute()
        .getFullname());
    ctx.setVariable("fromDate", vacation.getFrom());
    ctx.setVariable("toDate", vacation.getTo());
    ctx.setVariable("totalDays", vacation.getDays());
    ctx.setVariable("leftDays", vacation.getDaysLeft());

    return templateEngine.process(MAIL_TEMPLATE, ctx);
  }

  public SimpleMailMessage composeSimpleMailMessage(Vacation vacation) {
    LOG.debug("Preparing email for Vacation: {}", vacation.toString());
    SimpleMailMessage message = new SimpleMailMessage();

    message.setSubject(MAIL_SUBJECT);
    message.setSentDate(new Date());
    message.setTo(getRecipients(vacation));
    message.setText(prepareMailBody(vacation));
    return message;
  }

  private String[] getRecipients(Vacation vacation) {
    List<String> recipients = new ArrayList<>();
    recipients.add(retrieveMail(vacation.getUser()));
    recipients.add(retrieveMail(vacation.getSubstitute()));
    recipients.add(retrieveMail(vacation.getManager()));

    List<String> filteredList =
        recipients.stream().filter(e -> !e.isEmpty()).collect(Collectors.toList());
    String[] array = new String[filteredList.size()];
    return filteredList.toArray(array);
  }

  private String retrieveMail(User user) {
    String mail = "";
    if (user != null && user.getMail() != null) {
      mail = user.getMail();
    }
    return mail;
  }

  public MimeMessage composeMimeMailMessage(Vacation vacation, MimeMessage message) {
    try {
      MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);
      messageHelper.setSubject(MAIL_SUBJECT);
      messageHelper.setTo(getRecipients(vacation));
      messageHelper.setSentDate(new Date());
      messageHelper.setText(prepareMailBody(vacation), true);
    } catch (MessagingException e) {
      LOG.error(e);
    }
    return message;
  }

  public void setSpringTemplateEngine(SpringTemplateEngine templateEngine) {
    this.templateEngine = templateEngine;
  }

}
