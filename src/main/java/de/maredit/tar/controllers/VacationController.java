package de.maredit.tar.controllers;

import de.maredit.tar.models.AccountEntry;

import de.maredit.tar.beans.NavigationBean;
import de.maredit.tar.models.CommentItem;
import de.maredit.tar.models.TimelineItem;
import de.maredit.tar.models.User;
import de.maredit.tar.models.UserVacationAccount;
import de.maredit.tar.models.Vacation;
import de.maredit.tar.models.VacationEntitlement;
import de.maredit.tar.models.enums.FormMode;
import de.maredit.tar.models.enums.State;
import de.maredit.tar.models.validators.VacationValidator;
import de.maredit.tar.properties.CustomMailProperties;
import de.maredit.tar.properties.VacationProperties;
import de.maredit.tar.repositories.CommentItemRepository;
import de.maredit.tar.repositories.ProtocolItemRepository;
import de.maredit.tar.repositories.StateItemRepository;
import de.maredit.tar.repositories.UserRepository;
import de.maredit.tar.repositories.UserVacationAccountRepository;
import de.maredit.tar.repositories.VacationRepository;
import de.maredit.tar.services.CalendarService;
import de.maredit.tar.services.LdapService;
import de.maredit.tar.services.MailService;
import de.maredit.tar.services.UserService;
import de.maredit.tar.services.VacationService;
import de.maredit.tar.services.calendar.CalendarItem;
import de.maredit.tar.services.mail.CommentAddedMail;
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
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.MediaType;
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
import org.springframework.web.bind.annotation.ResponseBody;

import java.beans.PropertyEditorSupport;
import java.net.SocketException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
public class VacationController extends AbstractBaseController {

  private static final Logger LOG = LogManager.getLogger(VacationController.class);

  @Autowired
  private VacationRepository vacationRepository;

  @Autowired
  private UserRepository userRepository;
  
  @Autowired
  private VacationProperties vacationProperties; 

  @Autowired
  private ProtocolItemRepository protocolItemRepository;

  @Autowired
  private CommentItemRepository commentItemRepository;

  @Autowired
  private StateItemRepository stateItemRepository;

  @Autowired
  private MailService mailService;

  @Autowired
  private LdapService ldapService;

  @Autowired
  private CalendarService calendarService;

  @Autowired
  private UserService userService;

  @Autowired
  private UserVacationAccountRepository userVacationAccountRepository;

  @Autowired
  private NavigationBean navigationBean;

  @Autowired
  private CustomMailProperties customMailProperties;

  @Autowired
  private ApplicationController applicationController;
  
  @Autowired
  private VacationService vacationService;

  @ModelAttribute("vacation")
  public AccountEntry getVacation(@RequestParam(value = "id", required = false) String id) {
    if (StringUtils.isBlank(id)) {
      return new Vacation();
    }
    return vacationRepository.findOne(id);
  }

  @InitBinder("vacation")
  protected void initBinder(WebDataBinder binder) {
    binder.addValidators(new VacationValidator());
    binder.registerCustomEditor(String.class, "id", new StringTrimmerEditor(true));
    binder.registerCustomEditor(User.class, new PropertyEditorSupport() {

      @Override
      public String getAsText() {
        if (getValue() == null) {
          return "";
        }
        return ((User) getValue()).getId();
      }

      @Override
      public void setAsText(String text) throws IllegalArgumentException {
        if (StringUtils.isNotBlank(text)) {
          setValue(userRepository.findOne(text));
        }
      }
    });
  }

  @RequestMapping("/")
  public String index(HttpServletRequest request, Model model,
                      @ModelAttribute("vacation") Vacation vacation) {
    navigationBean.setActiveComponent(NavigationBean.VACATION_PAGE);
    vacation.setUser(applicationController.getConnectedUser());
    User selectedUser = getUser(request);

    setIndexModelValues(model, selectedUser);

    model.addAttribute("formMode", FormMode.NEW);
    model.addAttribute("timeLineItems", new ArrayList<TimelineItem>());
    model.addAttribute("remaining", vacationService.getRemainingVacationDays(userService.getUserVacationAccountForYear(applicationController.getConnectedUser(), LocalDateTime.now().getYear())));
    return "application/index";
  }

