package de.maredit.tar.services.mail;

import de.maredit.tar.models.CalendarEvent;
import de.maredit.tar.models.User;
import de.maredit.tar.models.Vacation;
import de.maredit.tar.models.VacationEntitlement;
import de.maredit.tar.utils.ConversionUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class VacationModifiedMail implements MailObject {

  private static final String MAIL_TEMPLATE = "mail/vacationModified";
  private static final String MAIL_SUBJECT = "Urlaubsantrag geändert";

  private Map<String, Object> values = new HashMap<>();
  private String[] ccRecipients;
  private String[] toRecipients;

  public VacationModifiedMail(Vacation vacation, VacationEntitlement remaining, String urlToVacation, String comment, Vacation vacationBeforeChange, VacationEntitlement oldRemaining, User user) {
    values.put("employee_old", vacationBeforeChange.getUser().getFirstname());
    values.put("manager_old", vacationBeforeChange.getManager() == null ? "" : vacationBeforeChange.getManager().getFullname());
    values.put("substitute_old", vacationBeforeChange.getSubstitute() == null ? "" : vacationBeforeChange.getSubstitute()
        .getFullname());

    String fromDateOld = ConversionUtils.convertLocalDateToString(vacationBeforeChange.getFrom());
    String toDateOld = ConversionUtils.convertLocalDateToString(vacationBeforeChange.getTo());

    if ( vacationBeforeChange.isHalfDay() ) {
      switch (vacationBeforeChange.getTimeframe()) {
        case AFTERNOON:
          fromDateOld = fromDateOld + CalendarEvent.START_HALF_DAY_HOLIDAY_AFTERNOON;
          toDateOld = toDateOld + CalendarEvent.END_HALF_DAY_HOLIDAY_AFTERNOON;
          break;
        case MORNING:
          fromDateOld = fromDateOld + CalendarEvent.START_HALF_DAY_HOLIDAY_MORNING;
          toDateOld = toDateOld + CalendarEvent.END_HALF_DAY_HOLIDAY_MORNING;
          break;
        default:
      }
    }

    values.put("fromDate", fromDateOld);
    values.put("toDate", toDateOld);

    values.put("totalDays_old", vacationBeforeChange.getDays());
    values.put("leftDays_old", oldRemaining.getDays());
    values.put("leftDaysLastYear_old", oldRemaining.getDaysLastYear());

    values.put("employee", vacation.getUser().getFirstname());
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
    values.put("leftDays", remaining.getDays());
    values.put("leftDaysLastYear", remaining.getDaysLastYear());
    values.put("modifiedBy", user.getFullname());
    values.put("urlToVacation", urlToVacation);
    values.put("comment", comment);
    toRecipients = ArrayUtils.add(toRecipients, retrieveMail(vacation.getUser()));
    if (vacation.getSubstitute() != null) {
      ccRecipients = ArrayUtils.add(ccRecipients, retrieveMail(vacation.getSubstitute()));
    }
    ccRecipients = ArrayUtils.add(ccRecipients, retrieveMail(vacation.getManager()));
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
    return "VacationModifiedMail [getTemplate()=" + getTemplate() + ", getHtmlTemplate()="
           + getHtmlTemplate() + ", getValues()=" + getValues() + ", getCCRecipients()="
           + Arrays.toString(getCCRecipients()) + ", getSubject()=" + getSubject()
           + ", getToRecipients()=" + Arrays.toString(getToRecipients()) + "]";
  }

}
