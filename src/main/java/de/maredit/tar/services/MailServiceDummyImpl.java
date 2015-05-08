package de.maredit.tar.services;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import de.maredit.tar.properties.CustomMailProperties;
import de.maredit.tar.services.mail.MailObject;

import java.util.ArrayList;
import java.util.List;

@Service
@Profile({"dev", "test"})
@EnableConfigurationProperties(CustomMailProperties.class)
public class MailServiceDummyImpl implements MailService {

  private static final Logger LOG = LogManager.getLogger(MailServiceDummyImpl.class);

  @Autowired
  private CustomMailProperties customMailProperties;

  @Override
  public void sendSimpleMail(MailObject mail) {
    if(mail.sendToAdditionalRecipient()) {
      ArrayUtils.addAll(mail.getCCRecipients(),
                        customMailProperties.getAdditionalrecipients().toArray());
    }
    LOG.info(
        "Mail to be send:\n {}",mail.toString());
  }

  @Override
  public void sendMail(MailObject mail) {
    if(mail.sendToAdditionalRecipient()) {
      String[] ccRecipients = mail.getCCRecipients();

      for ( String recipient : customMailProperties.getAdditionalrecipients() ){
        List<String> recipientList = new ArrayList<String>(CollectionUtils.arrayToList(ccRecipients));
        recipientList.add(recipient);
        ccRecipients = recipientList.toArray(ccRecipients);
      }

      mail.setCcRecipients(ccRecipients);
    }
    LOG.info(
        "HTML mail to be send:\n {}",mail.toString());
  }
}
