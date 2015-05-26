package de.maredit.tar.services;

import de.maredit.tar.services.mail.Attachment;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import microsoft.exchange.webservices.data.exception.ServiceLocalException;
import microsoft.exchange.webservices.data.property.complex.EmailAddress;
import de.maredit.tar.properties.CustomMailProperties;
import de.maredit.tar.services.mail.MailObject;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.property.complex.EmailAddressCollection;
import microsoft.exchange.webservices.data.property.complex.MessageBody;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import java.util.Arrays;

@Service
@Profile({"exchangeMailService"})
public class MailServiceExchangeImpl implements MailService {

  private static final Logger LOG = LogManager.getLogger(MailServiceExchangeImpl.class);

  @Autowired
  private ExchangeService exchangeService;
  
  @Autowired
  private CustomMailProperties customMailProperties;

  @Autowired
  public SpringTemplateEngine templateEngine;

  @Override
  public void sendMail(MailObject mail, Attachment... attachments) {
    try {
      EmailMessage msg = buildBaseMail(mail);
      msg.setBody(MessageBody.getMessageBodyFromText(prepareMailBody(mail, mail.getHtmlTemplate())));
      if (attachments != null) {
        for (Attachment attachment : attachments) {
          msg.getAttachments().addFileAttachment(attachment.getMimeType(), attachment.getData());
        }
      }
      msg.send();
    } catch (Exception e) {
      LOG.error("Error sending mail", e);
    }
  }

  @Override
  public void sendSimpleMail(MailObject mail) {
    try {
      EmailMessage msg = buildBaseMail(mail);
      msg.setBody(MessageBody.getMessageBodyFromText(prepareMailBody(mail, mail.getTemplate())));
      msg.send();
    } catch (Exception e) {
      LOG.error("Error sending mail", e);
    }
  }

  private EmailMessage buildBaseMail(MailObject mail) throws Exception, ServiceLocalException {
    EmailMessage msg = new EmailMessage(exchangeService);
    if (mail.sendToAdditionalRecipient()) {
      mail.setCcRecipients(ArrayUtils.addAll(mail.getCCRecipients(),
          customMailProperties.getAdditionalRecipients()));
    }
    msg.setSubject(mail.getSubject());
    EmailAddressCollection toRecipients = msg.getToRecipients();
    Arrays.stream(mail.getToRecipients()).forEach(adress -> toRecipients.add(adress));
    if (mail.getCCRecipients() != null) {
      EmailAddressCollection ccRecipients = msg.getCcRecipients();
      Arrays.stream(mail.getCCRecipients()).forEach(adress -> ccRecipients.add(adress));
    }
    if (customMailProperties.getSender() != null) {
      msg.setSender(new EmailAddress(customMailProperties.getSender()));
    }
    return msg;
  }

  private String prepareMailBody(MailObject mail, String templateName) {
    Context ctx = new Context();
    ctx.setVariables(mail.getValues());
    return templateEngine.process(templateName, ctx);
  }

}
