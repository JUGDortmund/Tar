package de.maredit.tar.services.mail;

import de.maredit.tar.data.ManualEntry;
import de.maredit.tar.data.UserVacationAccount;
import de.maredit.tar.data.Vacation;
import de.maredit.tar.models.CalendarEvent;
import de.maredit.tar.models.VacationEntitlement;
import de.maredit.tar.models.enums.ManualEntryType;
import de.maredit.tar.utils.ConversionUtils;

import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class ManualEntryCreateMail implements MailObject {

  private static final String MAIL_TEMPLATE = "mail/manualEntryCreated";
  private static final String MAIL_SUBJECT = "Manuelle Korrekturbuchung";

  private Map<String, Object> values = new HashMap<>();
  private String[] ccRecipients;
  private String[] toRecipients;


  public ManualEntryCreateMail(ManualEntry manualEntry, VacationEntitlement entitlement,
                               String urlToOverview) {
    values.put("employee", manualEntry.getUser().getFullname());
    values.put("manager", manualEntry.getAuthor().getFullname());
    values.put("manualEntryType", manualEntry.getType().get());

    Vacation vacation = manualEntry.getVacation();
    values.put("isVacationReference", vacation != null );

    if (vacation != null){
      String fromDate = ConversionUtils.convertLocalDateToString(vacation.getFrom());
      String toDate = ConversionUtils.convertLocalDateToString(vacation.getTo());

      if (vacation.isHalfDay()) {
        switch (vacation.getTimeframe()) {
          case AFTERNOON:
            fromDate = fromDate + CalendarEvent.START_HALF_DAY_HOLIDAY_AFTERNOON;
            toDate = toDate + CalendarEvent.END_HALF_DAY_HOLIDAY_AFTERNOON;
            break;
          case MORNING:
            fromDate = fromDate + CalendarEvent.START_HALF_DAY_HOLIDAY_MORNING;
            toDate = toDate + CalendarEvent.END_HALF_DAY_HOLIDAY_MORNING;
            break;
          default:
        }
      }
      values.put("fromDate", fromDate);
      values.put("toDate", toDate);
    } else {
      values.put("year", manualEntry.getYear());
    }

    values.put("entryDays", manualEntry.getDays());
    values.put("leftDays", entitlement.getDays());
    values.put("leftDaysLastYear", entitlement.getDaysLastYear());

    values.put("comment", manualEntry.getDescription());
    values.put("urlToOverview", urlToOverview);


    ccRecipients = ArrayUtils.add(ccRecipients, retrieveMail(manualEntry.getAuthor()));
    toRecipients = ArrayUtils.add(ccRecipients, retrieveMail(manualEntry.getUser()));
  }

  @Override
  public String getTemplate() {
    return MAIL_TEMPLATE;
  }

  @Override
  public String getHtmlTemplate() {
    return MAIL_TEMPLATE;
  }

  @Override
  public Map<String, Object> getValues() {
    return values;
  }

  @Override
  public String[] getCCRecipients() {
    return ccRecipients;
  }

  @Override
  public String getSubject() {
    return MAIL_SUBJECT;
  }

  @Override
  public String[] getToRecipients() {
    return toRecipients;
  }

  @Override
  public String toString() {
    return "VacationCreateMail [getTemplate()=" + getTemplate() + ", getHtmlTemplate()="
        + getHtmlTemplate() + ", getValues()=" + getValues() + ", getCCRecipients()="
        + Arrays.toString(getCCRecipients()) + ", getSubject()=" + getSubject()
        + ", getToRecipients()=" + Arrays.toString(getToRecipients()) + "]";
  }
}
