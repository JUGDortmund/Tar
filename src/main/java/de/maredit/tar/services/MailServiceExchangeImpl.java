package de.maredit.tar.services;

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

  @Autowired
  private ExchangeService exchangeService;
  
  @Autowired
  private CustomMailProperties customMailProperties;

  @Autowired
  public SpringTemplateEngine templateEngine;

  // @Override
  @Override
  public void sendMail(MailObject mail) {
    try {
      EmailMessage msg = new EmailMessage(exchangeService);
      if (mail.sendToAdditionalRecipient()) {
        mail.setCcRecipients(ArrayUtils.addAll(mail.getCCRecipients(),
            customMailProperties.getAdditionalRecipients()));
      }
      msg.setSubject(mail.getSubject());
      msg.setBody(MessageBody.getMessageBodyFromText(prepareMailBody(mail, mail.getHtmlTemplate())));
      EmailAddressCollection toRecipients = msg.getToRecipients();
      Arrays.stream(mail.getToRecipients()).forEach(adress -> toRecipients.add(adress));
      if (mail.getCCRecipients() != null) {
        EmailAddressCollection ccRecipients = msg.getCcRecipients();
        Arrays.stream(mail.getCCRecipients()).forEach(adress -> ccRecipients.add(adress));
      }
      msg.send();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void sendSimpleMail(MailObject mail) {
    try {
      EmailMessage msg = new EmailMessage(exchangeService);
      if (mail.sendToAdditionalRecipient()) {
        mail.setCcRecipients(ArrayUtils.addAll(mail.getCCRecipients(),
            customMailProperties.getAdditionalRecipients()));
      }
      msg.setSubject(mail.getSubject());
      msg.setBody(MessageBody.getMessageBodyFromText(prepareMailBody(mail, mail.getTemplate())));
      EmailAddressCollection toRecipients = msg.getToRecipients();
      Arrays.stream(mail.getToRecipients()).forEach(adress -> toRecipients.add(adress));
      if (mail.getCCRecipients() != null) {
        EmailAddressCollection ccRecipients = msg.getCcRecipients();
        Arrays.stream(mail.getCCRecipients()).forEach(adress -> ccRecipients.add(adress));
      }
      msg.send();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private String prepareMailBody(MailObject mail, String templateName) {
    Context ctx = new Context();
    ctx.setVariables(mail.getValues());
    return templateEngine.process(templateName, ctx);
  }

}
