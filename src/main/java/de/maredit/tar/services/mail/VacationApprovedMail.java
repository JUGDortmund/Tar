package de.maredit.tar.services.mail;

import de.maredit.tar.models.Vacation;
import de.maredit.tar.utils.ConversionUtils;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.parameter.Cn;
import net.fortuna.ical4j.model.parameter.Role;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.util.UidGenerator;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.net.SocketException;
import java.net.URI;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class VacationApprovedMail implements MailObject {

  private static final String MAIL_TEMPLATE = "mail/vacationApproved";
  private static final String MAIL_SUBJECT = "Urlaub genehmigt";

  private Map<String, Object> values = new HashMap<>();
  private String[] ccRecipients;
  private String[] toRecipients;
  private Calendar ical;

  public VacationApprovedMail(Vacation vacation) throws SocketException {
    values.put("employee", vacation.getUser().getFirstname());
    values.put("substitute", vacation.getSubstitute() == null ? "" : vacation.getSubstitute()
        .getFullname());
    values.put("fromDate", ConversionUtils.convertLocalDateToString(vacation.getFrom()));
    values.put("toDate",
               ConversionUtils.convertLocalDateToString(vacation.getTo()));
    values.put("totalDays", vacation.getDays());
    values.put("leftDays", vacation.getDaysLeft());
    toRecipients = ArrayUtils.add(toRecipients, retrieveMail(vacation.getUser()));
    if (vacation.getSubstitute() != null) {
      ccRecipients = ArrayUtils.add(ccRecipients, retrieveMail(vacation.getSubstitute()));
    }
    ccRecipients = ArrayUtils.add(ccRecipients, retrieveMail(vacation.getManager()));

    buildICalAttachment(vacation);
  }

  private void buildICalAttachment(Vacation vacation) throws SocketException {
    ical = new Calendar();
    ical.getProperties().add(CalScale.GREGORIAN);
    ical.getProperties().add(Version.VERSION_2_0);
    ical.getProperties().add(new ProdId("-//maredit//TaR 1.0//DE"));
    DateTime start = new DateTime(Date.from(vacation.getFrom().atStartOfDay(ZoneId.systemDefault()).toInstant()));
    DateTime end = new DateTime(Date.from(vacation.getTo().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
    VEvent meeting = new VEvent( start, end, "Urlaub " + vacation.getUser().getFullname());

    // generate unique identifier..
    UidGenerator ug = new UidGenerator("uidGen");
    Uid uid = ug.generateUid();
    meeting.getProperties().add(uid);

    // add attendees..
    Attendee user = new Attendee(URI.create("mailto:" + vacation.getUser().getMail()));
    user.getParameters().add(Role.REQ_PARTICIPANT);
    user.getParameters().add(new Cn(vacation.getUser().getFullname()));
    meeting.getProperties().add(user);
    ical.getComponents().add(meeting);
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
  public byte[] getICalAttachment() throws IOException, ValidationException{
    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    CalendarOutputter outputter = new CalendarOutputter();
    outputter.output(ical, outStream);
    outStream.close();
    return outStream.toByteArray();
  }

  @Override
  public String toString() {
    return "VacationApprovedMail [getTemplate()=" + getTemplate() + ", getHtmlTemplate()="
           + getHtmlTemplate() + ", getValues()=" + getValues() + ", getCCRecipients()="
           + Arrays.toString(getCCRecipients()) + ", getSubject()=" + getSubject()
           + ", getToRecipients()=" + Arrays.toString(getToRecipients()) + "]";
  }
}
