package de.maredit.tar.services;

import de.maredit.tar.services.calendar.CalendarItem;

import de.maredit.tar.models.Vacation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("dummyCalendarService")
public class CalendarServiceDummyImpl implements CalendarService{

  private static final Logger LOG = LogManager.getLogger(CalendarServiceDummyImpl.class);
  
  @Override
  public CalendarItem createAppointment(Vacation vacation) {
    LOG.info("Creating appointment for vacation of {} from {} to {}", vacation.getUser().getFullname(), vacation.getFrom(), vacation.getTo());
    return null;
  }

  @Override
  public void deleteAppointment(Vacation vacation) {
    LOG.info("Deleting appointment for vacation of {} from {} to {}", vacation.getUser().getFullname(), vacation.getFrom(), vacation.getTo());
  }

  @Override
  public void modifiyAppointment(Vacation vacation) {
    LOG.info("Modifying appointment for vacation of {} from {} to {}", vacation.getUser().getFullname(), vacation.getFrom(), vacation.getTo());
  }

}
