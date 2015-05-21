package de.maredit.tar.services;

import de.maredit.tar.models.Vacation;


public interface CalendarService {

  String createAppointment(Vacation vacation);

  void deleteAppointment(Vacation vacation);

  void modifiyAppointment(Vacation vacation);

}
