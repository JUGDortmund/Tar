package de.maredit.tar.models.validators;

import de.maredit.tar.Main;
import de.maredit.tar.models.User;
import de.maredit.tar.models.Vacation;

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

  @Before
  public void setup() {
    user = new User();
    user.setFirstName("John");
    user.setLastName("Deer");
  }


  @Test
  public void testValidationWithValidVacation() {

    VacationValidator validatorUnderTest = new VacationValidator();
    Vacation validVacation =
        new Vacation(user, LocalDate.of(2099, Month.JULY, 18), LocalDate.of(2099, Month.AUGUST, 03),
                     user, user, 15, 5);

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
  public void testValidationWithFromDateInPast() {
    VacationValidator validatorUnderTest = new VacationValidator();

    Vacation invalidFromVacation =
        new Vacation(user, LocalDate.of(2010, Month.AUGUST, 18), LocalDate.of(2099, Month.JULY, 03),
                     null, user, 15, 5);

    Errors errors = new BeanPropertyBindingResult(invalidFromVacation, "invalidFromVacation");
    validatorUnderTest.validate(invalidFromVacation, errors);

    assertTrue(errors.hasErrors());
    assertNotNull(errors.getFieldError("from"));
  }

  @Test
  public void testValidationWithToDateInPast() {
    VacationValidator validatorUnderTest = new VacationValidator();

    Vacation invalidToVacation =
        new Vacation(user, LocalDate.of(2099, Month.AUGUST, 18), LocalDate.of(2010, Month.JULY, 03),
                     null, user, 15, 5);
    Errors errors = new BeanPropertyBindingResult(invalidToVacation, "invalidFromVacation");
    validatorUnderTest.validate(invalidToVacation, errors);

    assertTrue(errors.hasErrors());
    assertNotNull(errors.getFieldError("to"));
  }
}
