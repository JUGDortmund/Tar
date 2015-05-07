package de.maredit.tar.services;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import de.maredit.tar.services.mail.MailObject;

@Service
@Profile({"prod", "demo", "serviceTest"})
@EnableConfigurationProperties(MailProperties.class)
public class MailServiceImpl implements MailService {

  private static final Logger LOG = LogManager.getLogger(MailServiceImpl.class);

  @Autowired
  private MailMessageComposer mailMessageComposer;

  @Autowired
  private MailProperties mailProperties;

  private JavaMailSender mailSender;

  @PostConstruct
  public void init() {
    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
    if (mailProperties != null && mailProperties.getHost() != null) {
      mailSender.setHost(mailProperties.getHost());
      if (mailProperties.getPort() != null) {
        mailSender.setPort(mailProperties.getPort());
      }
      mailSender.setUsername(mailProperties.getUsername());
      mailSender.setPassword(mailProperties.getPassword());
      this.mailSender = mailSender;
    }
  }

  @Override
  public void sendSimpleMail(MailObject mail) {
    SimpleMailMessage msg =
        new SimpleMailMessage(mailMessageComposer.composeSimpleMailMessage(mail));
    try {
      this.mailSender.send(msg);
    } catch (MailException ex) {
      LOG.error("Error sending mail", ex);
    }
  }


  @Override
  public void sendMail(MailObject mail) {
    try {
      mailSender.send(mailMessageComposer.composeMimeMailMessage(mail,
          mailSender.createMimeMessage()));
    } catch (MailException ex) {
      LOG.error("Error sending mail", ex);
    }
  }

}
