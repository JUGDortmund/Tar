package de.maredit.tar.services.mail;

import net.fortuna.ical4j.model.ValidationException;

import org.junit.Assert;
import de.maredit.tar.models.User;
import de.maredit.tar.models.Vacation;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDate;

public class VacationApprovedMailTest {

  @Test
  public void testGetICalAttachment() throws IOException, ValidationException {
    Vacation vacation = new Vacation();
    vacation.setFrom(LocalDate.parse("2015-05-01"));
    vacation.setTo(LocalDate.parse("2015-05-02"));
    vacation.setUser(new User());
    VacationApprovedMail mail = new VacationApprovedMail(vacation);
    byte[] iCalData = mail.getICalAttachment();
    Assert.assertNotNull(iCalData);
    String iCal = new String(iCalData, "ISO-8859-1");
    Assert.assertTrue(iCal.startsWith("BEGIN:VCALENDAR"));
    Assert.assertTrue(iCal.trim().endsWith("END:VEVENT\r\nEND:VCALENDAR"));
    Assert.assertTrue(iCal.contains("DTSTART:20150501T000000"));
    Assert.assertTrue(iCal.contains("DTEND:20150503T000000"));
  }

}
