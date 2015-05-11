package de.maredit.tar.controllers;

import com.unboundid.ldap.sdk.LDAPException;

import de.maredit.tar.models.User;
import de.maredit.tar.models.Vacation;
import de.maredit.tar.models.enums.FormMode;
import de.maredit.tar.models.enums.State;
import de.maredit.tar.models.validators.VacationValidator;
import de.maredit.tar.repositories.UserRepository;
import de.maredit.tar.repositories.VacationRepository;
import de.maredit.tar.services.LdapService;
import de.maredit.tar.services.MailService;
import de.maredit.tar.services.mail.MailObject;
import de.maredit.tar.services.mail.SubstitutionApprovedMail;
import de.maredit.tar.services.mail.SubstitutionRejectedMail;
import de.maredit.tar.services.mail.VacationApprovedMail;
import de.maredit.tar.services.mail.VacationCanceledMail;
import de.maredit.tar.services.mail.VacationCreateMail;
import de.maredit.tar.services.mail.VacationDeclinedMail;
import de.maredit.tar.services.mail.VacationModifiedMail;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * Created by czillmann on 22.04.15.
 */
@Controller
public class VacationController extends WebMvcConfigurerAdapter {

  private static final Logger LOG = LogManager.getLogger(VacationController.class);

  @Autowired
  private VacationRepository vacationRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private MailService mailService;

  @Autowired
  private LdapService ldapService;

  @ModelAttribute("vacation")
  public Vacation getVacation(@RequestParam(value = "id", required = false) String id) {
    if (id == null) {
      return new Vacation();
    }
    return vacationRepository.findOne(id);
  }

  @InitBinder("vacation")
  protected void initBinder(WebDataBinder binder) {
    binder.addValidators(new VacationValidator());
  }

  @RequestMapping("/")
  public String index(HttpServletRequest request, Model model,
                      @ModelAttribute("vacation") Vacation vacation) {

    vacation.setUser(getConnectedUser());
    User selectedUser = getUser(request);
    setIndexModelValues(model, selectedUser);

    model.addAttribute("formMode", FormMode.NEW);
    return "application/index";
  }

  @RequestMapping("/substitution")
  public String substitution(@ModelAttribute("vacation") Vacation vacation,
                             @RequestParam(value = "approve") boolean approve) {
    vacation.setState((approve) ? State.WAITING_FOR_APPROVEMENT : State.REJECTED);
    this.vacationRepository.save(vacation);

    MailObject
        mail =
        (approve ? new SubstitutionApprovedMail(vacation) : new SubstitutionRejectedMail(vacation));
    this.mailService.sendMail(mail);

    return "redirect:/";
  }

  @RequestMapping("/approval")
  @PreAuthorize("hasRole('SUPERVISOR')")
  public String approval(@ModelAttribute("vacation") Vacation vacation,
                         @RequestParam(value = "approve") boolean approve) {
    vacation.setState((approve) ? State.APPROVED : State.REJECTED);
    this.vacationRepository.save(vacation);

    MailObject
        mail =
        (approve ? new VacationApprovedMail(vacation) : new VacationDeclinedMail(vacation));
    this.mailService.sendMail(mail);

    return "redirect:/";
  }

  @RequestMapping(value = "/vacation", method = {RequestMethod.GET}, params = "id")
  public String vacation(@ModelAttribute("vacation") Vacation vacation,
                         @RequestParam(value = "action", required = false) String action,
                         Model model) {
    switch (action) {
      case "edit":
        model.addAttribute("users", getSortedUserList());
        model.addAttribute("managers", getManagerList());
        model.addAttribute("formMode", FormMode.EDIT);
        break;
      case "approve":
        model.addAttribute("formMode", FormMode.MANAGER_APPROVAL);
        break;
      case "substitute":
        model.addAttribute("formMode", FormMode.SUBSTITUTE_APPROVAL);
        break;
      case "view":
        model.addAttribute("formMode", FormMode.VIEW);
        break;
      default:
        model.addAttribute("formMode", FormMode.VIEW);
        break;
    }
    return "components/vacationForm";
  }

  @RequestMapping("/newVacation")
  public String newVacation(Vacation vacation, Model model) {
    vacation.setUser(getConnectedUser());
    model.addAttribute("managers", getManagerList());
    model.addAttribute("users", getSortedUserList());
    model.addAttribute("vacation", vacation);
    model.addAttribute("formMode", FormMode.NEW);
    return "components/vacationForm";
  }

