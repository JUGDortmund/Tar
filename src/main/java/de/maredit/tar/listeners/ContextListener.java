package de.maredit.tar.listeners;

import com.unboundid.ldap.sdk.LDAPException;

import de.maredit.tar.models.CommentItem;
import de.maredit.tar.models.Protocol;
import de.maredit.tar.models.ProtocolItem;
import de.maredit.tar.models.StateItem;
import de.maredit.tar.models.TimelineItem;
import de.maredit.tar.models.User;
import de.maredit.tar.models.Vacation;
import de.maredit.tar.models.enums.State;
import de.maredit.tar.repositories.CommentItemRepository;
import de.maredit.tar.repositories.ProtocolItemRepository;
import de.maredit.tar.repositories.StateItemRepository;
import de.maredit.tar.repositories.TimelineItemRepository;
import de.maredit.tar.repositories.UserRepository;
import de.maredit.tar.repositories.VacationRepository;
import de.maredit.tar.services.LdapService;
import de.maredit.tar.tasks.UserSyncTask;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ContextListener implements ApplicationListener<ContextRefreshedEvent> {

  @Override
  public void onApplicationEvent(ContextRefreshedEvent event) {
    UserSyncTask userSyncTask = event.getApplicationContext().getBean(UserSyncTask.class);
    userSyncTask.syncLdapUser();
    
    if (event.getApplicationContext().getEnvironment()
        .getProperty("spring.data.mongodb.preload", Boolean.class)) {
      UserRepository userRepository = event.getApplicationContext().getBean(UserRepository.class);
      ProtocolItemRepository protocolItemRepository = event.getApplicationContext().getBean(ProtocolItemRepository.class);
      CommentItemRepository commentItemRepository = event.getApplicationContext().getBean(CommentItemRepository.class);
      StateItemRepository stateItemRepository = event.getApplicationContext().getBean(StateItemRepository.class);
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
                1).plusDays(15), manager, manager, 15, 5);
        v1.setState(State.WAITING_FOR_APPROVEMENT);
        Vacation v2 =
            new Vacation(user, LocalDate.now().plusDays(5), LocalDate.now().plusDays(20),
                         manager, manager, 15, 5);
        v2.setState(State.REQUESTED_SUBSTITUTE);
        Vacation
            v3 =
            new Vacation(user, LocalDate.now().plusWeeks(2), LocalDate.now().plusWeeks(2).plusDays(
                15), manager, manager, 15, 5);
        v3.setState(State.APPROVED);
        vacationRepository.save(v1);
        vacationRepository.save(v2);
        vacationRepository.save(v3);

        ProtocolItem protocolItem = new ProtocolItem();
        protocolItem.setVacation(v1);
        protocolItem.setAuthor(user);
        protocolItem.setCreated(LocalDateTime.now());
        protocolItem.setFieldName("from");
        protocolItem.setOldValue(v1.getFrom());
        protocolItem.setNewValue(v1.getFrom().plusDays(1));
        protocolItemRepository.save(protocolItem);

        CommentItem commentItem = new CommentItem();
        commentItem.setVacation(v1);
        commentItem.setAuthor(user);
        commentItem.setCreated(LocalDateTime.now());
        commentItem.setText("TEST TEXT FUER KOMMENTAR!!!!111eins");
        commentItem.setModifed(LocalDateTime.now());
        commentItemRepository.save(commentItem);

        CommentItem commentItem2 = new CommentItem();
        commentItem2.setVacation(v1);
        commentItem2.setAuthor(user);
        commentItem2.setCreated(LocalDateTime.now());
        commentItem2.setText("zweiter Text");
        commentItem2.setModifed(LocalDateTime.now());
        commentItemRepository.save(commentItem2);

        StateItem stateItem = new StateItem();
        stateItem.setVacation(v1);
        stateItem.setAuthor(user);
        stateItem.setCreated(LocalDateTime.now());
        stateItem.setOldState(null);
        stateItem.setNewState(State.WAITING_FOR_APPROVEMENT);
        stateItemRepository.save(stateItem);
      }
    }
  }
}
