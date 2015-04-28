package de.maredit.tar.services;

import de.maredit.tar.models.Vacation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.SimpleMailMessage;

import javax.mail.internet.MimeMessage;

@Configuration
@Profile({"dev", "test"})
@EnableConfigurationProperties(MailProperties.class)
public class MailServiceDummyImpl implements MailService {

  private static final Logger LOG = LogManager.getLogger(MailServiceDummyImpl.class);

  @Autowired
  private MailProperties mailProperties;

  @Autowired
  private MailMessageComposer mailMessageComposer;

  @Override
  public void sendMail(SimpleMailMessage mailMessage) {
    SimpleMailMessage msg = new SimpleMailMessage(mailMessage);
    LOG.debug(
        "This is the dummy implementation of a SMTP-Server [host: {}, port:{}] in the develop environment. ;-) "
            + "Let's pretend I've just sent the following email:\n {}", mailProperties.getHost(),
        mailProperties.getPort(), msg.toString());
  }

  @Override
  public void sendMail(MimeMessage mimeMessage) {
    LOG.debug("This is the dummy implementation of send(MimeMessage)");
  }

  @Override
  public void sendSimpleMail(Vacation vacation) {
    sendMail(mailMessageComposer.composeSimpleMailMessage(vacation));
  }

  @Override
  public void sendMimeMail(Vacation vacation) {
    sendSimpleMail(vacation);
  }
}
