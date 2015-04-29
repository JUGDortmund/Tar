package de.maredit.tar.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.maredit.tar.Main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.mail.internet.MimeMessage;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Properties;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Main.class)
@ActiveProfiles("serviceTest")
public class MailServiceImplTest {

  private static final Logger LOG = LogManager.getLogger(MailServiceImplTest.class);

  @Autowired
  private MailServiceImpl mailService;

  private MailServiceImpl mockMailService = new MailServiceImpl();

  private SimpleMailMessage mailMessage = new SimpleMailMessage();

  private JavaMailSender mockMailSender = null;

  private SimpleMailMessage receivedMailMessage = new SimpleMailMessage();

  @Before
  public void setup() {
    mailMessage.setTo("test@maredit.de");
    mailMessage.setSubject("Test Subject");
    mailMessage.setText("This is a simple text for testing the email service!");
    LOG.debug("mailService = {}", (mailService == null ? "null" : mailService.toString()));

    mockMailSender = new JavaMailSender() {
      @Override
      public void send(SimpleMailMessage... msgs) throws MailException {}

      @Override
      public void send(SimpleMailMessage msg) throws MailException {
        receivedMailMessage.setSubject(msg.getSubject());
        receivedMailMessage.setText(msg.getText());
      }

      @Override
      public MimeMessage createMimeMessage() {
        return null;
      }

      @Override
      public MimeMessage createMimeMessage(InputStream paramInputStream) throws MailException {
        return null;
      }

      @Override
      public void send(MimeMessage paramMimeMessage) throws MailException {}

      @Override
      public void send(MimeMessage... paramArrayOfMimeMessage) throws MailException {}

      @Override
      public void send(MimeMessagePreparator paramMimeMessagePreparator) throws MailException {}

      @Override
      public void send(MimeMessagePreparator... paramArrayOfMimeMessagePreparator)
          throws MailException {}
    };
  }

  @Test
  public void sendMail() {
    assertNotNull("MailService cannot be null!!!", mockMailService);
    mockMailService.setJavaMailSender(mockMailSender);
    mockMailService.sendMail(mailMessage);
    assertEquals("Something wrong with sent message", mailMessage.getSubject(),
        receivedMailMessage.getSubject());
    assertEquals("Something wrong with sent message", mailMessage.getText(),
        receivedMailMessage.getText());
  }

  @Test
  public void mailSenderProperties() {
    String prefix = "spring.mail.";
    Properties p = readProperties();
    LOG.debug("Properties: {}", p.toString());

    MailSender ms = mailService.javaMailSender();
    JavaMailSenderImpl jms = null;
    if (ms instanceof JavaMailSenderImpl) {
      jms = (JavaMailSenderImpl) ms;
    }

    assertEquals(p.get(prefix + "host"), jms.getHost());
    assertEquals(p.get(prefix + "port"), String.valueOf(jms.getPort()));
    assertEquals(p.get(prefix + "username"), jms.getUsername());
  }

  private Properties readProperties() {
    Properties prop = new Properties();
    String properties =
        "spring.mail.host=localhost\n" + "spring.mail.port=2025\n" + "spring.mail.username=tar\n"
            + "spring.mail.password=";
    StringReader stReader = new StringReader(properties);
    try {
      prop.load(stReader);
    } catch (IOException e) {
      LOG.error("Error loading properties!", e);
    }
    return prop;
  }

}
