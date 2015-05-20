package de.maredit.tar.services;

import microsoft.exchange.webservices.data.enumeration.AffectedTaskOccurrence;
import microsoft.exchange.webservices.data.enumeration.SendCancellationsMode;
import microsoft.exchange.webservices.data.enumeration.DeleteMode;
import microsoft.exchange.webservices.data.property.complex.ItemId;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import de.maredit.tar.properties.ExchangeProperties;
import microsoft.exchange.webservices.data.property.complex.time.OlsonTimeZoneDefinition;
import microsoft.exchange.webservices.data.property.complex.time.TimeZoneDefinition;
import de.maredit.tar.models.Vacation;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.PropertySet;
import microsoft.exchange.webservices.data.core.service.folder.Folder;
import microsoft.exchange.webservices.data.core.service.item.Appointment;
import microsoft.exchange.webservices.data.core.service.schema.FolderSchema;
import microsoft.exchange.webservices.data.enumeration.BasePropertySet;
import microsoft.exchange.webservices.data.enumeration.FolderTraversal;
import microsoft.exchange.webservices.data.enumeration.LogicalOperator;
import microsoft.exchange.webservices.data.enumeration.SendInvitationsMode;
import microsoft.exchange.webservices.data.enumeration.WellKnownFolderName;
import microsoft.exchange.webservices.data.search.FindFoldersResults;
import microsoft.exchange.webservices.data.search.FolderView;
import microsoft.exchange.webservices.data.search.filter.SearchFilter;
import microsoft.exchange.webservices.data.search.filter.SearchFilter.SearchFilterCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.Date;
import java.util.TimeZone;

@Service
public class CalendarServiceExchangeImpl {

  private static final Logger LOG = LogManager.getLogger(CalendarServiceExchangeImpl.class);

  @Autowired
  private ExchangeService exchangeService;

  @Autowired
  private ExchangeProperties exchangeProperties;

  public void createAppointment(Vacation vacation) {
    try {
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
          LOG.error("Error finding calendar '{}'", exchangeProperties.getCalendar());
          folder = Folder.bind(exchangeService, WellKnownFolderName.Calendar);
        } else if (findFolderResults.getTotalCount() == 1) {
          folder = findFolderResults.getFolders().get(0);
        } else {
          LOG.error("Multiple calendars named '{}' found", exchangeProperties.getCalendar());
          folder = Folder.bind(exchangeService, WellKnownFolderName.Calendar);
        }
      } else {
        folder = Folder.bind(exchangeService, WellKnownFolderName.Calendar);
      }

      TimeZoneDefinition timezone = new OlsonTimeZoneDefinition(TimeZone.getDefault());
      Appointment appointment = new Appointment(exchangeService);
      appointment.setSubject("Urlaub");
      appointment.setIsAllDayEvent(true);
      appointment.setStart(Date.from(vacation.getFrom().atStartOfDay(ZoneId.systemDefault())
          .toInstant()));
      appointment.setStartTimeZone(timezone);
      appointment.setEnd(Date.from(vacation.getTo().atTime(23, 59, 59)
          .atZone(ZoneId.systemDefault()).toInstant()));
      appointment.setEndTimeZone(timezone);
      appointment.getRequiredAttendees().add(vacation.getUser().getMail());
      appointment.save(folder.getId(), SendInvitationsMode.SendToAllAndSaveCopy);
    } catch (Exception e) {
      LOG.error("Error creating appointment in Exchange", e);
    }
  }

  public void deleteAppointment(Vacation vacation) {
    try {
      // TODO get ItemId
      exchangeService.deleteItem(new ItemId(""), DeleteMode.SoftDelete,
          SendCancellationsMode.SendToAllAndSaveCopy, AffectedTaskOccurrence.AllOccurrences);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void modifiyAppointment(Vacation vacation) {
    try {
      // TODO get ItemId
      TimeZoneDefinition timezone = new OlsonTimeZoneDefinition(TimeZone.getDefault());
      Appointment appointment = Appointment.bind(exchangeService, new ItemId());
      appointment.load();
      appointment.setSubject("Urlaub");
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
      e.printStackTrace();
    }
  }
}
