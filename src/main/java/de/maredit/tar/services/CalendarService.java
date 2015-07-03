package de.maredit.tar.services;

import de.maredit.tar.data.Vacation;
import de.maredit.tar.services.calendar.CalendarItem;


public interface CalendarService {

  CalendarItem createAppointment(Vacation vacation);

  void deleteAppointment(Vacation vacation);

  void modifiyAppointment(Vacation vacation);

}
