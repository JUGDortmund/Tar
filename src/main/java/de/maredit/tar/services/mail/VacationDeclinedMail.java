package de.maredit.tar.services.mail;

import de.maredit.tar.models.VacationEntitlement;

import de.maredit.tar.models.Vacation;
import de.maredit.tar.utils.ConversionUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class VacationDeclinedMail implements MailObject {

  private static final String MAIL_TEMPLATE = "mail/vacationDeclined";
  private static final String MAIL_SUBJECT = "Urlaub abgelehnt";

  private Map<String, Object> values = new HashMap<>();
  private String[] ccRecipients;
  private String[] toRecipients;

  public VacationDeclinedMail(Vacation vacation, VacationEntitlement entitlement, String urlToVacation, String comment) {
    values.put("employee", vacation.getUser().getFirstname());
    values.put("substitute", vacation.getSubstitute() == null ? "" : vacation.getSubstitute()
        .getFullname());
    values.put("manager", vacation.getManager() == null ? "" : vacation.getManager().getFullname());
    values.put("fromDate", ConversionUtils.convertLocalDateToString(vacation.getFrom()));
    values.put("toDate", ConversionUtils.convertLocalDateToString(vacation.getTo()));
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
    return "VacationDeclinedMail [getTemplate()=" + getTemplate() + ", getHtmlTemplate()="
        + getHtmlTemplate() + ", getValues()=" + getValues() + ", getCCRecipients()="
        + Arrays.toString(getCCRecipients()) + ", getSubject()=" + getSubject()
        + ", getToRecipients()=" + Arrays.toString(getToRecipients()) + "]";
  }
}
