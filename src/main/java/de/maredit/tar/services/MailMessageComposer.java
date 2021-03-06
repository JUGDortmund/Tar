package de.maredit.tar.services;

import de.maredit.tar.services.mail.Attachment;
import de.maredit.tar.services.mail.MailObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import java.util.Date;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;

@Component
public class MailMessageComposer {

  @Autowired
  public SpringTemplateEngine templateEngine;

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

  public MimeMessage composeMimeMailMessage(MailObject mail, MimeMessage message, Attachment... attachments)
      throws MessagingException {
    MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);
    messageHelper.setSubject(mail.getSubject());
    messageHelper.setSentDate(new Date());
    messageHelper.setTo(mail.getToRecipients());
    if (mail.getCCRecipients() != null) {
      messageHelper.setCc(mail.getCCRecipients());
    }
    messageHelper.setText(prepareMailBody(mail, mail.getHtmlTemplate()), true);
    if (attachments != null) {
      for (Attachment attachment : attachments) {
        if (attachment != null) {
          messageHelper.addAttachment(attachment.getFilename(), new ByteArrayDataSource(attachment.getData(), attachment.getMimeType()));
        }
      }
    }
    return message;
  }

  public void setSpringTemplateEngine(SpringTemplateEngine templateEngine) {
    this.templateEngine = templateEngine;
  }

}
