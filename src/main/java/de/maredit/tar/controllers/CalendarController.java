package de.maredit.tar.controllers;

import de.maredit.tar.models.CalendarEvent;
import de.maredit.tar.models.User;
import de.maredit.tar.models.Vacation;
import de.maredit.tar.models.enums.State;
import de.maredit.tar.properties.VersionProperties;
import de.maredit.tar.providers.VersionProvider;
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
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Created by czillmann on 29.04.15.
 */

@Controller
public class CalendarController {

	private static final Logger LOG = LogManager
			.getLogger(CalendarController.class);

	@Autowired
	private VacationRepository vacationRepository;

	@Autowired
	private ApplicationController applicationController;
	
	@Autowired
	private VersionProperties versionProperties;
	
	private VersionProvider versionProvider = new VersionProvider();
	
	@RequestMapping("/calendar")
	public String calendar(Model model) {
		List<Vacation> vacations = this.vacationRepository.findAll();
		User user = applicationController.getConnectedUser();
		
		model.addAttribute("vacations", vacations);
		model.addAttribute("loginUser", applicationController.getConnectedUser());
		model.addAttribute("appVersion", versionProvider.getApplicationVersion());
		model.addAttribute("buildnumber", versionProperties.getBuild());

		return "application/calendar";
	}

	@RequestMapping(value = "/calendar", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public List<CalendarEvent> getCalendarElements(
			@RequestParam(value = "start") String start,
			@RequestParam(value = "end") String end,
			@RequestParam(value = "showApproved", defaultValue = "true") Boolean showApproved,
			@RequestParam(value = "showPending", defaultValue = "false") Boolean showPending,
			@RequestParam(value = "showCanceled", defaultValue = "false") Boolean showCanceled,
			@RequestParam(value = "showRejected", defaultValue = "false") Boolean showRejected) {
		LocalDate startDate = LocalDate.parse(start,
				DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		LocalDate endDate = LocalDate.parse(end,
				DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		LOG.debug("Selecting vacation data from " + startDate + " to "
				+ endDate);

		List<State> states = new ArrayList<State>();
		if (showApproved) {
			states.add(State.APPROVED);
		}
		if (showPending) {
			states.add(State.WAITING_FOR_APPROVEMENT);
			states.add(State.REQUESTED_SUBSTITUTE);
		}
		if (showCanceled) {
			states.add(State.CANCELED);
		}
		if (showRejected) {
			states.add(State.REJECTED);
		}

		List<Vacation> vacations = this.vacationRepository
				.findVacationByFromBetweenAndStateInOrToBetweenAndStateIn(
						startDate, endDate, states, startDate, endDate, states);

		List<CalendarEvent> calendarEvents = vacations
				.stream()
				.map(vacation -> {
					CalendarEvent calendarEvent = new CalendarEvent(vacation);
					LOG.debug("added CalendarEvent '"
							+ calendarEvent.getTitle() + "' [start: "
							+ calendarEvent.getStart() + " - end: "
							+ calendarEvent.getEnd() + "] - "
							+ calendarEvent.getState());
					return calendarEvent;
				}).collect(toList());

		return calendarEvents;
	}
}
