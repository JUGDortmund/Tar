package de.maredit.tar.services.mail;

import de.maredit.tar.models.CalendarEvent;
import de.maredit.tar.models.Vacation;
import de.maredit.tar.models.VacationEntitlement;
import de.maredit.tar.utils.ConversionUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.net.SocketException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class VacationApprovedMail implements MailObject {

  private static final String MAIL_TEMPLATE = "mail/vacationApproved";
  private static final String MAIL_SUBJECT = "Urlaub genehmigt";

  private Map<String, Object> values = new HashMap<>();
  private String[] ccRecipients;
  private String[] toRecipients;

  public VacationApprovedMail(Vacation vacation, VacationEntitlement entitlement, String urlToVacation, String comment) throws SocketException {
    values.put("employee", vacation.getUser().getFirstname());
    values.put("substitute", vacation.getSubstitute() == null ? "" : vacation.getSubstitute()
        .getFullname());
    values.put("manager", vacation.getManager() == null ? "" : vacation.getManager().getFullname());

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
    values.put("urlToVacation", urlToVacation);
    values.put("comment", comment);
    toRecipients = ArrayUtils.add(toRecipients, retrieveMail(vacation.getUser()));
    if (vacation.getSubstitute() != null) {
      ccRecipients = ArrayUtils.add(ccRecipients, retrieveMail(vacation.getSubstitute()));
    }
    ccRecipients = ArrayUtils.add(ccRecipients, retrieveMail(vacation.getManager()));
  }

  @Override
  public void setCcRecipients(String[] ccRecipients) {
    this.ccRecipients = ccRecipients;
  }

  @Override
  public boolean sendToAdditionalRecipient() {
    return true;
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
    return "VacationApprovedMail [getTemplate()=" + getTemplate() + ", getHtmlTemplate()="
        + getHtmlTemplate() + ", getValues()=" + getValues() + ", getCCRecipients()="
        + Arrays.toString(getCCRecipients()) + ", getSubject()=" + getSubject()
        + ", getToRecipients()=" + Arrays.toString(getToRecipients()) + "]";
  }
}
