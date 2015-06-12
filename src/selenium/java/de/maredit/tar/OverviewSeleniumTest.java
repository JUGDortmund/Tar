package de.maredit.tar;
import org.junit.Ignore;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.openqa.selenium.By;

// TODO Profil in Maven erstellen, um Selenium-Tests separat ausführen zu können
public class OverviewSeleniumTest extends BaseSeleniumTest {
    @Test
    public void testUser() throws Exception {
      driver.get(baseUrl + "/login");
      driver.findElement(By.id("username")).clear();
      driver.findElement(By.id("username")).sendKeys("user1");
      driver.findElement(By.id("password")).clear();
      driver.findElement(By.id("password")).sendKeys("login");
      driver.findElement(By.xpath("//button[@type='submit']")).click();
      driver.findElement(By.xpath("//li[3]/a/span")).click();
      assertEquals("Bname, Benutzer 2", driver.findElement(By.cssSelector("span.progress-text")).getText());
      assertEquals("Gesamt", driver.findElement(By.cssSelector("span.progress-number > span")).getText());
      assertEquals("30", driver.findElement(By.cssSelector("b > span")).getText());
      driver.findElement(By.xpath("//button[@type='submit']")).click();
      assertEquals("Du wurdest erfolgreich abgemeldet", driver.findElement(By.cssSelector("div.alert.alert-success")).getText());
    }
    
    @Test
    public void testSupervisor() {
      driver.get(baseUrl + "/login");
      driver.findElement(By.id("username")).clear();
      driver.findElement(By.id("username")).sendKeys("supervisor");
      driver.findElement(By.id("password")).clear();
      driver.findElement(By.id("password")).sendKeys("login");
      driver.findElement(By.xpath("//button[@type='submit']")).click();
      driver.findElement(By.xpath("//li[3]/a/span")).click();
      assertEquals("Bname, Benutzer 2",
                   driver.findElement(By.linkText("Bname, Benutzer 2")).getText());
      assertEquals("Gesamt", driver.findElement(By.cssSelector("span.progress-number > span")).getText());
      assertEquals("30", driver.findElement(By.cssSelector("b > span")).getText());
      driver.findElement(By.linkText("Bname, Benutzer 2")).click();
      assertEquals("Benutzereinstellungen", driver.findElement(By.cssSelector("h1")).getText());
      assertEquals("Bname, Benutzer 2", driver.findElement(By.cssSelector("div.panel-heading")).getText());
      try {
        assertEquals("30.0", driver.findElement(By.id("vacationDays")).getAttribute("value"));
      } catch (Error e) {
        verificationErrors.append(e.toString());
      }
      driver.findElement(By.xpath("//button[@type='submit']")).click();
      assertEquals("Du wurdest erfolgreich abgemeldet", driver.findElement(By.cssSelector("div.alert.alert-success")).getText());
    }
}
