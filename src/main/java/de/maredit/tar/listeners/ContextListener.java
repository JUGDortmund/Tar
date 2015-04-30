package de.maredit.tar.listeners;

import de.maredit.tar.models.User;
import de.maredit.tar.models.Vacation;
import de.maredit.tar.models.enums.State;
import de.maredit.tar.repositories.UserRepository;
import de.maredit.tar.repositories.VacationRepository;
import de.maredit.tar.tasks.UserSyncTask;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

public class ContextListener implements ApplicationListener<ContextRefreshedEvent> {

  @Override
  public void onApplicationEvent(ContextRefreshedEvent event) {
    UserSyncTask userSyncTask = event.getApplicationContext().getBean(UserSyncTask.class);
    userSyncTask.syncLdapUser();
    
    if (event.getApplicationContext().getEnvironment()
        .getProperty("spring.data.mongodb.preload", Boolean.class)) {
      UserRepository userRepository = event.getApplicationContext().getBean(UserRepository.class);
      VacationRepository
          vacationRepository =
          event.getApplicationContext().getBean(VacationRepository.class);
      
      List<User> users = userRepository.findAll();
      for (User user : users) {

        Vacation v1 =
            new Vacation(user, LocalDate.now().plusMonths(1), LocalDate.now().plusMonths(
            1).plusDays(15), user, user, 15, 5);
        Vacation v2 =
            new Vacation(user, LocalDate.now().plusDays(5), LocalDate.now().plusDays(20),
                                   user, user, 15, 5);
        v2.setState(State.REJECTED);
        Vacation
            v3 =
            new Vacation(user, LocalDate.now().plusWeeks(2), LocalDate.now().plusWeeks(2).plusDays(15),
                                       user, user, 15, 5);
        v3.setState(State.APPROVED);
        vacationRepository.save(v1);
        vacationRepository.save(v2);
        vacationRepository.save(v3);
      }
    }
  }
}
