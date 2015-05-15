package de.maredit.tar.services;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import de.maredit.tar.properties.CustomMailProperties;
import de.maredit.tar.services.mail.MailObject;

@Service
@Profile({"prod", "demo", "serviceTest"})
@EnableConfigurationProperties(CustomMailProperties.class)
public class MailServiceImpl implements MailService {

  private static final Logger LOG = LogManager.getLogger(MailServiceImpl.class);

  @Autowired
  private MailMessageComposer mailMessageComposer;

  @Autowired
  private CustomMailProperties customMailProperties;

  private JavaMailSender mailSender;

  @PostConstruct
  public void init() {
    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
    if (customMailProperties != null && customMailProperties.getHost() != null) {
      mailSender.setHost(customMailProperties.getHost());
      if (customMailProperties.getPort() != null) {
        mailSender.setPort(customMailProperties.getPort());
      }
      mailSender.setUsername(customMailProperties.getUsername());
      mailSender.setPassword(customMailProperties.getPassword());
      this.mailSender = mailSender;
    }
  }

  @Override
  public void sendSimpleMail(MailObject mail) {
    SimpleMailMessage msg =
        new SimpleMailMessage(mailMessageComposer.composeSimpleMailMessage(mail));
    try {
      if(mail.sendToAdditionalRecipient()) {
        mail.setCcRecipients(ArrayUtils.addAll(mail.getCCRecipients(), customMailProperties.getAdditionalRecipients()));
      }
      this.mailSender.send(msg);
    } catch (MailException ex) {
      LOG.error("Error sending mail", ex);
    }
  }

  @Override
  public void sendMail(MailObject mail) {
    try {
      if(mail.sendToAdditionalRecipient()) {
        mail.setCcRecipients(ArrayUtils.addAll(mail.getCCRecipients(), customMailProperties.getAdditionalRecipients()));
      }
      mailSender.send(mailMessageComposer.composeMimeMailMessage(mail,
          mailSender.createMimeMessage()));
    } catch (MailException ex) {
      LOG.error("Error sending mail", ex);
    }
  }

}
