package de.maredit.tar.listeners;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.maredit.tar.Main;
import de.maredit.tar.listeners.ContextListener;
import de.maredit.tar.models.User;
import de.maredit.tar.models.Vacation;
import de.maredit.tar.repositories.UserRepository;
import de.maredit.tar.repositories.VacationRepository;
import de.maredit.tar.tasks.UserSyncTask;
import de.svenkubiak.embeddedmongodb.EmbeddedMongo;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Main.class)
@ActiveProfiles("test")
public class ContextListenerTest {

  @Autowired
  private UserRepository userRepository;
  
  @Autowired
  private VacationRepository vacationRepository;
  
  @Autowired
  private UserSyncTask userSyncTask;
  
  private ContextRefreshedEvent event = Mockito.mock(ContextRefreshedEvent.class);
  private UserSyncTask mockedUserSyncTask = Mockito.mock(UserSyncTask.class);
  private ApplicationContext applicationContext = Mockito.mock(ApplicationContext.class);
  private Environment environment = Mockito.mock(Environment.class);
  
  @BeforeClass
  public static void init() {
    EmbeddedMongo.DB.port(28018).start();
  }
  
  @Test
  public void preloadData() {
    when(event.getApplicationContext()).thenReturn(applicationContext);
    when(event.getApplicationContext().getBean(UserSyncTask.class)).thenReturn(mockedUserSyncTask);
    when(event.getApplicationContext().getEnvironment()).thenReturn(environment);
    when(event.getApplicationContext().getEnvironment().getProperty("spring.data.mongodb.preload", Boolean.class)).thenReturn(true);
    when(event.getApplicationContext().getBean(UserRepository.class)).thenReturn(userRepository);
    when(event.getApplicationContext().getBean(VacationRepository.class)).thenReturn(vacationRepository);
    
    this.userSyncTask.syncLdapUser();

    List<User> users = userRepository.findAll();
    assertNotNull(users);
    assertTrue(users.size() > 0);
    
    ContextListener contextListener = new ContextListener();    
    contextListener.onApplicationEvent(event);
    
    List<Vacation> vacations = vacationRepository.findAll();
    assertNotNull(vacations);
    assertTrue(vacations.size() > 0);
  }
  
  @AfterClass
  public static void shutdown() {
    EmbeddedMongo.DB.stop();
  }
}