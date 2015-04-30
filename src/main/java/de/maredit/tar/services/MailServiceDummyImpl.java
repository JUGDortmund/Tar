package de.maredit.tar.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import de.maredit.tar.services.mail.MailObject;

@Service
@Profile({"dev", "test"})
public class MailServiceDummyImpl implements MailService {

  private static final Logger LOG = LogManager.getLogger(MailServiceDummyImpl.class);

  @Override
  public void sendSimpleMail(MailObject mail) {
    LOG.info(
        "Mail to be send:\n {}",mail.toString());
  }

  @Override
  public void sendMail(MailObject mail) {
    LOG.info(
        "HTML mail to be send:\n {}",mail.toString());
  }
}
