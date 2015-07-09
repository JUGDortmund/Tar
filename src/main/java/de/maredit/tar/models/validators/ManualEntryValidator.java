package de.maredit.tar.models.validators;

import de.maredit.tar.data.ManualEntry;
import de.maredit.tar.data.Vacation;
import de.maredit.tar.models.enums.ManualEntryType;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Created by czillmann on 22.04.15.
 */
public class ManualEntryValidator implements Validator {

  @Override
  public boolean supports(Class<?> aClass) {
    return ManualEntry.class.equals(aClass);
  }

  @Override
  public void validate(Object o, Errors errors) {
    ManualEntry manualEntry = (ManualEntry) o;
    if(manualEntry.getVacation() != null){
      // check if is valid vacation for user an year...
      Vacation vacation = manualEntry.getVacation();
      if(!vacation.getUser().getId().equals(manualEntry.getUser().getId())){
        errors.rejectValue("vacation", "manualEntry.vacation.user", "Gewähler Urlaub gehört nicht zu Benutzer");
      }
      if(vacation.getFrom().getYear() != manualEntry.getYear() && vacation.getTo().getYear() != manualEntry.getYear()){
        errors.rejectValue("vacation", "manualEntry.vacation.year", "Gewähler Urlaub gehört nicht zu Buchungsjahr");
      }
      if(vacation.getDays() < manualEntry.getDays()){
        errors.rejectValue("days", "manualEntry.vacation.days", "Anzahl der Tage höher als referenzierter Urlaub");
      }
      if(manualEntry.getType() == ManualEntryType.REDUCE){
        errors.rejectValue("type", "manualEntry.vacation.type", "Bei Bezug auf einen Urlaub kann eine Korrekturbuchung den Anspruch nicht reduzieren");
      }
    }
  }
}
