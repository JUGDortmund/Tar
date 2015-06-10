package de.maredit.tar.services;

import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.maredit.tar.models.UserHoliday;
import de.maredit.tar.properties.VacationProperties;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {HolidayServiceTest.Config.class})
public class HolidayServiceTest {

  @Configuration
  static class Config {
    
    @Bean
    public VacationProperties vacationProperties() {
      return new VacationProperties();
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
    Set<UserHoliday> allHolidays = holidayService.getAllHolidays(2015);
    UserHoliday christmas = new UserHoliday();
    UserHoliday newYearsEve = new UserHoliday();
    UserHoliday easter = new UserHoliday();
    
    christmas.setDate("2015-12-25");
    christmas.setValence(1.0);
    newYearsEve.setDate("2015-12-31");
    newYearsEve.setValence(0.5);
    easter.setDate("2015-04-05");
    easter.setValence(1.0);

    Assert.assertNotNull(allHolidays);
    Assert.assertEquals(16, allHolidays.size());
    Assert.assertTrue(allHolidays.contains(christmas));
    Assert.assertTrue(allHolidays.contains(newYearsEve));
    Assert.assertTrue(allHolidays.contains(easter));
  }
}