  @RequestMapping(value = "/substitution", method = RequestMethod.POST)
  public String substitution(@ModelAttribute("vacation") Vacation vacation,
                             @ModelAttribute("comment") String comment,
                             @ModelAttribute("approve") String approval, Model model) {
    boolean approve = Boolean.valueOf(approval);
    vacation.setState((approve) ? State.WAITING_FOR_APPROVEMENT : State.REJECTED);
    this.vacationRepository.save(vacation);
    saveComment(comment, vacation);
    VacationEntitlement entilement = vacationService.getRemainingVacationDays(userService.getUserVacationAccountForYear(vacation.getUser(), vacation.getFrom().getYear()));
    MailObject mail =
        (approve ? new SubstitutionApprovedMail(vacation, entilement, customMailProperties.getUrlToVacation(), comment) : new SubstitutionRejectedMail(vacation, entilement, customMailProperties.getUrlToVacation(), comment));
    this.mailService.sendMail(mail);
    List<TimelineItem> allTimeline = getTimelineItems(vacation);
    model.addAttribute("timeLineItems", allTimeline);
    return "redirect:/";
  }

  @RequestMapping(value = "/approval", method = RequestMethod.POST)
  @PreAuthorize("hasRole('SUPERVISOR')")
  public String approval(@ModelAttribute("vacation") Vacation vacation,
                         @ModelAttribute("comment") String comment,
                         @ModelAttribute("approve") String approval, Model model)
      throws SocketException {
    boolean approve = Boolean.valueOf(approval);
    vacation.setState((approve) ? State.APPROVED : State.REJECTED);

    CalendarItem appointment = null;
    if (approve) {
      vacation.setState(State.APPROVED);
      appointment = calendarService.createAppointment(vacation);
      vacation.setAppointmentId(appointment.getAppointmentId());
    } else {
      vacation.setState(State.REJECTED);
    }

    this.vacationRepository.save(vacation);
    saveComment(comment, vacation);
    VacationEntitlement entilement = vacationService.getRemainingVacationDays(userService.getUserVacationAccountForYear(vacation.getUser(), vacation.getFrom().getYear()));
    MailObject mail =
        (approve ? new VacationApprovedMail(vacation, entilement, customMailProperties.getUrlToVacation(), comment) : new VacationDeclinedMail(vacation, entilement, customMailProperties.getUrlToVacation(), comment));
    this.mailService.sendMail(mail);
    List<TimelineItem> allTimeline = getTimelineItems(vacation);
    model.addAttribute("timeLineItems", allTimeline);
    return "redirect:/";
  }

  @RequestMapping(value = "/addComment", method = RequestMethod.POST)
  public String addComment(@ModelAttribute("id") String id,
                           @ModelAttribute("comment") String comment) {
    if (StringUtils.isNotBlank(id) && StringUtils.isNotBlank(comment)) {
      Vacation vacation = vacationRepository.findOne(id);
      final CommentItem commentItem = saveComment(comment, vacation);
      this.mailService.sendMail(
          new CommentAddedMail(vacation, customMailProperties.getUrlToVacation(), commentItem));
    }
    return "redirect:/";
  }

  @RequestMapping(value = "/vacation", method = {RequestMethod.GET}, params = "id")
  public String vacation(@ModelAttribute("vacation") Vacation vacation,
                         @RequestParam(value = "action", required = false) String action,
                         Model model) {
    List<TimelineItem> allTimeline = getTimelineItems(vacation);
    model.addAttribute("timeLineItems", allTimeline);
    model.addAttribute("vacationDays", vacation.getDays());
    model.addAttribute("remaining", vacationService.getRemainingVacationDays(userService.getUserVacationAccountForYear(vacation.getUser(), vacation.getFrom() == null ? LocalDateTime.now().getYear(): vacation.getFrom().getYear())));
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
    model.addAttribute("remaining", vacationService.getRemainingVacationDays(userService.getUserVacationAccountForYear(applicationController.getConnectedUser(), LocalDateTime.now().getYear())));
    return "components/vacationForm";
  }

