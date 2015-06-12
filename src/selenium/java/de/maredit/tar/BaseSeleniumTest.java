package de.maredit.tar;
import static org.junit.Assert.fail;

import de.maredit.tar.Main;
import de.maredit.tar.listeners.ContextListener;
import de.maredit.tar.listeners.StartupListener;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

public class BaseSeleniumTest {

  private static ConfigurableApplicationContext context;

  @BeforeClass
  public static void startApplication() {
    SpringApplication springApplication = new SpringApplication(Main.class);
    springApplication.addListeners(new StartupListener(), new ContextListener());
    context = springApplication.run();
  }

  @AfterClass
  public static void stopApplication() {
    context.close();
  }

  protected WebDriver driver;
  protected String baseUrl;
  private boolean acceptNextAlert = true;
  protected StringBuffer verificationErrors = new StringBuffer();

  public BaseSeleniumTest() {
    super();
  }

  @Before
  public void setUp() throws Exception {
    driver = new FirefoxDriver();
    baseUrl = "http://localhost:8080/";
    driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
  }

  @After
  public void tearDown() throws Exception {
    driver.quit();
    String verificationErrorString = verificationErrors.toString();
    if (!"".equals(verificationErrorString)) {
      fail(verificationErrorString);
    }
  }

  protected boolean isElementPresent(By by) {
    try {
      driver.findElement(by);
      return true;
    } catch (NoSuchElementException e) {
      return false;
    }
  }

  protected boolean isAlertPresent() {
    try {
      driver.switchTo().alert();
      return true;
    } catch (NoAlertPresentException e) {
      return false;
    }
  }

  protected String closeAlertAndGetItsText() {
    try {
      Alert alert = driver.switchTo().alert();
      String alertText = alert.getText();
      if (acceptNextAlert) {
        alert.accept();
      } else {
        alert.dismiss();
      }
      return alertText;
    } finally {
      acceptNextAlert = true;
    }
  }

}
