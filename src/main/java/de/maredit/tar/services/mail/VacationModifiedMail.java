package de.maredit.tar.services.mail;

import de.maredit.tar.models.User;
import de.maredit.tar.models.Vacation;
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

  public VacationModifiedMail(Vacation vacation, User user) {
    values.put("employee", vacation.getUser().getFirstname());
    values.put("manager", vacation.getManager().getFullname());
    values.put("substitute", vacation.getSubstitute() == null ? "" : vacation.getSubstitute()
        .getFullname());
    values.put("fromDate", vacation.getFrom());
    values.put("toDate", vacation.getTo());
    values.put("totalDays", vacation.getDays());
    values.put("leftDays", vacation.getDaysLeft());
    if (!user.equals(vacation.getUser())) {
      values.put("modifiedBy", user.getFullname());
    }
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
