package de.maredit.tar.services;

import de.maredit.tar.data.Vacation;
import de.maredit.tar.services.calendar.CalendarItem;

import de.maredit.tar.models.CalendarEvent;
import de.maredit.tar.services.mail.Attachment;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.parameter.Cn;
import net.fortuna.ical4j.model.parameter.Role;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.util.UidGenerator;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

@Service
@Profile("iCalCalendarService")
public class CalendarServiceICalImpl implements CalendarService {

  private static final Logger LOG = LogManager.getLogger(CalendarServiceICalImpl.class);

  @Override
  public CalendarItem createAppointment(Vacation vacation) {
    try {
      Calendar ical = new Calendar();
      ical.getProperties().add(CalScale.GREGORIAN);
      ical.getProperties().add(Version.VERSION_2_0);
      ical.getProperties().add(new ProdId("-//maredit//TaR 1.0//DE"));

      DateTime start;
      DateTime end;

      if (vacation.isHalfDay()) {
        String startTime = "00:00:00";
        String endTime = "23:59:59";

        switch(vacation.getTimeframe()) {
          case AFTERNOON:
            startTime = CalendarEvent.START_HALF_DAY_HOLIDAY_AFTERNOON;
            endTime = CalendarEvent.END_HALF_DAY_HOLIDAY_AFTERNOON;
            break;
          case MORNING:
            startTime = CalendarEvent.START_HALF_DAY_HOLIDAY_AFTERNOON;
            endTime = CalendarEvent.END_HALF_DAY_HOLIDAY_AFTERNOON;
            break;
        }
        start =
            new DateTime(Date.from(vacation.getFrom().atTime(LocalTime.parse(startTime))
                                       .atZone(ZoneId.systemDefault()).toInstant()));
        end =
            new DateTime(Date.from(vacation.getTo().atTime(LocalTime.parse(endTime))
                                       .atZone(ZoneId.systemDefault()).toInstant()));
      }
      else {
        start =
            new DateTime(Date.from(vacation.getFrom().atStartOfDay(ZoneId.systemDefault())
                                       .toInstant()));
        end =
            new DateTime(Date.from(vacation.getTo().plusDays(1).atStartOfDay(ZoneId.systemDefault())
                                       .toInstant()));
      }

      VEvent meeting = new VEvent(start, end, "Urlaub " + vacation.getUser().getFullname());

      // generate unique identifier..
      UidGenerator ug = new UidGenerator("uidGen");
      Uid uid = ug.generateUid();
      meeting.getProperties().add(uid);

      // add attendees..
      Attendee user = new Attendee(URI.create("mailto:" + vacation.getUser().getMail()));
      user.getParameters().add(Role.REQ_PARTICIPANT);
      user.getParameters().add(new Cn(vacation.getUser().getFullname()));
      meeting.getProperties().add(user);
      ical.getComponents().add(meeting);
      ByteArrayOutputStream outStream = new ByteArrayOutputStream();
      CalendarOutputter outputter = new CalendarOutputter();
      outputter.output(ical, outStream);
      outStream.close();
      Attachment attachment = new Attachment();
      attachment.setData(outStream.toByteArray());
      attachment.setFilename("vacation.ics");
      attachment.setMimeType("text/calendar");
      CalendarItem calendarItem = new CalendarItem();
      calendarItem.setMailAttachment(attachment);
      return calendarItem;
    } catch (IOException | ValidationException e) {
      LOG.error("Error creating iCal object", e);
    }
    return new CalendarItem();
  }

  @Override
  public void deleteAppointment(Vacation vacation) {

  }

  @Override
  public void modifiyAppointment(Vacation vacation) {

  }

}
