package de.maredit.tar.services.mail;

import de.maredit.tar.models.User;
import de.maredit.tar.models.Vacation;

import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class VacationCanceledMail implements MailObject {

  private static final String MAIL_TEMPLATE = "mail/vacationCanceled";
  private static final String MAIL_SUBJECT = "Urlaub storniert";

  private Map<String, Object> values = new HashMap<>();
  private String[] ccRecipients;
  private String toRecipient;

  public VacationCanceledMail(Vacation vacation) {
    values.put("employee", vacation.getUser().getFirstName());
    values.put("fromDate", vacation.getFrom());
    values.put("toDate", vacation.getTo());
    values.put("totalDays", vacation.getDays());
    values.put("leftDays", vacation.getDaysLeft());
    toRecipient = retrieveMail(vacation.getUser());
    if(vacation.getSubstitute() != null) {
      ccRecipients = ArrayUtils.add(ccRecipients, retrieveMail(vacation.getSubstitute()));
    }
    ccRecipients = ArrayUtils.add(ccRecipients, retrieveMail(vacation.getManager()));
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
  public String getToRecipient() {
    return toRecipient;
  }

  @Override
  public String toString() {
    return "VacationCanceledMail [getTemplate()=" + getTemplate() + ", getHtmlTemplate()="
           + getHtmlTemplate() + ", getValues()=" + getValues() + ", getCCRecipients()="
           + Arrays.toString(getCCRecipients()) + ", getSubject()=" + getSubject()
           + ", getToRecipient()=" + getToRecipient() + "]";
  }
}