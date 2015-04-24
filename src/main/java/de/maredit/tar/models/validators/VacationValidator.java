package de.maredit.tar.models.validators;

import de.maredit.tar.models.Vacation;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDate;

/**
 * Created by czillmann on 22.04.15.
 */
public class VacationValidator implements Validator {

  @Override
  public boolean supports(Class<?> aClass) {
    return Vacation.class.equals(aClass);
  }

  @Override
  public void validate(Object o, Errors errors) {
    Vacation vacation = (Vacation) o;
    if (vacation.getTo() != null && vacation.getFrom() != null
        && vacation.getTo().isBefore(vacation.getFrom())) {
      errors.rejectValue("to", "to.before.from", "Ende-Termin liegt vor Anfangs-Termin");
    }
    if (vacation.getFrom() != null && vacation.getFrom().isBefore(LocalDate.now())) {
      errors.rejectValue("from", "date.in.past", "Datum liegt in der Vergangenheit");
    }
    if (vacation.getTo() != null && vacation.getTo().isBefore(LocalDate.now())) {
      errors.rejectValue("to", "date.in.past", "Datum liegt in der Vergangenheit");
    }
  }
}