  @RequestMapping(value = "/saveVacation", method = RequestMethod.POST)
  @PreAuthorize("hasRole('SUPERVISOR') or #vacation.user.username == authentication.name")
  public String saveVacation(@ModelAttribute("vacation") @Valid Vacation vacation,
                             BindingResult bindingResult, Model model,
                             HttpServletRequest request) {
    if (bindingResult.hasErrors()) {
      bindingResult.getFieldErrors().forEach(
          fieldError -> LOG.error(fieldError.getField() + " " + fieldError.getDefaultMessage()));

      User selectedUser = getUser(request);

      setIndexModelValues(model, selectedUser);
      model.addAttribute("formMode", FormMode.EDIT);
      return "application/index";
    } else {
      boolean newVacation = vacation.getId() == null;
      if (!newVacation) {
        vacation.setState(vacation.getSubstitute() == null ? State.WAITING_FOR_APPROVEMENT
                                                           : State.REQUESTED_SUBSTITUTE);
      }
      this.vacationRepository.save(vacation);
      this.mailService.sendMail(newVacation ? new VacationCreateMail(vacation)
                                            : new VacationModifiedMail(vacation,
                                                                       getConnectedUser()));
      return "redirect:/";
    }
  }

  @RequestMapping(value = "/cancelVacation", method = RequestMethod.GET)
  @PreAuthorize("hasRole('SUPERVISOR') or #vacation.user.username == authentication.name")
  public String cancelVacation(@ModelAttribute("vacation") Vacation vacation) {

    VacationCanceledMail mail = new
        VacationCanceledMail(vacation);
    vacation.setState(State.CANCELED);
    this.vacationRepository.save(vacation);
    this.mailService.sendMail(mail);

    return "redirect:/";
  }

  private void setIndexModelValues(Model model, User selectedUser) {

    List<User> users = getSortedUserList();
    List<User> managerList = getManagerList();

    List<Vacation> vacations = getVacationsForUser(selectedUser);
    List<Vacation> substitutes = getSubstitutesForUser(selectedUser);

    List<Vacation>
        substitutesForApproval = getVacationsForSubstituteApprovalForUser(getConnectedUser());
    List<Vacation> approvals = getVacationsForApprovalForUser(getConnectedUser());

    model.addAttribute("users", users);
    model.addAttribute("vacations", vacations);
    model.addAttribute("selectedUser", selectedUser);
    model.addAttribute("managers", managerList);
    model.addAttribute("substitutes", substitutes);
    model.addAttribute("substitutesForApproval", substitutesForApproval);
    model.addAttribute("approvals", approvals);
  }

  private List<User> getSortedUserList() {
    List<User> userList = new ArrayList<User>();
    userList =
        userRepository.findAll();
    userList =
        userList.stream().filter(e -> e.isActive())
            .sorted((e1, e2) -> e1.getLastname().toUpperCase()
                .compareTo(e2.getLastname().toUpperCase()))
            .collect(Collectors.toList());
    return userList;
  }

  private List<User> getManagerList() {
    List<User> managerList = new ArrayList<User>();
    try {
      managerList =
          userRepository.findByUsernames(ldapService.getLdapSupervisorList());
      managerList =
          managerList.stream().filter(e -> e.isActive()).sorted((e1, e2) -> e1.getLastname()
              .compareTo(e2.getLastname()))
              .collect(Collectors.toList());

    } catch (LDAPException e) {
      LOG.error("Error while reading manager list for vacation form", e);
    }
    return managerList;
  }

  private User getConnectedUser() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    User user = this.userRepository.findUserByUsername(auth.getName());

    return user;
  }

  private User getUser(HttpServletRequest request) {
    User user = null;
    Object selected = request.getParameter("selected");
    if (selected == null) {
      user = getConnectedUser();
    } else {
      user = this.userRepository.findUserByUsername(String.valueOf(selected));
    }
    return user;
  }

  private List<Vacation> getSubstitutesForUser(User user) {
    List<Vacation>
        substitutes =
        this.vacationRepository.findVacationBySubstituteAndStateNotOrderByFromAsc(
            user, State.CANCELED);
    return substitutes;
  }

  private List<Vacation> getVacationsForUser(User user) {
    List<Vacation>
        vacations =
        this.vacationRepository.findVacationByUserAndStateNotOrderByFromAsc(user, State.CANCELED);
    return vacations;
  }

  private List<Vacation> getVacationsForApprovalForUser(User user) {
    List<Vacation> approvals = this.vacationRepository.findVacationByManagerAndState(
        user, State.WAITING_FOR_APPROVEMENT);
    return approvals;
  }

  private List<Vacation> getVacationsForSubstituteApprovalForUser(User user) {
    List<Vacation>
        substitutesForApproval =
        this.vacationRepository.findVacationBySubstituteAndState(
            user, State.REQUESTED_SUBSTITUTE);
    return substitutesForApproval;
  }
}
