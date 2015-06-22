package de.maredit.tar.utils;

import de.maredit.tar.models.enums.HalfDayTimeFrame;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

/**
 * Created by czillmann on 05.05.15.
 */
public class ConversionUtils {

  public static String convertLocalDateToString(LocalDate localDate) {
    return localDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM));
  }
}
