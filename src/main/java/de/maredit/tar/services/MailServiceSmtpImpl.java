package de.maredit.tar.services;

import de.maredit.tar.services.mail.Attachment;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import de.maredit.tar.properties.CustomMailProperties;
import de.maredit.tar.services.mail.MailObject;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Profile({"smtpMailService"})
@EnableConfigurationProperties(CustomMailProperties.class)
public class MailServiceSmtpImpl implements MailService {

  private static final Logger LOG = LogManager.getLogger(MailServiceSmtpImpl.class);

  @Autowired
  private MailMessageComposer mailMessageComposer;

  @Autowired
  private CustomMailProperties customMailProperties;

  @Autowired
  private JavaMailSender mailSender;

  @Override
  public void sendSimpleMail(MailObject mail) {
    SimpleMailMessage msg =
        new SimpleMailMessage(mailMessageComposer.composeSimpleMailMessage(mail));
    try {
      if(mail.sendToAdditionalRecipient()) {
        mail.setCcRecipients(ArrayUtils.addAll(mail.getCCRecipients(), customMailProperties.getAdditionalRecipients()));
      }
      if (customMailProperties.getSender() != null) {
        msg.setFrom(customMailProperties.getSender());
      }
      this.mailSender.send(msg);
    } catch (MailException ex) {
      LOG.error("Error sending mail", ex);
    }
  }

  @Override
  public void sendMail(MailObject mail, Attachment... attachments) {
    try {
      if(mail.sendToAdditionalRecipient()) {
        mail.setCcRecipients(ArrayUtils.addAll(mail.getCCRecipients(), customMailProperties.getAdditionalRecipients()));
      }
      MimeMessage msg = mailMessageComposer.composeMimeMailMessage(mail,
          mailSender.createMimeMessage(), attachments);
      if (customMailProperties.getSender() != null) {
        msg.setFrom(customMailProperties.getSender());
      }
      mailSender.send(msg);
    } catch (MailException | MessagingException ex) {
      LOG.error("Error sending mail", ex);
    }
  }

}
