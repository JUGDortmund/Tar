package de.maredit.tar.services;

import de.maredit.tar.models.LastingVacation;

import org.springframework.beans.factory.annotation.Autowired;
import de.maredit.tar.models.UserVacationAccount;
import de.maredit.tar.models.Vacation;
import de.maredit.tar.models.enums.State;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class VacationServiceTest {

  @Configuration
  static class Config {

    //TODO Hier Feiertagsberechnung noch initialisieren
    
      @Bean
      public VacationService vacationService() {
          return new VacationService();
      }
  }

  @Autowired
  private VacationService vacationService;
  
  @Test
  public void testCountofDays() {
    Vacation vacation = new Vacation();
    vacation.setFrom(LocalDate.of(2015, Month.JUNE, 8));
    vacation.setTo(LocalDate.of(2015, Month.JUNE, 12));
    Assert.assertEquals(5, vacationService.getCountOfVacation(vacation), 0);
  }

  @Test
  public void testCountofDaysWithWeekend() {
    Vacation vacation = new Vacation();
    vacation.setFrom(LocalDate.of(2015, Month.JUNE, 8));
    vacation.setTo(LocalDate.of(2015, Month.JUNE, 16));
    Assert.assertEquals(7, vacationService.getCountOfVacation(vacation), 0);
  }

  @Test
  public void testRemainingDays() {
    UserVacationAccount account = new UserVacationAccount();
    Vacation vacation = new Vacation();
    vacation.setState(State.APPROVED);
    vacation.setFrom(LocalDate.of(2015, Month.FEBRUARY, 8));
    vacation.setTo(LocalDate.of(2015, Month.FEBRUARY, 16));
    vacation.setDays(7);
    account.setVacations(new ArrayList<Vacation>());
    account.getVacations().add(vacation);

    LastingVacation lastingVacationDays = vacationService.getLastingVacationDays(account);
    Assert.assertEquals(28, lastingVacationDays.getVacationDays(), 0);
    Assert.assertEquals(0, lastingVacationDays.getVacationDaysLastYear(), 0);
  }
}
