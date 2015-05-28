package de.maredit.tar.services.mail;

import net.fortuna.ical4j.model.ValidationException;

import de.maredit.tar.models.User;

import java.io.IOException;
import java.util.Map;

public interface MailObject {

  String getTemplate();

  String getHtmlTemplate();

  Map<String, Object> getValues();

  String[] getCCRecipients();

  String getSubject();

  String[] getToRecipients();

  default void setCcRecipients(String[] ccRecipients) {};

  default boolean sendToAdditionalRecipient() {
    return false;
  }

  default String retrieveMail(User user) {
    String mail = "";
    if (user != null && user.getMail() != null) {
      mail = user.getMail();
    }
    return mail;
  }

  default byte[] getICalAttachment() throws IOException, ValidationException {
    return null;
  }
}
