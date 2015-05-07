package de.maredit.tar.services;

import java.util.Date;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import de.maredit.tar.services.mail.MailObject;

@Component
public class MailMessageComposer {


  private static final Logger LOG = LogManager.getLogger(MailMessageComposer.class);

  @Autowired
  public SpringTemplateEngine templateEngine;

  public MailMessageComposer() {}

  private String prepareMailBody(MailObject mail, String templateName) {
    Context ctx = new Context();
    ctx.setVariables(mail.getValues());
    return templateEngine.process(templateName, ctx);
  }

  public SimpleMailMessage composeSimpleMailMessage(MailObject mail) {
    SimpleMailMessage message = new SimpleMailMessage();

    message.setSubject(mail.getSubject());
    message.setSentDate(new Date());
    message.setTo(mail.getToRecipients());
    if (mail.getCCRecipients() != null) {
      message.setCc(mail.getCCRecipients());
    }
    message.setText(prepareMailBody(mail, mail.getTemplate()));
    return message;
  }


  public MimeMessage composeMimeMailMessage(MailObject mail, MimeMessage message) {
    try {
      MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);
      messageHelper.setSubject(mail.getSubject());
      messageHelper.setSentDate(new Date());
      messageHelper.setTo(mail.getToRecipients());
      if (mail.getCCRecipients() != null) {
        messageHelper.setCc(mail.getCCRecipients());
      }
      messageHelper.setText(prepareMailBody(mail, mail.getHtmlTemplate()), true);
    } catch (MessagingException e) {
      LOG.error("Error creating mail", e);
    }
    return message;
  }

  public void setSpringTemplateEngine(SpringTemplateEngine templateEngine) {
    this.templateEngine = templateEngine;
  }

}