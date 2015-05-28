package de.maredit.tar.controllers;

import de.maredit.tar.beans.NavigationBean;

import de.maredit.tar.services.calendar.CalendarItem;

import de.maredit.tar.services.CalendarService;
import de.maredit.tar.models.User;
import de.maredit.tar.models.Vacation;
import de.maredit.tar.models.enums.FormMode;
import de.maredit.tar.models.enums.State;
import de.maredit.tar.models.validators.VacationValidator;
import de.maredit.tar.properties.CustomMailProperties;
import de.maredit.tar.properties.VersionProperties;
import de.maredit.tar.providers.VersionProvider;
import de.maredit.tar.repositories.UserRepository;
import de.maredit.tar.repositories.VacationRepository;
import de.maredit.tar.services.LdapService;
import de.maredit.tar.services.MailService;
import de.maredit.tar.services.UserService;
import de.maredit.tar.services.mail.MailObject;
import de.maredit.tar.services.mail.SubstitutionApprovedMail;
import de.maredit.tar.services.mail.SubstitutionRejectedMail;
import de.maredit.tar.services.mail.VacationApprovedMail;
import de.maredit.tar.services.mail.VacationCanceledMail;
import de.maredit.tar.services.mail.VacationCreateMail;
import de.maredit.tar.services.mail.VacationDeclinedMail;
import de.maredit.tar.services.mail.VacationModifiedMail;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.access.prepost.PreAuthorize;
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

import java.time.LocalDate;
import java.net.SocketException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

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
  
  @Autowired
  private CalendarService calendarService;

  @Autowired
  private UserService userService;

  @Autowired
  private VersionProperties versionProperties;
  
  @Autowired
  private NavigationBean navigationBean;

  @Autowired
  private CustomMailProperties customMailProperties;

  @Autowired
  private ApplicationController applicationController;

  @Autowired
  private VersionProvider versionProvider;

  @ModelAttribute("vacation")
  public Vacation getVacation(@RequestParam(value = "id", required = false) String id) {
    if (StringUtils.isBlank(id)) {
      return new Vacation();
    }
    return vacationRepository.findOne(id);
  }

  @InitBinder("vacation")
  protected void initBinder(WebDataBinder binder) {
    binder.addValidators(new VacationValidator());
    binder.registerCustomEditor(String.class, "id", new StringTrimmerEditor(true));
  }

  @RequestMapping("/")
  public String index(HttpServletRequest request, Model model,
                      @ModelAttribute("vacation") Vacation vacation) {

    navigationBean.setActiveComponent(NavigationBean.VACATION_PAGE);
    vacation.setUser(applicationController.getConnectedUser());
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
    MailObject mail =
        (approve ? new SubstitutionApprovedMail(vacation, customMailProperties.getUrlToVacation()) : new SubstitutionRejectedMail(vacation, customMailProperties.getUrlToVacation()));
    this.mailService.sendMail(mail);

    return "redirect:/";
  }

  @RequestMapping("/approval")
  @PreAuthorize("hasRole('SUPERVISOR')")
  public String approval(@ModelAttribute("vacation") Vacation vacation,
                         @RequestParam(value = "approve") boolean approve) throws SocketException {

    CalendarItem appointment = null;
    if (approve) {
      vacation.setState(State.APPROVED);
      appointment = calendarService.createAppointment(vacation);
      vacation.setAppointmentId(appointment.getAppointmentId());
    } else {
      vacation.setState(State.REJECTED);
    }
    this.vacationRepository.save(vacation);
    MailObject mail =
        (approve ? new VacationApprovedMail(vacation, customMailProperties.getUrlToVacation()) : new VacationDeclinedMail(vacation, customMailProperties.getUrlToVacation()));
    this.mailService.sendMail(mail, appointment.getMailAttachment());

    return "redirect:/";
  }

  @RequestMapping(value = "/vacation", method = {RequestMethod.GET}, params = "id")
  public String vacation(@ModelAttribute("vacation") Vacation vacation,
                         @RequestParam(value = "action", required = false) String action,
                         Model model) {
    switch (action) {
      case "edit":
        model.addAttribute("users", userService.getSortedUserList());
        model.addAttribute("managers", userService.getManagerList());
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
  public String newVacation(@ModelAttribute("vacation") Vacation vacation, Model model) {
    vacation.setUser(applicationController.getConnectedUser());
    model.addAttribute("managers", userService.getManagerList());
    model.addAttribute("users", userService.getSortedUserList());
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
      boolean newVacation = StringUtils.isBlank(vacation.getId());
      Vacation vacationBeforeChange = newVacation ? null : vacationRepository.findOne(vacation.getId());

      if (newVacation) {
        vacation.setAuthor(applicationController.getConnectedUser());
      } else {
        vacation.setState(vacation.getSubstitute() == null ? State.WAITING_FOR_APPROVEMENT
                                                           : State.REQUESTED_SUBSTITUTE);
        calendarService.deleteAppointment(vacation);
        vacation.setAppointmentId(null);
      }
      this.vacationRepository.save(vacation);
      this.mailService.sendMail(newVacation ? new VacationCreateMail(vacation, customMailProperties.getUrlToVacation())
                                            : new VacationModifiedMail(vacation, customMailProperties.getUrlToVacation(), vacationBeforeChange,
                                                                       applicationController
                                                                           .getConnectedUser()));
      return "redirect:/";
    }
  }

  @RequestMapping(value = "/cancelVacation", method = RequestMethod.GET)
  @PreAuthorize("hasRole('SUPERVISOR') or #vacation.user.username == authentication.name")
  public String cancelVacation(@ModelAttribute("vacation") Vacation vacation) {
    VacationCanceledMail mail = new
        VacationCanceledMail(vacation);
    vacation.setState(State.CANCELED);
    calendarService.deleteAppointment(vacation);
    vacation.setAppointmentId(null);
    this.vacationRepository.save(vacation);
    this.mailService.sendMail(mail);

    return "redirect:/";
  }

  private void setIndexModelValues(Model model, User selectedUser) {

    List<User> users = userService.getSortedUserList();
    List<User> managerList = userService.getManagerList();

    List<Vacation> vacations = getVacationsForUser(selectedUser);
    List<Vacation> substitutes = getSubstitutesForUser(selectedUser);

    List<Vacation>
        substitutesForApproval =
        getVacationsForSubstituteApprovalForUser(applicationController.getConnectedUser());
    List<Vacation>
        approvals =
        getVacationsForApprovalForUser(applicationController.getConnectedUser());

    model.addAttribute("users", users);
    model.addAttribute("vacations", vacations);
    model.addAttribute("selectedUser", selectedUser);
    model.addAttribute("managers", managerList);
    model.addAttribute("substitutes", substitutes);
    model.addAttribute("substitutesForApproval", substitutesForApproval);
    model.addAttribute("approvals", approvals);

    model.addAttribute("loginUser", applicationController.getConnectedUser());
    model.addAttribute("appVersion", versionProvider.getApplicationVersion());
    model.addAttribute("buildnumber", versionProperties.getBuild());
  }

  private User getUser(HttpServletRequest request) {
    User user = null;
    Object selected = request.getParameter("selected");
    if (selected == null) {
      user = applicationController.getConnectedUser();
    } else {
      user = this.userRepository.findUserByUsername(String.valueOf(selected));
    }

    return user;
  }

  private List<Vacation> getSubstitutesForUser(User user) {
    List<Vacation>
        substitutes =
        this.vacationRepository.findVacationBySubstituteAndStateNotAndToAfterOrderByFromAsc(
            user, State.CANCELED, LocalDate.now().minusDays(1));
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
