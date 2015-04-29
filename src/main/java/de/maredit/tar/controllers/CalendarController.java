package de.maredit.tar.controllers;

import de.maredit.tar.models.CalendarEvent;
import de.maredit.tar.models.Vacation;
import de.maredit.tar.repositories.VacationRepository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Created by czillmann on 29.04.15.
 */

@Controller
public class CalendarController {

  private static final Logger LOG = LogManager.getLogger(CalendarController.class);

  @Autowired
  private VacationRepository vacationRepository;

  @RequestMapping("/calendar")
  public String calendar(Model model) {
    List<Vacation> vacations = this.vacationRepository.findAll();
    model.addAttribute("vacations", vacations);

    return "application/calendar";
  }

  @RequestMapping(value = "/calendar", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public List<CalendarEvent> getCalendarElements(@RequestParam(value = "start") String start,
                                                 @RequestParam(value = "end") String end) {
    LocalDate startDate = LocalDate.parse(start, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    LocalDate endDate = LocalDate.parse(end, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    LOG.info("Selecting vacation data from " + startDate + " to " + endDate);
    List<Vacation>
        vacations =
        this.vacationRepository
            .findVacationByFromBetweenOrToBetween(startDate, endDate, startDate, endDate);

    List<CalendarEvent>
        calendarEvents =
        vacations.stream().map(vacation -> {
          CalendarEvent calendarEvent = new CalendarEvent(vacation);
          LOG.info(
              "added CalendarEvent '" + calendarEvent.getTitle() + "' [start: " + calendarEvent
                  .getStart() + " - end: "
              + calendarEvent.getEnd() + "]");
          return calendarEvent;
        }).collect(toList());

    return calendarEvents;
  }
}
