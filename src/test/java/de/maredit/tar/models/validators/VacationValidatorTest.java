package de.maredit.tar.models.validators;

import de.maredit.tar.Main;
import de.maredit.tar.models.User;
import de.maredit.tar.models.Vacation;
import de.maredit.tar.models.enums.HalfDayTimeFrame;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by czillmann on 23.04.15.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Main.class)
@ActiveProfiles("test")
public class VacationValidatorTest {

  private User user = null;
  private User user2 = null;

  @Before
  public void setup() {
    user = new User();
    user.setFirstname("John");
    user.setLastname("Deer");
    user.setUidNumber("4711");

    user2 = new User();
    user2.setFirstname("Johanna");
    user2.setLastname("Deerer");
    user2.setUidNumber("4712");
  }

  @Test
  public void testValidationWithValidVacation() {

    VacationValidator validatorUnderTest = new VacationValidator();
    Vacation validVacation =
        new Vacation(user, LocalDate.of(2099, Month.JULY, 18), LocalDate.of(2099, Month.AUGUST, 03),
                     user2, user, 15, 5);

    Errors errors = new BeanPropertyBindingResult(validVacation, "validVacation");
    validatorUnderTest.validate(validVacation, errors);

    assertFalse(errors.hasErrors());
  }

  @Test
  public void testValidationWithToBeforeFromDate() {
    VacationValidator validatorUnderTest = new VacationValidator();

    Vacation invalidVacation =
        new Vacation(user, LocalDate.of(2099, Month.AUGUST, 18), LocalDate.of(2099, Month.JULY, 03),
                     null, user, 15, 5);

    Errors errors = new BeanPropertyBindingResult(invalidVacation, "invalidVacation");
    validatorUnderTest.validate(invalidVacation, errors);

    assertTrue(errors.hasErrors());
    assertNotNull(errors.getFieldError("to"));
  }

  @Test
  public void testValidationHalfDayWithoutTimeFrame() {
    VacationValidator validatorUnderTest = new VacationValidator();

    Vacation invalidVacation =
        new Vacation(user, LocalDate.of(2099, Month.AUGUST, 03), LocalDate.of(2099, Month.AUGUST, 03),
                     null, user, 0.5, 5);
    invalidVacation.setHalfDay(true);
    invalidVacation.setTimeframe(null);

    Errors errors = new BeanPropertyBindingResult(invalidVacation, "invalidVacation");
    validatorUnderTest.validate(invalidVacation, errors);

    assertTrue(errors.hasErrors());
    assertNotNull(errors.getFieldError("halfDay"));
  }

  @Test
  public void testValidationHalfDayToLong() {
    VacationValidator validatorUnderTest = new VacationValidator();

    Vacation invalidVacation =
        new Vacation(user, LocalDate.of(2099, Month.AUGUST, 03), LocalDate.of(2099, Month.AUGUST, 05),
                     null, user, 0.5, 5);
    invalidVacation.setHalfDay(true);
    invalidVacation.setTimeframe(HalfDayTimeFrame.AFTERNOON);

    Errors errors = new BeanPropertyBindingResult(invalidVacation, "invalidVacation");
    validatorUnderTest.validate(invalidVacation, errors);

    assertTrue(errors.hasErrors());
    assertNotNull(errors.getFieldError("to"));
  }
}
