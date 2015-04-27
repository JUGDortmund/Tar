package de.maredit.tar.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.maredit.tar.Main;
import de.maredit.tar.models.User;
import de.maredit.tar.models.Vacation;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDate;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Main.class)
@ActiveProfiles("test")
public class MailMessageComposerTest {

  @Autowired
  private MailMessageComposer mailComposer;

  private Vacation standardVacation = new Vacation();

  @Before
  public void setup() {
    standardVacation.setUser(createDummyUser("Mark"));
    standardVacation.setSubstitute(createDummyUser("Luke"));
    standardVacation.setManager(createDummyUser("John"));
  }

  @Test
  public void recipientsInComposedMail() {
    // Given
    Vacation vacation = standardVacation;
    String[] expectedArray = {/* User */"Mark@maredit.de", /* Substitute */"Luke@maredit.de", /* Manager */
    "John@maredit.de"};

    // When
    String[] actualArray = mailComposer.composeMail(vacation).getTo();

    // Then
    assertEquals(expectedArray[0], actualArray[0]);
    assertEquals(expectedArray[1], actualArray[1]);
    assertEquals(expectedArray[2], actualArray[2]);
  }

  @Test
  public void changedMailAdressesInComposedMail() {
    SimpleMailMessage mailMessage = mailComposer.composeMail(standardVacation);

    assertEquals("Mark@maredit.de", mailMessage.getTo()[0]);
    assertEquals("Luke@maredit.de", mailMessage.getTo()[1]);
    assertEquals("John@maredit.de", mailMessage.getTo()[2]);

    standardVacation.getUser().setMail("JohnnyEnglish@maredit.de");
    standardVacation.getManager().setMail("manager@maredit.de");
    standardVacation.getSubstitute().setMail("substitute@maredit.de");
    mailMessage = mailComposer.composeMail(standardVacation);

    assertEquals("JohnnyEnglish@maredit.de", mailMessage.getTo()[0]);
    assertEquals("substitute@maredit.de", mailMessage.getTo()[1]);
    assertEquals("manager@maredit.de", mailMessage.getTo()[2]);
  }

  @Test
  public void placeHoldersInTemplateCorrectlyReplaced() {
    standardVacation.setFrom(LocalDate.of(2015, 04, 27));
    standardVacation.setTo(LocalDate.of(2016, 05, 30));
    standardVacation.setDays(13);
    standardVacation.setDaysLeft(11);

    SimpleMailMessage mailMessage = mailComposer.composeMail(standardVacation);
    String actualBodyText = mailMessage.getText();

    String errorMessage = "Expected substring not found in body text: ";

    String expectedSubString = "Hallo, <span>Mark</span>";
    assertTrue(errorMessage + expectedSubString, actualBodyText.contains(expectedSubString));

    expectedSubString = "<b>Name des Projektleiters:</b> <span>John</span>";
    assertTrue(errorMessage + expectedSubString, actualBodyText.contains(expectedSubString));

    expectedSubString = "<b>Name des Stellvertreters:</b> <span>Luke</span>";
    assertTrue(errorMessage + expectedSubString, actualBodyText.contains(expectedSubString));

    expectedSubString = "Von: <span>2015-04-27</span>";
    assertTrue(errorMessage + expectedSubString, actualBodyText.contains(expectedSubString));

    expectedSubString = "Bis: <span>2016-05-30</span>";
    assertTrue(errorMessage + expectedSubString, actualBodyText.contains(expectedSubString));

    expectedSubString = "<b>Summe der Urlaubstage:</b> <span>13.0</span>";
    assertTrue(errorMessage + expectedSubString, actualBodyText.contains(expectedSubString));

    expectedSubString = "<b>Summe der Resturlaubstage:</b> <span>11.0</span>";
    assertTrue(errorMessage + expectedSubString, actualBodyText.contains(expectedSubString));
  }

  private User createDummyUser(String name) {
    User user = new User();
    user.setFirstName(name);
    user.setMail(name + "@maredit.de");
    return user;
  }
}
