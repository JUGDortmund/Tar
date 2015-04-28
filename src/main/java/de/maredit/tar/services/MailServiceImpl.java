package de.maredit.tar.services;

import de.maredit.tar.models.Vacation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.mail.internet.MimeMessage;

@Configuration
@Profile("!dev")
@EnableConfigurationProperties(MailProperties.class)
public class MailServiceImpl implements MailService {

  private static final Logger LOG = LogManager.getLogger(MailServiceImpl.class);

  @Autowired
  private MailMessageComposer mailMessageComposer;

  @Autowired
  private MailProperties mailProperties;

  private JavaMailSender javaMailSender;

  public void setJavaMailSender(JavaMailSender mailSender) {
    this.javaMailSender = mailSender;
  }

  @Override
  public void sendMail(SimpleMailMessage mailMessage) {
    SimpleMailMessage msg = new SimpleMailMessage(mailMessage);
    try {
      this.javaMailSender.send(msg);
    } catch (MailException ex) {
      handleMailException(msg, ex);
    }
  }

  private void handleMailException(SimpleMailMessage msg, MailException ex) {
    if ("dev".equals(mailProperties.getProperties().get("environment"))) {
      LOG.debug("Could not connect to SMTP-Server [host: {}, port:{}] in develop environment. "
          + "Tried to send the following email:\n {}", mailProperties.getHost(),
          mailProperties.getPort(), msg.toString());
    } else {
      LOG.error(ex.getMessage());
    }
  }

  @Override
  public void sendMail(MimeMessage mimeMessage) {
    javaMailSender.send(mimeMessage);
  }

  @Bean
  public JavaMailSender javaMailSender() {
    if (javaMailSender == null) {
      JavaMailSenderImpl jMailSender = new JavaMailSenderImpl();
      if (mailProperties != null) {
        jMailSender.setHost(mailProperties.getHost());
        jMailSender.setPort(mailProperties.getPort());
        jMailSender.setUsername(mailProperties.getUsername());
        jMailSender.setPassword(mailProperties.getPassword());
      } else {
        LOG.error("mailProperties == null!. Apparently it has not been properly autowired!");
      }
      this.javaMailSender = jMailSender;
    }
    return javaMailSender;
  }

  @Override
  public void sendSimpleMail(Vacation vacation) {
    sendMail(mailMessageComposer.composeSimpleMailMessage(vacation));
  }

  @Override
  public void sendMimeMail(Vacation vacation) {
    JavaMailSenderImpl javaMailSender = null;
    if (this.javaMailSender instanceof JavaMailSenderImpl) {
      javaMailSender = (JavaMailSenderImpl) this.javaMailSender;
    }
    sendMail(mailMessageComposer.composeMimeMailMessage(vacation,
        javaMailSender.createMimeMessage()));
  }

}
