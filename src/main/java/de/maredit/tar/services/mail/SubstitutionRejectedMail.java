package de.maredit.tar.services.mail;

import de.maredit.tar.models.Vacation;

import org.apache.commons.lang3.ArrayUtils;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SubstitutionRejectedMail implements MailObject {

  private static final String MAIL_TEMPLATE = "mail/substitutionRejected";
  private static final String MAIL_SUBJECT = "Vertretung abgelehnt";

  private Map<String, Object> values = new HashMap<>();
  private String[] ccRecipients;
  private String[] toRecipients;

  public SubstitutionRejectedMail(Vacation vacation) {
    values.put("employee", vacation.getUser().getFirstname());
    values.put("fromDate", vacation.getFrom().format(
        DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
    values.put("toDate", vacation.getTo().format(
        DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
    values.put("totalDays", vacation.getDays());
    values.put("leftDays", vacation.getDaysLeft());
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
    return "SubstitutionRejectedMail [getTemplate()=" + getTemplate() + ", getHtmlTemplate()="
           + getHtmlTemplate() + ", getValues()=" + getValues() + ", getCCRecipients()="
           + Arrays.toString(getCCRecipients()) + ", getSubject()=" + getSubject()
           + ", getToRecipients()=" + Arrays.toString(getToRecipients()) + "]";
  }
}
