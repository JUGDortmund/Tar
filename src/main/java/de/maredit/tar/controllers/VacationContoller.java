package de.maredit.tar.controllers;

import com.unboundid.ldap.sdk.LDAPException;
import de.maredit.tar.models.User;
import de.maredit.tar.models.Vacation;
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
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
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
public class VacationContoller extends WebMvcConfigurerAdapter {

  private static final Logger LOG = LogManager.getLogger(VacationContoller.class);

  @Autowired
  private VacationRepository vacationRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private MailService mailService;

  @Autowired
  private LdapService ldapService;

  @InitBinder("vacation")
  protected void initBinder(WebDataBinder binder) {
    binder.addValidators(new VacationValidator());
  }

  @RequestMapping("/")
  public String index(HttpServletRequest request, Model model, Vacation vacation) {
    User user = getUser(request);
    vacation.setUser(user);
    List<User> users = this.userRepository.findAll();
    List<Vacation> vacations = this.vacationRepository.findVacationByUserAndStateNotOrderByFromAsc(
        user, State.CANCELED);
    List<User> managerList = getManagerList();
    List<Vacation> substitutes = this.vacationRepository.findVacationBySubstitute(
        getConnectedUser());
    List<Vacation> substitutesForApproval = this.vacationRepository.findVacationBySubstituteAndState(
        getConnectedUser(), State.REQUESTED_SUBSTITUTE);
    List<Vacation> approvals = this.vacationRepository.findVacationByManagerAndState(
        getConnectedUser(), State.WAITING_FOR_APPROVEMENT);
    
    setVacationFormModelValues(model, user, users, vacations, managerList, substitutes,
                               substitutesForApproval, approvals);
    return "application/index";
  }

  @RequestMapping("/substitution")
  public String substitution(@RequestParam(value="id") String id, @RequestParam(value="approve") boolean approve) {
    Vacation vacation = this.vacationRepository.findOne(id);
    vacation.setState((approve) ? State.WAITING_FOR_APPROVEMENT : State.REJECTED);
    this.vacationRepository.save(vacation);

    MailObject mail = (approve ? new SubstitutionApprovedMail(vacation) : new SubstitutionRejectedMail(vacation));
    this.mailService.sendMail(mail);

    return "redirect:/";
  }
  
  @RequestMapping("/approval")
  public String approval(@RequestParam(value="id") String id, @RequestParam(value="approve") boolean approve) {
    Vacation vacation = this.vacationRepository.findOne(id);
    vacation.setState((approve) ? State.APPROVED : State.REJECTED);
    this.vacationRepository.save(vacation);

    MailObject mail = (approve ? new VacationApprovedMail(vacation) : new VacationDeclinedMail(vacation));
    this.mailService.sendMail(mail);

    return "redirect:/";
  }

  @RequestMapping("/vacation")
  public String vacation(@RequestParam(value="id") String id,@RequestParam(value="action", required=false) String action, Model model) {
    Vacation vacation = this.vacationRepository.findOne(id);
    model.addAttribute("vacation", vacation);
    
    switch(action) {
      case "edit":
        model.addAttribute("vacation", vacation);
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("managers", getManagerList());
        model.addAttribute("selectedUser",
            this.userRepository.findByUidNumber(vacation.getUser().getUidNumber()));
        model.addAttribute("disableInput", !getConnectedUser().equals(vacation.getUser()));
        return "application/vacationEdit";
      case "approve":
        return "application/vacationApprove";
      case "substitute":
        return "application/vacationSubstitute";
      case "view":
        return "application/vacationView";
      default:
        return "application/vacation";
    }
  }

  @RequestMapping(value = "/saveVacation", method = RequestMethod.POST)
  public String saveVacation(@Valid Vacation vacation, BindingResult bindingResult, Model model) {
    if (bindingResult.hasErrors()) {
      bindingResult.getFieldErrors().forEach(
          fieldError -> LOG.error(fieldError.getField() + " " + fieldError.getDefaultMessage()));
      User selectedUser = this.userRepository.findByUidNumber(vacation.getUser().getUidNumber());
      List<User> users = this.userRepository.findAll();
      List<Vacation> vacations = this.vacationRepository.findVacationByUserAndStateNotOrderByFromAsc(
          selectedUser, State.CANCELED);
      List<User> managerList = getManagerList();
      List<Vacation> substitutes = this.vacationRepository.findVacationBySubstitute(
          getConnectedUser());
      List<Vacation> substitutesForApproval = this.vacationRepository.findVacationBySubstituteAndState(
          getConnectedUser(), State.REQUESTED_SUBSTITUTE);
      List<Vacation> approvals = this.vacationRepository.findVacationByManagerAndState(
          getConnectedUser(), State.WAITING_FOR_APPROVEMENT);
      
      setVacationFormModelValues(model, selectedUser, users, vacations, managerList, substitutes, substitutesForApproval,
                                 approvals);
      return "application/index";
    } else {
      boolean newVacation = vacation.getId() == null;
      if (!newVacation) {
        vacation.setState(State.WAITING_FOR_APPROVEMENT);
      }
      this.vacationRepository.save(vacation);
      this.mailService.sendMail(newVacation ? new VacationCreateMail(vacation) : new VacationModifiedMail(vacation));
      return "redirect:/";
    }
  }

  @RequestMapping(value = "/cancelVacation", method = RequestMethod.GET)
  @Secured({"AUTH_OWN_CANCEL_VACATION", "AUTH_CANCEL_VACATION"})
  public String cancelVacation(HttpServletRequest request, @RequestParam(value="id") String id, Model model) {
    Vacation vacation = this.vacationRepository.findOne(id);
    User user = getUser(request);
    vacation.setUser(user);

    VacationCanceledMail mail = new VacationCanceledMail(vacation);
    vacation.setState(State.CANCELED);
    this.vacationRepository.save(vacation);
    this.mailService.sendMail(mail);

    List<User> users = this.userRepository.findAll();
    List<Vacation> vacations =
        this.vacationRepository.findVacationByUserAndStateNotOrderByFromAsc(user, State.CANCELED);
    List<User> managerList = getManagerList();
    List<Vacation> substitutes = this.vacationRepository.findVacationBySubstitute(
        getConnectedUser());
    List<Vacation> substitutesForApproval = this.vacationRepository.findVacationBySubstituteAndState(
        getConnectedUser(), State.REQUESTED_SUBSTITUTE);
    List<Vacation> approvals = this.vacationRepository.findVacationByManagerAndState(
        getConnectedUser(), State.WAITING_FOR_APPROVEMENT);
    setVacationFormModelValues(model, user, users, vacations, managerList, substitutes, substitutesForApproval, approvals);

    return "redirect:/";
  }

  private void setVacationFormModelValues(Model model, User selectedUser, List<User> users,
                                          List<Vacation> vacations, List<User> managerList,
                                          List<Vacation> substitutes, List<Vacation> substitutesForApproval, List<Vacation> approvals) {
    model.addAttribute("users", users);
    model.addAttribute("vacations", vacations);
    model.addAttribute("selectedUser", selectedUser);
    model.addAttribute("managers", managerList);
    model.addAttribute("substitutes", substitutes);
    model.addAttribute("substitutesForApproval", substitutesForApproval);
    model.addAttribute("approvals", approvals);
  }

  private List<User> getManagerList() {
    List<User> managerList = new ArrayList<User>();
    try {
      managerList =
          userRepository.findByUsernames(ldapService.getLdapManagerList());
      managerList =
          managerList.stream().filter(e -> e.isActive()).collect(Collectors.toList());

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
}
