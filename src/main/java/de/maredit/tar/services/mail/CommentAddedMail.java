package de.maredit.tar.services.mail;

import de.maredit.tar.models.CalendarEvent;
import de.maredit.tar.data.CommentItem;
import de.maredit.tar.data.Vacation;
import de.maredit.tar.utils.ConversionUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CommentAddedMail implements MailObject {

  private static final String MAIL_TEMPLATE = "mail/commentAdded";
  private static final String MAIL_SUBJECT = "Ein Kommentar wurde hinzugefügt";

  private Map<String, Object> values = new HashMap<>();
  private String[] ccRecipients;
  private String[] toRecipients;

  public CommentAddedMail(Vacation vacation, String urlToVacation, CommentItem comment) {
    values.put("author", comment.getAuthor().getFullname());
    values.put("created", comment.getCreated());
    values.put("text", comment.getText());
    values.put("employee", vacation.getUser().getFirstname());

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

    values.put("urlToVacation", urlToVacation);

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
    return "SubstitutionApprovedMail [getTemplate()=" + getTemplate() + ", getHtmlTemplate()="
        + getHtmlTemplate() + ", getValues()=" + getValues() + ", getCCRecipients()="
        + Arrays.toString(getCCRecipients()) + ", getSubject()=" + getSubject()
        + ", getToRecipients()=" + Arrays.toString(getToRecipients()) + "]";
  }
}
