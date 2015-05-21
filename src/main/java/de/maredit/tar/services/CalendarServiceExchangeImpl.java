package de.maredit.tar.services;

import de.maredit.tar.models.Vacation;
import de.maredit.tar.properties.ExchangeProperties;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.PropertySet;
import microsoft.exchange.webservices.data.core.service.folder.Folder;
import microsoft.exchange.webservices.data.core.service.item.Appointment;
import microsoft.exchange.webservices.data.core.service.schema.FolderSchema;
import microsoft.exchange.webservices.data.enumeration.AffectedTaskOccurrence;
import microsoft.exchange.webservices.data.enumeration.BasePropertySet;
import microsoft.exchange.webservices.data.enumeration.DeleteMode;
import microsoft.exchange.webservices.data.enumeration.FolderTraversal;
import microsoft.exchange.webservices.data.enumeration.LogicalOperator;
import microsoft.exchange.webservices.data.enumeration.SendCancellationsMode;
import microsoft.exchange.webservices.data.enumeration.SendInvitationsMode;
import microsoft.exchange.webservices.data.enumeration.WellKnownFolderName;
import microsoft.exchange.webservices.data.property.complex.ItemId;
import microsoft.exchange.webservices.data.property.complex.time.OlsonTimeZoneDefinition;
import microsoft.exchange.webservices.data.property.complex.time.TimeZoneDefinition;
import microsoft.exchange.webservices.data.search.FindFoldersResults;
import microsoft.exchange.webservices.data.search.FolderView;
import microsoft.exchange.webservices.data.search.filter.SearchFilter;
import microsoft.exchange.webservices.data.search.filter.SearchFilter.SearchFilterCollection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.Date;
import java.util.TimeZone;

@Service
@Profile("!dummyCalendarService")
public class CalendarServiceExchangeImpl implements CalendarService {

  private static final Logger LOG = LogManager.getLogger(CalendarServiceExchangeImpl.class);

  @Autowired
  private ExchangeService exchangeService;

  @Autowired
  private ExchangeProperties exchangeProperties;

  /* (non-Javadoc)
   * @see de.maredit.tar.services.CalendarService#createAppointment(de.maredit.tar.models.Vacation)
   */
  @Override
  public String createAppointment(Vacation vacation) {
    try {
      Folder folder = getCaldendarFolder();
      TimeZoneDefinition timezone = new OlsonTimeZoneDefinition(TimeZone.getDefault());
      Appointment appointment = new Appointment(exchangeService);
      appointment.setIsReminderSet(false);
      appointment.setIsResponseRequested(false);
      appointment.setSubject("Urlaub: " + vacation.getUser().getFullname());
      appointment.setIsAllDayEvent(true);
      appointment.setStart(Date.from(vacation.getFrom().atStartOfDay(ZoneId.systemDefault())
          .toInstant()));
      appointment.setStartTimeZone(timezone);
      appointment.setEnd(Date.from(vacation.getTo().atTime(23, 59, 59)
          .atZone(ZoneId.systemDefault()).toInstant()));
      appointment.setEndTimeZone(timezone);
      appointment.getRequiredAttendees().add(vacation.getUser().getMail());
      appointment.save(folder.getId(), SendInvitationsMode.SendToAllAndSaveCopy);
      return appointment.getId().toString();
    } catch (Exception e) {
      LOG.error("Error creating appointment in Exchange", e);
    }
    return null;
  }

  private Folder getCaldendarFolder() throws Exception {
    Folder folder;
    if (exchangeProperties.getCalendar() != null) {
      FolderView view = new FolderView(2);
      view.setPropertySet(new PropertySet(BasePropertySet.IdOnly));
      view.getPropertySet().add(FolderSchema.DisplayName);
      view.getPropertySet().add(FolderSchema.FolderClass);
      view.setTraversal(FolderTraversal.Deep);

      SearchFilterCollection filter =
          new SearchFilter.SearchFilterCollection(LogicalOperator.And);
      filter.add(new SearchFilter.IsEqualTo(FolderSchema.FolderClass, "IPF.Appointment"));
      filter.add(new SearchFilter.IsEqualTo(FolderSchema.DisplayName, exchangeProperties
          .getCalendar()));
      FindFoldersResults findFolderResults =
          exchangeService.findFolders(WellKnownFolderName.Root, filter, view);

      if (findFolderResults.getTotalCount() == 0) {
        LOG.error("Error finding calendar '{}'. Using default calendar", exchangeProperties.getCalendar());
        folder = Folder.bind(exchangeService, WellKnownFolderName.Calendar);
      } else if (findFolderResults.getTotalCount() == 1) {
        folder = findFolderResults.getFolders().get(0);
      } else {
        LOG.error("Multiple calendars named '{}' found. Using default calendar", exchangeProperties.getCalendar());
        folder = Folder.bind(exchangeService, WellKnownFolderName.Calendar);
      }
    } else {
      folder = Folder.bind(exchangeService, WellKnownFolderName.Calendar);
    }
    return folder;
  }

  /* (non-Javadoc)
   * @see de.maredit.tar.services.CalendarService#deleteAppointment(de.maredit.tar.models.Vacation)
   */
  @Override
  public void deleteAppointment(Vacation vacation) {
    try {
      exchangeService.deleteItem(new ItemId(vacation.getAppointmentId()), DeleteMode.HardDelete,
          SendCancellationsMode.SendToAllAndSaveCopy, AffectedTaskOccurrence.AllOccurrences);
    } catch (Exception e) {
      LOG.error("Error deleting appointment in Exchange", e);
    }
  }

  /* (non-Javadoc)
   * @see de.maredit.tar.services.CalendarService#modifiyAppointment(de.maredit.tar.models.Vacation)
   */
  @Override
  public void modifiyAppointment(Vacation vacation) {
    try {
      TimeZoneDefinition timezone = new OlsonTimeZoneDefinition(TimeZone.getDefault());
      Appointment appointment = Appointment.bind(exchangeService, new ItemId(vacation.getAppointmentId()));
      appointment.load();
      appointment.setIsAllDayEvent(true);
      appointment.setStart(Date.from(vacation.getFrom().atStartOfDay(ZoneId.systemDefault())
          .toInstant()));
      appointment.setStartTimeZone(timezone);
      appointment.setEnd(Date.from(vacation.getTo().atTime(23, 59, 59)
          .atZone(ZoneId.systemDefault()).toInstant()));
      appointment.setEndTimeZone(timezone);
      appointment.getRequiredAttendees().add(vacation.getUser().getMail());
      appointment.save(SendInvitationsMode.SendToAllAndSaveCopy);
    } catch (Exception e) {
      LOG.error("Error modifying appointment in Exchange", e);
    }
  }
}
