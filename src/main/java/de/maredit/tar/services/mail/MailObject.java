package de.maredit.tar.services.mail;

import java.util.Map;

public interface MailObject {
  
  String getTemplate();
  
  String getHtmlTemplate();
  
  Map<String, Object> getValues();
  
  String[] getCCRecipients();

  String getSubject();

  String[] getToRecipients();
}
