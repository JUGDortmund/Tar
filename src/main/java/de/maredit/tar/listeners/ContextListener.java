package de.maredit.tar.listeners;

import java.time.LocalDate;
import java.util.List;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import de.maredit.tar.models.User;
import de.maredit.tar.models.Vacation;
import de.maredit.tar.models.enums.State;
import de.maredit.tar.repositories.UserRepository;
import de.maredit.tar.repositories.VacationRepository;
import de.maredit.tar.tasks.UserSyncTask;

public class ContextListener implements ApplicationListener<ContextRefreshedEvent> {

  @Override
  public void onApplicationEvent(ContextRefreshedEvent event) {
    UserSyncTask userSyncTask = event.getApplicationContext().getBean(UserSyncTask.class);
    userSyncTask.syncLdapUser();
    
    if (event.getApplicationContext().getEnvironment().getProperty("spring.data.mongodb.preload", Boolean.class)) {
      UserRepository userRepository = event.getApplicationContext().getBean(UserRepository.class);
      VacationRepository vacationRepository = event.getApplicationContext().getBean(VacationRepository.class);
      
      List<User> users = userRepository.findAll();
      for (User user : users) {
        Vacation v1 = new Vacation(user, LocalDate.of(2015, 1, 1), LocalDate.of(2015, 1, 11), State.APPROVED);
        Vacation v2 = new Vacation(user, LocalDate.of(2015, 1, 1), LocalDate.of(2015, 1, 11), State.REJECTED);
        Vacation v3 = new Vacation(user, LocalDate.of(2015, 1, 1), LocalDate.of(2015, 1, 11), State.WAITING_FOR_APPROVEMENT);
        vacationRepository.save(v1);
        vacationRepository.save(v2);
        vacationRepository.save(v3);
      }
    }
  }
}