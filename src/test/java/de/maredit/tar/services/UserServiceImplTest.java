package de.maredit.tar.services;

import de.maredit.tar.Main;
import de.maredit.tar.models.User;
import de.maredit.tar.models.UserVacationAccount;
import de.maredit.tar.models.Vacation;
import de.maredit.tar.models.enums.State;
import de.maredit.tar.repositories.UserRepository;
import de.maredit.tar.repositories.VacationRepository;
import de.svenkubiak.embeddedmongodb.EmbeddedMongo;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Main.class)
@ActiveProfiles("test")
public class UserServiceImplTest {

  @Autowired
  private UserService userService;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private VacationRepository vacationRepository;

  private User user1;
  private User user2;

  @BeforeClass
  public static void init() {
    EmbeddedMongo.DB.port(28018).start();

  }

  @AfterClass
  public static void destroy() {
    EmbeddedMongo.DB.port(28018).stop();
  }

  @Before
  public void setup() {
    userRepository.save(createDummyUserList());
    vacationRepository.save(createDummyVactions());
  }

  @Test
  public void testGetSortedUserList() {
    List<User> users = userService.getSortedUserList();
    Assert.notNull(users);
    Assert.notEmpty(users);
    Assert.hasLength("4", "Length should be 4! Inactive dummy user should be filtered.");
    assertEquals(users.get(users.size() - 1).getLastname(), "Zuletzt");
    assertEquals(users.get(0).getLastname(), "an den Anonyma");
  }

  @Test
  public void testGetVacationsForUserAndYearWithThisYear() {
    List<Vacation>
        vacations =
        userService.getVacationsForUserAndYear(user1, LocalDate.now().getYear());
    Assert.notNull(vacations);
    Assert.notEmpty(vacations);
    assertEquals(4, vacations.size());
    for (Vacation v : vacations) {
      assertTrue("Vacation should be within given year",
                 v.getFrom().getYear() == LocalDate.now().getYear() ||
                 v.getTo().getYear() == LocalDate.now().getYear()
      );
    }
  }

  @Test
  public void testGetVacationsForUserAndYearWithPreviousYear() {
    List<Vacation>
        lastYearsVacations =
        userService.getVacationsForUserAndYear(user1, LocalDate.now().getYear() - 1);
    Assert.notNull(lastYearsVacations);
    Assert.notEmpty(lastYearsVacations);
    assertEquals(1, lastYearsVacations.size());
    for (Vacation v : lastYearsVacations) {
      assertTrue("Vacation should be within given year",
                 v.getFrom().getYear() == LocalDate.now().getYear() - 1 ||
                 v.getTo().getYear() == LocalDate.now().getYear() - 1
      );
    }
  }

  @Test
  public void testGetUserVacationAccountForYear() {
    UserVacationAccount
        account =
        userService.getUserVacationAccountForYear(user1, LocalDate.now().getYear());
    Assert.notNull(account);
    Assert.notEmpty(account.getVacations());
    assertEquals(4, account.getVacations().size());
    assertEquals(4, account.getApprovedVacationDays(), 0);
    assertEquals(4, account.getPendingVacationDays(), 0);
    assertEquals(22, account.getOpenVacationDays(), 0);
    assertEquals(2, account.getPreviousYearOpenVacationDays(), 0);
  }

  @Test
  public void testGetUserVacationAccountForYearWithNoVacationData() {
    UserVacationAccount
        account =
        userService.getUserVacationAccountForYear(user2, LocalDate.now().getYear());
    Assert.notNull(account);
    assertEquals(0, account.getVacations().size());
    assertEquals(0, account.getApprovedVacationDays(), 0);
    assertEquals(0, account.getPendingVacationDays(), 0);
    assertEquals(0, account.getOpenVacationDays(), 0);
    assertEquals(0, account.getPreviousYearOpenVacationDays(), 0);
  }

