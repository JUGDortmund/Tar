package de.maredit.tar.services;

import de.maredit.tar.Main;
import de.maredit.tar.services.mail.MailObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.dumbster.smtp.SimpleSmtpServer;
import com.dumbster.smtp.SmtpMessage;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Main.class)
@ActiveProfiles("serviceTest")
public class MailServiceImplTest {

  private static final Logger LOG = LogManager.getLogger(MailServiceImplTest.class);

  @Autowired
  private MailServiceSmtpImpl mailService;

  private MailObject mailObject;

  private SimpleSmtpServer server;

  @Before
  public void setup() {
    mailObject = new MailObject() {

      @Override
      public Map<String, Object> getValues() {
        Map<String, Object> content = new HashMap<>();
        content.put("content", "This is a simple text for testing the email service!");
        return content;
      }

      @Override
      public String[] getToRecipients() {
        String[] receipients = {"test@maredit.de"};
        return receipients;
      }

      @Override
      public String getTemplate() {
        return "mail/test";
      }

      @Override
      public String getSubject() {
        return "Test Subject";
      }

      @Override
      public String getHtmlTemplate() {
        return "mail/test";
      }

      @Override
      public String[] getCCRecipients() {
        return new String[0];
      }
    };
    server = SimpleSmtpServer.start(1600);
  }

  @After
  public void shutdown() {
    server.stop();
  }

  @Test
  public void sendMail() {
    mailService.sendMail(mailObject);
    Assert.assertEquals(1, server.getReceivedEmailSize());
    SmtpMessage mail = (SmtpMessage) server.getReceivedEmail().next();

    Assert.assertEquals("test@maredit.de", mail.getHeaderValue("To"));
    Assert.assertEquals("Test Subject", mail.getHeaderValue("Subject"));
  }
}
