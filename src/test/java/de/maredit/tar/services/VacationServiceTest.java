package de.maredit.tar.services;

import de.maredit.tar.models.VacationEntitlement;
import de.maredit.tar.models.UserVacationAccount;
import de.maredit.tar.models.Vacation;
import de.maredit.tar.models.enums.State;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashSet;

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
    account.setTotalVacationDays(30);
    account.setExpiryDate(LocalDate.of(2015, 4, 1));
    Vacation vacation = new Vacation();
    vacation.setState(State.APPROVED);
    vacation.setFrom(LocalDate.of(2015, Month.FEBRUARY, 8));
    vacation.setTo(LocalDate.of(2015, Month.FEBRUARY, 16));
    account.setVacations(new HashSet<Vacation>());
    account.getVacations().add(vacation);

    
    VacationEntitlement lastingVacationDays = vacationService.getRemainingVacationDays(account);
    Assert.assertEquals(23, lastingVacationDays.getDays(), 0);
    Assert.assertEquals(0, lastingVacationDays.getDaysLastYear(), 0);

    account.setPreviousYearOpenVacationDays(5d);
    VacationEntitlement lastingVacationDays2 = vacationService.getRemainingVacationDays(account);
    Assert.assertEquals(28, lastingVacationDays2.getDays(), 0);
    Assert.assertEquals(0, lastingVacationDays2.getDaysLastYear(), 0);
  }
}
