package de.maredit.tar.controllers;


import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

@Controller
public abstract class AbstractBaseController {

  @ModelAttribute("dateFormat")
  public String getDateFormatPattern() {

    final Locale locale = LocaleContextHolder.getLocale();
    DateFormat formatter = DateFormat.getDateInstance(DateFormat.MEDIUM, locale);
    String localPattern  = ((SimpleDateFormat)formatter).toPattern();
    return localPattern;
  }

}
