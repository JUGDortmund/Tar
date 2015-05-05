package de.maredit.tar.services.mail;

import de.maredit.tar.models.User;
import de.maredit.tar.models.Vacation;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubstitutionApprovedMail implements MailObject {

  private static final String MAIL_TEMPLATE = "mail/substitutionApproved";
  private static final String MAIL_SUBJECT = "Vertretung akzeptiert";

  private Map<String, Object> values = new HashMap<>();
  private String[] ccRecipients;
  private String[] toRecipients;

  public SubstitutionApprovedMail(Vacation vacation) {
    values.put("employee", vacation.getUser().getFirstName());
    values.put("fromDate", vacation.getFrom().format(
        DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
    values.put("toDate", vacation.getTo().format(
        DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
    values.put("totalDays", vacation.getDays());
    values.put("leftDays", vacation.getDaysLeft());
    toRecipients = getRecipients(vacation);
  }

  private String[] getRecipients(Vacation vacation) {
    String[] recipients = {retrieveMail(vacation.getUser())};
    return recipients;
  }

  private String retrieveMail(User user) {
    String mail = "";
    if (user != null && user.getMail() != null) {
      mail = user.getMail();
    }
    return mail;
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
    return "SubstitutionApprovedMail [getTemplate()=" + getTemplate() + ", getHtmlTemplate()="
           + getHtmlTemplate() + ", getValues()=" + getValues() + ", getCCRecipients()="
           + Arrays.toString(getCCRecipients()) + ", getSubject()=" + getSubject()
           + ", getToRecipient()=" + getToRecipients() + "]";
  }
}