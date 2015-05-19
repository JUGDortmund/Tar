package de.maredit.tar.listeners;

import com.unboundid.ldap.sdk.LDAPException;

import de.maredit.tar.models.User;
import de.maredit.tar.models.Vacation;
import de.maredit.tar.models.enums.State;
import de.maredit.tar.repositories.UserRepository;
import de.maredit.tar.repositories.VacationRepository;
import de.maredit.tar.services.LdapService;
import de.maredit.tar.tasks.UserSyncTask;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.time.LocalDate;
import java.util.List;

public class ContextListener implements ApplicationListener<ContextRefreshedEvent> {

  @Override
  public void onApplicationEvent(ContextRefreshedEvent event) {
    UserSyncTask userSyncTask = event.getApplicationContext().getBean(UserSyncTask.class);
    userSyncTask.syncLdapUser();
    
    if (event.getApplicationContext().getEnvironment()
        .getProperty("spring.data.mongodb.preload", Boolean.class)) {
      UserRepository userRepository = event.getApplicationContext().getBean(UserRepository.class);
      VacationRepository vacationRepository =
          event.getApplicationContext().getBean(VacationRepository.class);
      LdapService ldapService = event.getApplicationContext().getBean(LdapService.class);
      
      User manager = null;

      try {
        List<User>
            managerList =
            userRepository.findByUsernames(ldapService.getLdapSupervisorList());
        manager = managerList.stream().findFirst().get();
      } catch (LDAPException e) {
        e.printStackTrace();
      }

      List<User> users = userRepository.findAll();
      for (User user : users) {

        Vacation v1 =
            new Vacation(user, LocalDate.now().plusMonths(1), LocalDate.now().plusMonths(
                1).plusDays(1), manager, manager, 2, 13);
        v1.setState(State.WAITING_FOR_APPROVEMENT);
        Vacation v2 =
            new Vacation(user, LocalDate.now().plusDays(5), LocalDate.now().plusDays(2),
                         manager, manager, 3, 10);
        v2.setState(State.REQUESTED_SUBSTITUTE);
        Vacation
            v3 =
            new Vacation(user, LocalDate.now().plusWeeks(2), LocalDate.now().plusWeeks(2).plusDays(
                4), manager, manager, 5, 5);
        v3.setState(State.APPROVED);
        vacationRepository.save(v1);
        vacationRepository.save(v2);
        vacationRepository.save(v3);
      }
    }
  }
}