  @RequestMapping(value = "/saveVacation", method = RequestMethod.POST)
  @PreAuthorize("hasRole('SUPERVISOR') or #vacation.user.username == authentication.name")
  public String saveVacation(@ModelAttribute("comment") String comment,
                             @ModelAttribute("vacation") @Valid Vacation vacation,
                             BindingResult bindingResult, Model model,
                             HttpServletRequest request) {

    boolean newVacation = StringUtils.isBlank(vacation.getId());
    UserVacationAccount account = userService.getUserVacationAccountForYear(vacation.getUser(), vacation.getFrom().getYear());
    account.addVacation(vacation);
    if (bindingResult.hasErrors()) {
      bindingResult.getFieldErrors().forEach(
          fieldError -> LOG.error(fieldError.getField() + " " + fieldError.getDefaultMessage()));

      User selectedUser = getUser(request);

      setIndexModelValues(model, selectedUser);
      if (newVacation) {
        model.addAttribute("formMode", FormMode.NEW);
      } else {
        model.addAttribute("formMode", FormMode.EDIT);
      }
      if (bindingResult.hasFieldErrors("from") || bindingResult.hasFieldErrors("to")) {
        model.addAttribute("remaining", vacationService.getRemainingVacationDays(userService.getUserVacationAccountForYear(applicationController.getConnectedUser(), LocalDateTime.now().getYear())));
      } else {
        model.addAttribute("vacationDays", vacationService.getCountOfVacation(vacation));
        model.addAttribute("remaining", vacationService.getRemainingVacationDays(account));
      }
      return "application/index";
    } else {

      vacation.setDays(vacationService.getCountOfVacation(vacation));

      if (newVacation) {
        vacation.setAuthor(applicationController.getConnectedUser());
        VacationEntitlement remainingVacationDays = vacationService.getRemainingVacationDays(account);
        if (remainingVacationDays.getDays() < 0) {
          bindingResult.reject("error.notEnoughRemaingDays", "You have not enough days left for this vacation");
          setIndexModelValues(model, vacation.getUser());
          model.addAttribute("formMode", FormMode.NEW);
          model.addAttribute("vacationDays", vacationService.getCountOfVacation(vacation));
          model.addAttribute("remaining", remainingVacationDays);
          return "application/index";
        }
        this.vacationRepository.save(vacation);
        saveComment(comment, vacation);
        userVacationAccountRepository.save(account);

        this.mailService.sendMail(new VacationCreateMail(vacation, remainingVacationDays, customMailProperties.getUrlToVacation(), comment));
      } else {
        VacationEntitlement oldRemaining = vacationService.getRemainingVacationDays(account);
        Vacation vacationBeforeChange = vacationRepository.findOne(vacation.getId());
        vacation.setState(vacation.getSubstitute() == null ? State.WAITING_FOR_APPROVEMENT
            : State.REQUESTED_SUBSTITUTE);
        calendarService.deleteAppointment(vacation);
        vacation.setAppointmentId(null);
        account.addVacation(vacation);

        VacationEntitlement remaining = vacationService.getRemainingVacationDays(account);
        if (remaining.getDays() < 0) {
          bindingResult.reject("error.notEnoughRemaingDays", "You have not enough days left for this vacation");
          setIndexModelValues(model, vacation.getUser());
          model.addAttribute("formMode", FormMode.EDIT);
          model.addAttribute("vacationDays", vacationService.getCountOfVacation(vacation));
          model.addAttribute("remaining", remaining);
          return "application/index";
        }

        this.vacationRepository.save(vacation);
        saveComment(comment, vacation);
        userVacationAccountRepository.save(account);

        this.mailService.sendMail(new VacationModifiedMail(vacation, remaining, customMailProperties.getUrlToVacation(), comment, vacationBeforeChange,
                                   oldRemaining, applicationController
                                       .getConnectedUser()));
      }

    }
    return "redirect:/";
  }

