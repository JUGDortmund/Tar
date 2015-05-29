package de.maredit.tar.services;

import de.maredit.tar.services.mail.Attachment;

import de.maredit.tar.services.mail.MailObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile({"dummyMailService"})
public class MailServiceDummyImpl implements MailService {

  private static final Logger LOG = LogManager.getLogger(MailServiceDummyImpl.class);

  @Autowired
  private MailMessageComposer mailMessageComposer;

  @Override
  public void sendSimpleMail(MailObject mail) {
    LOG.info("Mail to be send:\n {}", mail.toString());
    LOG.info("Mail Text:\n {}", mailMessageComposer.composeSimpleMailMessage(mail).getText());
  }

  @Override
  public void sendMail(MailObject mail, Attachment... attachments) {
    LOG.info("HTML mail to be send:\n {}", mail.toString());
    LOG.info("Mail Text:\n {}", mailMessageComposer.composeSimpleMailMessage(mail).getText());
  }
}
