package de.maredit.tar.services.mail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.maredit.tar.models.User;
import de.maredit.tar.models.Vacation;

public class VacationCreateMail implements MailObject {

  private static final String MAIL_TEMPLATE = "mail/vacationCreated";
  private static final String MAIL_SUBJECT = "Urlaubsantrag";

  private Map<String, Object> values = new HashMap<>();
  private String[] ccRecipients;
  private String toRecipient;

  public VacationCreateMail(Vacation vacation) {
    values.put("employee", vacation.getUser().getFirstName());
    values.put("manager", vacation.getManager().getFullname());
    values.put("substitute", vacation.getSubstitute() == null ? "" : vacation.getSubstitute()
        .getFullname());
    values.put("fromDate", vacation.getFrom());
    values.put("toDate", vacation.getTo());
    values.put("totalDays", vacation.getDays());
    values.put("leftDays", vacation.getDaysLeft());
    ccRecipients = getRecipients(vacation);
    toRecipient = retrieveMail(vacation.getUser());
  }

  private String[] getRecipients(Vacation vacation) {
    List<String> recipients = new ArrayList<>();
    recipients.add(retrieveMail(vacation.getSubstitute()));
    recipients.add(retrieveMail(vacation.getManager()));

    List<String> filteredList =
        recipients.stream().filter(e -> !e.isEmpty()).collect(Collectors.toList());
    String[] array = new String[filteredList.size()];
    return filteredList.toArray(array);
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
    return "VacationCreateMail [getTemplate()=" + getTemplate() + ", getHtmlTemplate()="
        + getHtmlTemplate() + ", getValues()=" + getValues() + ", getCCRecipients()="
        + Arrays.toString(getCCRecipients()) + ", getSubject()=" + getSubject()
        + ", getToRecipient()=" + getToRecipient() + "]";
  }

}
