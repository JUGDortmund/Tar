package de.maredit.tar.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import de.maredit.tar.Main;
import de.maredit.tar.models.enums.State;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDate;
import java.time.Month;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Main.class)
@ActiveProfiles("test")
public class VacationTest {

  private Vacation vacation1 = null;
  private Vacation vacation2 = null;

  @Before
  public void setup() {
    User user = new User();
    user.setFirstname("John");
    user.setLastname("Deer");

    vacation1 =
        new Vacation(user, LocalDate.of(2099, Month.JULY, 18), LocalDate.of(2099, Month.AUGUST, 03),
                     user, user, 15, 5);
    vacation2 =
        new Vacation(user, LocalDate.of(2099, Month.JULY, 18), LocalDate.of(2099, Month.AUGUST, 03),
                     null, user, 15, 5);
  }

  @Test
  public void vacationModel() {
    assertEquals("John", vacation1.getUser().getFirstname());
    assertEquals("John", vacation1.getSubstitute().getFirstname());
    assertEquals(LocalDate.of(2099, Month.JULY, 18), vacation1.getFrom());
    assertEquals(State.REQUESTED_SUBSTITUTE, vacation1.getState());

    assertEquals("John", vacation2.getUser().getFirstname());
    assertNull(vacation2.getSubstitute());
    assertEquals(LocalDate.of(2099, Month.JULY, 18), vacation2.getFrom());
    assertEquals(State.WAITING_FOR_APPROVEMENT, vacation2.getState());
  }
}

