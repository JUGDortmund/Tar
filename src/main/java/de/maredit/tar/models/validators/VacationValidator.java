package de.maredit.tar.models.validators;

import de.maredit.tar.models.Vacation;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

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
    if (vacation.getSubstitute() != null
        && vacation.getUser().getUidNumber().equals(vacation.getSubstitute().getUidNumber())) {
      errors.rejectValue("substitute", "substitute.is.user",
          "Vertretung darf nicht Antragssteller sein");
    }
    if (vacation.isHalfDay()) {
      if (vacation.getTimeframe() == null) {
        errors.rejectValue("halfDay", "halfday.without.timeframe", "Halbe Urlaubstage benötigen einen Zeitraum");
      }

      if (!vacation.getTo().isEqual(vacation.getFrom())) {
        errors.rejectValue("to", "halfday.to.long", "Halbe Urlaubstagedürfen nicht über mehrere Tage beantragt werden.");
      }
    }
  }
}
