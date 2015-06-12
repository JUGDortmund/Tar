package de.maredit.tar.services;

import de.maredit.tar.models.Holiday;
import de.maredit.tar.models.UserHoliday;
import de.maredit.tar.properties.VacationProperties;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Set;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {HolidayServiceTest.Config.class})
public class HolidayServiceTest {

  static class Config {

    @Bean
    public VacationProperties vacationProperties() {
      VacationProperties properties = new VacationProperties();
      UserHoliday christmasEve = new UserHoliday();
      christmasEve.setDate("12-14");
      christmasEve.setValence(0.5);
      UserHoliday newYearEve = new UserHoliday();
      newYearEve.setDate("12-31");
      newYearEve.setValence(0.5);

      properties.setUserHolidays(Arrays.asList(christmasEve, newYearEve));
      return properties;
    }
    
    @Bean
    public HolidayService holidayService() {
      return new HolidayServiceImpl();
    }
  }

  @Autowired
  private HolidayService holidayService;

  @Test
  public void test() {
    Set<Holiday> allHolidays = holidayService.getAllHolidays(2015);
    Holiday christmas = new Holiday(LocalDate.of(2015, 12, 25), "X-Max");
    christmas.setValence(1.0);
    Holiday newYearsEve = new Holiday(LocalDate.of(2015, 12, 31), "Silvester");
    newYearsEve.setValence(0.5);
    Holiday easter = new Holiday(LocalDate.of(2015, 4, 5), "Easter");
    easter.setValence(1.0);

    System.out.println(allHolidays);
    
    Assert.assertNotNull(allHolidays);
    Assert.assertEquals(15, allHolidays.size());
    Assert.assertTrue(allHolidays.contains(christmas));
    Assert.assertTrue(allHolidays.contains(newYearsEve));
    Assert.assertTrue(allHolidays.contains(easter));
  }
}