  @RequestMapping(value = "/cancelVacation", method = RequestMethod.POST)
  @PreAuthorize("hasRole('SUPERVISOR') or #vacation.user.username == authentication.name")
  public String cancelVacation(@ModelAttribute("vacation") Vacation vacation,
                               @ModelAttribute("comment") String comment) {
    vacation.setState(State.CANCELED);
    calendarService.deleteAppointment(vacation);
    vacation.setAppointmentId(null);
    this.vacationRepository.save(vacation);
    this.mailService.sendMail(new VacationCanceledMail(vacation, vacationService.getRemainingVacationDays(userService.getUserVacationAccountForYear(vacation.getUser(), vacation.getFrom().getYear())), comment));

    return "redirect:/";
  }
  
  
  @RequestMapping(value="/updateVacationForm", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public Map<String, Object> updateVacationForm(@ModelAttribute("vacation") @Valid Vacation vacation, BindingResult bindingResult, Model model) {
    
    if (!bindingResult.hasFieldErrors("from") && !bindingResult.hasFieldErrors("to")) {
      UserVacationAccount account = userService.getUserVacationAccountForYear(vacation.getUser(), vacation.getFrom().getYear());
      UserVacationAccount calculatingAccount = new UserVacationAccount();
      calculatingAccount.setUser(vacation.getUser());
      calculatingAccount.setYear(vacation.getFrom().getYear());
      calculatingAccount.setExpiryDate(account.getExpiryDate());
      calculatingAccount.setTotalVacationDays(account.getTotalVacationDays());
      calculatingAccount.setPreviousYearOpenVacationDays(account.getPreviousYearOpenVacationDays());
      vacation.setDays(vacationService.getCountOfVacation(vacation));
      calculatingAccount.setVacations(account.getVacations());
      calculatingAccount.addVacation(vacation);
      
      return buildRemainingDays(calculatingAccount, vacation);
    }
    return buildRemainingDays(userService.getUserVacationAccountForYear(vacation.getUser(), LocalDate.now().getYear()), null);
  }

  private Map<String, Object> buildRemainingDays(UserVacationAccount account, Vacation vacation) {
    NumberFormat localFormat = NumberFormat.getNumberInstance(LocaleContextHolder.getLocale());
    localFormat.setMinimumFractionDigits(1);
    Map<String, Object> result = new HashMap<>();
    result.put("vacationDays", vacation == null ? "" : localFormat.format(vacationService.getCountOfVacation(vacation)));
    VacationEntitlement remainingDays = vacationService.getRemainingVacationDays(account);
    StringBuilder remainingBuilder = new StringBuilder(localFormat.format(remainingDays.getDays()));
    if (remainingDays.getDaysLastYear() > 0) {
      remainingBuilder.append(" + ").append(localFormat.format(remainingDays.getDaysLastYear()));
    }
    result.put("remainingDays", remainingBuilder.toString());
    return result;
  }


  private CommentItem saveComment(@ModelAttribute("comment") String comment,
                                  @ModelAttribute("vacation") @Valid AccountEntry vacation) {
    if (StringUtils.isNotBlank(comment)) {
      CommentItem commentItem = new CommentItem();
      commentItem.setModifed(LocalDateTime.now());
      commentItem.setCreated(LocalDateTime.now());
      commentItem.setText(comment);
      commentItem.setAuthor(applicationController.getConnectedUser());
      commentItem.setVacation(vacation);
      commentItemRepository.save(commentItem);
      return commentItem;
    }
    return null;
  }

  private List<TimelineItem> getTimelineItems(@ModelAttribute("vacation") Vacation vacation) {
    List<TimelineItem> allTimeline = new ArrayList<TimelineItem>();
    allTimeline.addAll(commentItemRepository.findAllByVacation(vacation));
    allTimeline.addAll(protocolItemRepository.findAllByVacation(vacation));
    allTimeline.addAll(stateItemRepository.findAllByVacation(vacation));

    allTimeline =
        allTimeline
            .stream()
            .sorted(
                (e1, e2) -> e2.getCreated()
                    .compareTo(e1.getCreated())).collect(Collectors.toList());
    return allTimeline;
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
