package de.maredit.tar.services;

import net.fortuna.ical4j.model.ValidationException;

import java.io.IOException;

import org.apache.commons.lang3.ArrayUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import de.maredit.tar.services.mail.MailObject;

@Service
@Profile({"dev", "test"})
public class MailServiceDummyImpl implements MailService {

  private static final Logger LOG = LogManager.getLogger(MailServiceDummyImpl.class);

  @Autowired
  private MailMessageComposer mailMessageComposer;

  @Override
  public void sendSimpleMail(MailObject mail) {
    LOG.info(
        "Mail to be send:\n {}",mail.toString());
    LOG.info(
        "Mail Text:\n {}",mailMessageComposer.composeSimpleMailMessage(mail).getText());
  }

  @Override
  public void sendMail(MailObject mail) {
    LOG.info(
        "HTML mail to be send:\n {}",mail.toString());
    LOG.info(
        "Mail Text:\n {}",mailMessageComposer.composeSimpleMailMessage(mail).getText());
    try {
      LOG.debug("iCal:\n{}", new String(ArrayUtils.nullToEmpty(mail.getICalAttachment()), "ISO-8859-1"));
    } catch (IOException | ValidationException e) {
      LOG.error("Error creating iCal", e);
    }
  }
}
