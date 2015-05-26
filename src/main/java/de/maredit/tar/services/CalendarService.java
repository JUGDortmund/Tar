package de.maredit.tar.services;

import de.maredit.tar.services.calendar.CalendarItem;

import de.maredit.tar.models.Vacation;


public interface CalendarService {

  CalendarItem createAppointment(Vacation vacation);

  void deleteAppointment(Vacation vacation);

  void modifiyAppointment(Vacation vacation);

}
