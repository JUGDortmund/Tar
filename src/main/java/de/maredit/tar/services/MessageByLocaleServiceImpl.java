package de.maredit.tar.services;

import com.fasterxml.jackson.annotation.JacksonInject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Locale;

@Service
public class MessageByLocaleServiceImpl implements MessageByLocaleService {

  @Autowired
  private MessageSource messageSource;

  @Override
  public String getMessage(String id) {
    Locale locale = LocaleContextHolder.getLocale();
    return messageSource.getMessage(id, null, locale);
  }
}
