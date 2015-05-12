package de.maredit.tar.services;

import de.maredit.tar.properties.CustomMailProperties;
import de.maredit.tar.services.mail.MailObject;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile({"dev", "test"})
@EnableConfigurationProperties(CustomMailProperties.class)
public class MailServiceDummyImpl implements MailService {

  private static final Logger LOG = LogManager.getLogger(MailServiceDummyImpl.class);

  @Autowired
  private CustomMailProperties customMailProperties;

  private MailMessageComposer mailMessageComposer;

  @Override
  public void sendSimpleMail(MailObject mail) {
    if(mail.sendToAdditionalRecipient()) {
      mail.setCcRecipients(ArrayUtils.addAll(mail.getCCRecipients(), customMailProperties.getAdditionalRecipients()));
    }
    LOG.info(
        "Mail to be send:\n {}",mail.toString());
    LOG.info(
        "Mail Text:\n {}",mailMessageComposer.composeSimpleMailMessage(mail).getText());
  }

  @Override
  public void sendMail(MailObject mail) {
    if(mail.sendToAdditionalRecipient()) {
      mail.setCcRecipients(ArrayUtils.addAll(mail.getCCRecipients(), customMailProperties.getAdditionalRecipients()));
    }
    LOG.info(
        "HTML mail to be send:\n {}",mail.toString());
    LOG.info(
        "Mail Text:\n {}",mailMessageComposer.composeSimpleMailMessage(mail).getText());
  }
}
