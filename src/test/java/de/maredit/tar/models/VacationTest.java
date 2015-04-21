package de.maredit.tar.models;

import de.maredit.tar.Main;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Main.class)
@ActiveProfiles("test")
public class VacationTest {

  private Vacation vacation1 = null;
  private Vacation vacation2 = null;

  @Before
  public void setup() {
    User user = new User();
    user.setFirstName("John");
    user.setLastName("Deer");

    vacation1 =
        new Vacation(user, LocalDate.of(2015, Month.JULY, 18), LocalDate.of(2015, Month.AUGUST, 03),
                     user, user, 15, 5);
    vacation2 =
        new Vacation(user, LocalDate.of(2015, Month.JULY, 18), LocalDate.of(2015, Month.AUGUST, 03),
                     null, user, 15, 5);
  }

  @Test
  public void vacationModel() {
    assertEquals("John", vacation1.getUser().getFirstName());
    assertEquals("John", vacation1.getSubstitute().getFirstName());
    assertEquals(LocalDate.of(2015, Month.JULY, 18), vacation1.getFrom());
    assertEquals(State.REQUESTED_SUBSTITUTE, vacation1.getState());

    assertEquals("John", vacation2.getUser().getFirstName());
    assertNull(vacation2.getSubstitute());
    assertEquals(LocalDate.of(2015, Month.JULY, 18), vacation2.getFrom());
    assertEquals(State.WAITING_FOR_APPROVEMENT, vacation2.getState());
  }
}

