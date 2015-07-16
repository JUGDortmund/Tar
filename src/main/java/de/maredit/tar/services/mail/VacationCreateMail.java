package de.maredit.tar.services.mail;

import de.maredit.tar.models.CalendarEvent;
import de.maredit.tar.data.Vacation;
import de.maredit.tar.models.VacationEntitlement;
import de.maredit.tar.utils.ConversionUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class VacationCreateMail implements MailObject {

  private static final String MAIL_TEMPLATE = "mail/vacationCreated";
  private static final String MAIL_SUBJECT = "Urlaubsantrag";

  private Map<String, Object> values = new HashMap<>();
  private String[] ccRecipients;
  private String[] toRecipients;


  public VacationCreateMail(Vacation vacation, VacationEntitlement entitlement, String urlToVacation, String comment) {
    values.put("employee", vacation.getUser().getFullname());
    values.put("manager", vacation.getManager() == null ? "" : vacation.getManager().getFullname());
    values.put("substitute", vacation.getSubstitute() == null ? "" : vacation.getSubstitute()
        .getFullname());

    String fromDate = ConversionUtils.convertLocalDateToString(vacation.getFrom());
    String toDate = ConversionUtils.convertLocalDateToString(vacation.getTo());

    if ( vacation.isHalfDay() ) {
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

    values.put("totalDays", vacation.getDays());
    values.put("leftDays", entitlement.getDays());
    values.put("leftDaysLastYear", entitlement.getDaysLastYear());
    if (!vacation.getAuthor().equals(vacation.getUser())) {
      values.put("createdBy", vacation.getAuthor().getFullname());
    }
    values.put("urlToVacation", urlToVacation);
    values.put("comment", comment);
    ccRecipients = ArrayUtils.add(ccRecipients, retrieveMail(vacation.getUser()));
    if (vacation.getSubstitute() != null) {
      toRecipients = ArrayUtils.add(toRecipients, retrieveMail(vacation.getSubstitute()));
    }
    toRecipients = ArrayUtils.add(toRecipients, retrieveMail(vacation.getManager()));
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
