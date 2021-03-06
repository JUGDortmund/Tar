package de.maredit.tar.services;

import de.maredit.tar.models.UserHoliday;
import de.maredit.tar.data.UserVacationAccount;
import de.maredit.tar.data.Vacation;
import de.maredit.tar.models.VacationEntitlement;
import de.maredit.tar.models.enums.State;
import de.maredit.tar.properties.VacationProperties;
import de.maredit.tar.repositories.VacationRepository;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.HashSet;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {VacationServiceTest.Config.class})
public class VacationServiceTest {

  static class Config {

    //TODO Hier Feiertagsberechnung noch initialisieren
    @Bean
    public VacationProperties vacattionProperties() {
        VacationProperties vacationProperties = new VacationProperties();
        vacationProperties.setDefaultVacationDays(30);
        vacationProperties.setExpiryDate("04-01");
        
        UserHoliday christmasEve = new UserHoliday();
        christmasEve.setDate("12-24");
        christmasEve.setValence(0.5);
        vacationProperties.setUserHolidays(Arrays.asList(christmasEve));
        return vacationProperties;
    }
    
    @Bean
    public HolidayService holidayService() {
        return new HolidayServiceImpl();
    }

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
    Assert.assertEquals(5, vacationService.getValueOfVacation(vacation), 0);
  }

  @Test
  public void testCountofDaysWithWeekend() {
    Vacation vacation = new Vacation();
    vacation.setFrom(LocalDate.of(2015, Month.JUNE, 8));
    vacation.setTo(LocalDate.of(2015, Month.JUNE, 16));
    Assert.assertEquals(7, vacationService.getValueOfVacation(vacation), 0);
  }

  @Test
  public void testCountofDaysWithChristmas() {
    Vacation vacation = new Vacation();
    vacation.setFrom(LocalDate.of(2015, Month.DECEMBER, 24));
    vacation.setTo(LocalDate.of(2015, Month.DECEMBER, 28));
    Assert.assertEquals(1.5, vacationService.getValueOfVacation(vacation), 0);
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

    
    VacationEntitlement lastingVacationDays = vacationService.getRemainingVacationEntitlement(
        account);
    Assert.assertEquals(24, lastingVacationDays.getDays(), 0);
    Assert.assertEquals(0, lastingVacationDays.getDaysLastYear(), 0);

    account.setPreviousYearOpenVacationDays(5d);
    VacationEntitlement lastingVacationDays2 = vacationService.getRemainingVacationEntitlement(
        account);
    Assert.assertEquals(29, lastingVacationDays2.getDays(), 0);
    Assert.assertEquals(0, lastingVacationDays2.getDaysLastYear(), 0);
  }
}
