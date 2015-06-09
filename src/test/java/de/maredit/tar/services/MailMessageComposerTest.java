package de.maredit.tar.services;

import de.maredit.tar.services.mail.VacationCreateMail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import de.maredit.tar.Main;
import de.maredit.tar.models.User;
import de.maredit.tar.models.Vacation;

import org.junit.Before;
import org.junit.Ignore;
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
    standardVacation.setAuthor(createDummyUser("Mark"));
    standardVacation.setSubstitute(createDummyUser("Luke"));
    standardVacation.setManager(createDummyUser("John"));
    standardVacation.setFrom(LocalDate.of(2015, 04, 27));
    standardVacation.setTo(LocalDate.of(2016, 05, 30));
  }

  @Test
  public void recipientsInComposedMail() {
    Vacation vacation = standardVacation;
    String[] expectedArray = {/* User */"Mark@maredit.de", /* Substitute */"Luke@maredit.de", /* Manager */
    "John@maredit.de"};

    String[] actualArray = mailComposer.composeSimpleMailMessage(new VacationCreateMail(vacation, "http://www.maredit.de", "Kommentar")).getTo();
    assertEquals(expectedArray[1], actualArray[0]);
    assertEquals(expectedArray[2], actualArray[1]);

    actualArray = mailComposer.composeSimpleMailMessage(new VacationCreateMail(vacation, "http://www.maredit.de", "Kommentar")).getCc();
    assertEquals(expectedArray[0], actualArray[0]);
  }

  @Test
  public void changedMailAdressesInComposedMail() {
    SimpleMailMessage mailMessage = mailComposer.composeSimpleMailMessage(new VacationCreateMail(standardVacation, "http://www.maredit.de", "Kommentar"));

    assertEquals("Mark@maredit.de", mailMessage.getCc()[0]);
    assertEquals("Luke@maredit.de", mailMessage.getTo()[0]);
    assertEquals("John@maredit.de", mailMessage.getTo()[1]);

    standardVacation.getUser().setMail("JohnnyEnglish@maredit.de");
    standardVacation.getManager().setMail("manager@maredit.de");
    standardVacation.getSubstitute().setMail("substitute@maredit.de");
    mailMessage = mailComposer.composeSimpleMailMessage(new VacationCreateMail(standardVacation, "http://www.maredit.de", "Kommentar"));

    assertEquals("JohnnyEnglish@maredit.de", mailMessage.getCc()[0]);
    assertEquals("substitute@maredit.de", mailMessage.getTo()[0]);
    assertEquals("manager@maredit.de", mailMessage.getTo()[1]);
  }

  @Test
  public void placeHoldersInTemplateCorrectlyReplaced() {
    standardVacation.setFrom(LocalDate.of(2015, 04, 27));
    standardVacation.setTo(LocalDate.of(2016, 05, 30));
    standardVacation.setDays(13);
    standardVacation.setDaysLeft(11);

    SimpleMailMessage mailMessage = mailComposer.composeSimpleMailMessage(new VacationCreateMail(standardVacation, "http://www.maredit.de", "Kommentar"));
    String actualBodyText = mailMessage.getText();
    
    String errorMessage = "Expected substring not found in body text: ";

    String expectedSubString = "Hallo, <span>John Surname</span>";
    assertTrue(errorMessage + expectedSubString, actualBodyText.contains(expectedSubString));

    expectedSubString = "<b>Name des Vorgesetzten:</b> <span>John Surname</span>";
    assertTrue(errorMessage + expectedSubString, actualBodyText.contains(expectedSubString));

    expectedSubString = "<b>Name des Stellvertreters:</b> <span>Luke Surname</span>";
    assertTrue(errorMessage + expectedSubString, actualBodyText.contains(expectedSubString));

    expectedSubString = "Von: <span>27.04.2015</span>";
    assertTrue(errorMessage + expectedSubString, actualBodyText.contains(expectedSubString));

    expectedSubString = "Bis: <span>30.05.2016</span>";
    assertTrue(errorMessage + expectedSubString, actualBodyText.contains(expectedSubString));

    expectedSubString = "<b>Summe der Urlaubstage:</b> <span>13.0</span>";
    assertTrue(errorMessage + expectedSubString, actualBodyText.contains(expectedSubString));

    expectedSubString = "<b>Summe der Resturlaubstage:</b> <span>11.0</span>";
    assertTrue(errorMessage + expectedSubString, actualBodyText.contains(expectedSubString));
  }

  private User createDummyUser(String name) {
    User user = new User();
    user.setFirstname(name);
    user.setLastname("Surname");
    user.setMail(name + "@maredit.de");
    return user;
  }
}