  @Test
  public void testGetUserVacationAccountsForYear() {
    List<UserVacationAccount> accounts =
        userService
            .getUserVacationAccountsForYear(createDummyUserList(), LocalDate.now().getYear());

    Assert.notNull(accounts);
    Assert.notEmpty(accounts);
    assertEquals(5, accounts.size());
  }

  private List<User> createDummyUserList() {
    List<User> dummys = new ArrayList<User>();

    user1 = new User();
    user1.setId("01");
    user1.setActive(Boolean.TRUE);
    user1.setFirstname("Xena");
    user1.setLastname("Xantippe");
    user1.setMail("xena.xantippe@maredit.de");
    dummys.add(user1);

    user2 = new User();
    user2.setId("02");
    user2.setActive(Boolean.TRUE);
    user2.setFirstname("Toni");
    user2.setLastname("Test");
    user2.setMail("toni.test@maredit.de");
    dummys.add(user2);

    User user3 = new User();
    user3.setId("03");
    user3.setActive(Boolean.TRUE);
    user3.setFirstname("Anna");
    user3.setLastname("an den Anonyma");
    user3.setMail("anna.anonyma@maredit.de");
    dummys.add(user3);

    User user4 = new User();
    user4.setId("04");
    user4.setActive(Boolean.TRUE);
    user4.setFirstname("Otto");
    user4.setLastname("Zuletzt");
    user4.setMail("otto.zuletzt@maredit.de");
    dummys.add(user4);

    User user5 = new User();
    user5.setId("05");
    user5.setActive(Boolean.FALSE);
    user5.setFirstname("Ina");
    user5.setLastname("Inaktiv");
    user5.setMail("ina.inaktiv@maredit.de");
    dummys.add(user5);

    return dummys;
  }

  private List<Vacation> createDummyVactions() {
    List<Vacation> vacations = new ArrayList<Vacation>();

    Vacation
        vacation1 =
        new Vacation(user1, LocalDate.now().withMonth(4).plusDays(5),
                     LocalDate.now().withMonth(4).plusDays(8),
                     null, user1, 4, 26);
    vacation1.setCreated(LocalDateTime.now().withMonth(3).minusDays(10));
    vacation1.setState(State.APPROVED);
    vacation1.setId("01-01");
    vacations.add(vacation1);
    Vacation
        vacation2 =
        new Vacation(user1, LocalDate.now().withMonth(4).plusDays(15),
                     LocalDate.now().withMonth(4).plusDays(18),
                     null, user1, 4, 22);
    vacation2.setCreated(LocalDateTime.now().withMonth(3).minusDays(8));
    vacation2.setId("01-02");
    vacations.add(vacation2);
    Vacation
        vacation3 =
        new Vacation(user1, LocalDate.now().withMonth(4).minusYears(1).plusDays(15),
                     LocalDate.now().withMonth(
                         4).minusYears(
                         1).plusDays(18),
                     null, user1, 4, 2);
    vacation3.setCreated(LocalDateTime.now().withMonth(3).minusYears(1));
    vacation3.setId("01-03");
    vacations.add(vacation3);

    Vacation
        vacation4 =
        new Vacation(user1, LocalDate.now().withMonth(4).plusMonths(1).plusDays(1),
                     LocalDate.now().withMonth(4).plusMonths(1).plusDays(1),
                     null, user1, 2, 24);
    vacation4.setCreated(LocalDateTime.now().withMonth(3).minusDays(5));
    vacation4.setState(State.REJECTED);
    vacation4.setId("01-04");
    vacations.add(vacation4);

    Vacation
        vacation5 =
        new Vacation(user1, LocalDate.now().withMonth(4), LocalDate.now().withMonth(4),
                     null, user1, 1, 25);
    vacation5.setCreated(LocalDateTime.now().withMonth(3).minusDays(2));
    vacation5.setState(State.CANCELED);
    vacation5.setId("01-05");
    vacations.add(vacation5);
    return vacations;
  }
}
