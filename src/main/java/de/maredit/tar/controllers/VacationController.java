package de.maredit.tar.controllers;

import de.maredit.tar.beans.NavigationBean;
import de.maredit.tar.data.CommentItem;
import de.maredit.tar.data.User;
import de.maredit.tar.data.UserVacationAccount;
import de.maredit.tar.data.Vacation;
import de.maredit.tar.models.TimelineItem;
import de.maredit.tar.models.VacationEntitlement;
import de.maredit.tar.models.enums.FormMode;
import de.maredit.tar.models.enums.State;
import de.maredit.tar.models.validators.VacationValidator;
import de.maredit.tar.properties.CustomMailProperties;
import de.maredit.tar.repositories.CommentItemRepository;
import de.maredit.tar.repositories.ProtocolItemRepository;
import de.maredit.tar.repositories.StateItemRepository;
import de.maredit.tar.repositories.UserRepository;
import de.maredit.tar.repositories.UserVacationAccountRepository;
import de.maredit.tar.repositories.VacationRepository;
import de.maredit.tar.services.CalendarService;
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
public class VacationController {

  private static final Logger LOG = LogManager.getLogger(VacationController.class);


  @Autowired
  private UserRepository userRepository;

  @Autowired
  private VacationRepository vacationRepository;

  @Autowired
  private ProtocolItemRepository protocolItemRepository;

  @Autowired
  private CommentItemRepository commentItemRepository;

  @Autowired
  private StateItemRepository stateItemRepository;

  @Autowired
  private MailService mailService;

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
    model.addAttribute("remaining", vacationService.getRemainingVacationEntitlement(
        userService.getUserVacationAccountForYear(
            applicationController.getConnectedUser(),
            LocalDateTime.now().getYear())));
    return "application/index";
  }

  @RequestMapping(value = "/substitution", method = RequestMethod.POST)
  public String substitution(@ModelAttribute("vacation") Vacation vacation,
                             @ModelAttribute("comment") String comment,
                             @ModelAttribute("approve") String approval,
                             Model model) {
    boolean approve = Boolean.valueOf(approval);
    vacation.setState((approve) ? State.WAITING_FOR_APPROVEMENT : State.REJECTED);
    vacationService.saveComment(comment, vacation, applicationController.getConnectedUser());
    vacationRepository.save(vacation);
    VacationEntitlement entitlement =
        vacationService.getRemainingVacationEntitlement(userService.getUserVacationAccountForYear(
            vacation.getUser(), vacation.getFrom().getYear()));
    MailObject mail =
        (approve ? new SubstitutionApprovedMail(
            vacation, entitlement, customMailProperties.getUrlToVacation(), comment)
                 : new SubstitutionRejectedMail(
                     vacation, entitlement, customMailProperties.getUrlToVacation(), comment));
    this.mailService.sendMail(mail);
    List<TimelineItem> allTimeline = getTimelineItems(vacation);
    model.addAttribute("timeLineItems", allTimeline);
    return "redirect:/";
  }

  @RequestMapping(value = "/approval", method = RequestMethod.POST)
  @PreAuthorize("hasRole('SUPERVISOR')")
  public String approval(@ModelAttribute("vacation") Vacation vacation,
                         @ModelAttribute("comment") String comment,
                         @ModelAttribute("approve") String approval,
                         Model model) throws SocketException {
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
    vacationService.saveComment(comment, vacation, applicationController.getConnectedUser());
    vacationRepository.save(vacation);
    VacationEntitlement entilement =
        vacationService.getRemainingVacationEntitlement(userService.getUserVacationAccountForYear(
            vacation.getUser(), vacation.getFrom().getYear()));
    MailObject mail =
        (approve ? new VacationApprovedMail(
            vacation, entilement, customMailProperties.getUrlToVacation(), comment)
                 : new VacationDeclinedMail(
                     vacation, entilement, customMailProperties.getUrlToVacation(), comment));
    mailService.sendMail(mail);
    List<TimelineItem> allTimeline = getTimelineItems(vacation);
    model.addAttribute("timeLineItems", allTimeline);
    return "redirect:/";
  }

  @RequestMapping(value = "/addComment", method = RequestMethod.POST)
  public String addComment(@ModelAttribute("id") String id,
                           @ModelAttribute("comment") String comment) {
    if (StringUtils.isNotBlank(id) && StringUtils.isNotBlank(comment)) {
      Vacation vacation = vacationRepository.findOne(id);
      final CommentItem
          commentItem =
          vacationService.saveComment(comment, vacation, applicationController.getConnectedUser());
      mailService.sendMail(new CommentAddedMail(vacation, customMailProperties
          .getUrlToVacation(), commentItem));
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
    model.addAttribute("remaining", vacationService.getRemainingVacationEntitlement(
        userService.getUserVacationAccountForYear(vacation.getUser(),
                                                  vacation.getFrom() == null ? LocalDateTime.now()
                                                      .getYear()
                                                                             : vacation.getFrom()
                                                      .getYear())));
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
    model.addAttribute("remaining", vacationService.getRemainingVacationEntitlement(
        userService.getUserVacationAccountForYear(
            applicationController.getConnectedUser(), LocalDateTime.now().getYear())));
    return "components/vacationForm";
  }

  @RequestMapping(value = "/saveVacation", method = RequestMethod.POST)
  @PreAuthorize("hasRole('SUPERVISOR') or #vacation.user.username == authentication.name")
  public String saveVacation(@ModelAttribute("comment") String comment,
                             @ModelAttribute("vacation") @Valid Vacation vacation,
                             BindingResult bindingResult,
                             Model model, HttpServletRequest request) {

    boolean success = false;
    boolean showRemainigAfter = false;
    boolean newVacation = StringUtils.isBlank(vacation.getId());

    int
        year =
        vacation.getFrom() != null ? vacation.getFrom().getYear() : LocalDateTime.now().getYear();
    UserVacationAccount account =
        userService.getUserVacationAccountForYear(vacation.getUser(), year);
    VacationEntitlement remainingBefore = vacationService.getRemainingVacationEntitlement(account);
    VacationEntitlement remainingAfter = null;

    if (vacation.getFrom() != null) {
      account.addVacation(vacation);
      remainingAfter = vacationService.getRemainingVacationEntitlement(account);

      if (!bindingResult.hasErrors()) {
        vacation.setDays(vacationService.getValueOfVacation(vacation));
        if (remainingAfter.getDays() < 0) {
          bindingResult.reject("error.notEnoughRemaingDays",
                               "You have not enough days left for this vacation");
          showRemainigAfter = true;
          success = false;
        } else {
          success = true;
        }
      } else {
        bindingResult.getFieldErrors().forEach(
            fieldError -> LOG.error(fieldError.getDefaultMessage()));
        if (bindingResult.hasFieldErrors("from") || bindingResult.hasFieldErrors("to")) {
          showRemainigAfter = false;
        } else {
          showRemainigAfter = true;
        }
        success = false;
      }
    } else {
      showRemainigAfter = false;
      success = false;
    }
    // checked validity of vacation to save
    if (success) {
      if (newVacation) {
        vacation.setAuthor(applicationController.getConnectedUser());
        vacationRepository.save(vacation);
        vacationService.saveComment(comment, vacation, applicationController.getConnectedUser());
        userVacationAccountRepository.save(account);
        this.mailService.sendMail(
            new VacationCreateMail(vacation, remainingAfter,
                                   customMailProperties.getUrlToVacation(),
                                   comment));
      } else {
        Vacation vacationBeforeChange = vacationRepository.findOne(vacation.getId());
        vacation.setState(vacation.getSubstitute() == null
                          ? State.WAITING_FOR_APPROVEMENT
                          : State.REQUESTED_SUBSTITUTE);
        calendarService.deleteAppointment(vacation);
        vacation.setAppointmentId(null);

        vacationService.saveComment(comment, vacation, applicationController.getConnectedUser());
        vacationRepository.save(vacation);
        userVacationAccountRepository.save(account);

        this.mailService.sendMail(
            new VacationModifiedMail(vacation, remainingAfter,
                                     customMailProperties.getUrlToVacation(), comment,
                                     vacationBeforeChange,
                                     remainingBefore, applicationController.getConnectedUser()));
      }
      return "redirect:/";

    } else {
      User selectedUser = getUser(request);
      setIndexModelValues(model, selectedUser);
      model.addAttribute("vacation", vacation);
      model.addAttribute("formMode", newVacation ? FormMode.NEW : FormMode.EDIT);
      model.addAttribute("remaining", showRemainigAfter ? remainingAfter : remainingBefore);
      if (showRemainigAfter) {
        model.addAttribute("vacationDays", vacationService.getValueOfVacation(vacation));
      }
      model.addAttribute("comment", comment);
      return "application/index";
    }
  }

  @RequestMapping(value = "/cancelVacation", method = RequestMethod.POST)
  @PreAuthorize("hasRole('SUPERVISOR') or #vacation.user.username == authentication.name")
  public String cancelVacation(@ModelAttribute("vacation") Vacation vacation,
                               @ModelAttribute("comment") String comment) {
    vacation.setState(State.CANCELED);
    calendarService.deleteAppointment(vacation);
    vacation.setAppointmentId(null);
    vacationRepository.save(vacation);
    VacationEntitlement vacationEntitlement = vacationService.getRemainingVacationEntitlement(
        userService
            .getUserVacationAccountForYear(vacation.getUser(), vacation.getFrom().getYear()));
    mailService.sendMail(new VacationCanceledMail(vacation, vacationEntitlement, comment));
    return "redirect:/";
  }

  @RequestMapping(value = "/updateVacationForm", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public Map<String, Object> updateVacationForm(
      @ModelAttribute("vacation") @Valid Vacation vacation, BindingResult bindingResult,
      Model model) {

    if (!bindingResult.hasFieldErrors("from") && !bindingResult.hasFieldErrors("to")) {
      UserVacationAccount account =
          userService.getUserVacationAccountForYear(vacation.getUser(), vacation.getFrom()
              .getYear());
      UserVacationAccount calculatingAccount = new UserVacationAccount();
      calculatingAccount.setUser(vacation.getUser());
      calculatingAccount.setYear(vacation.getFrom().getYear());
      calculatingAccount.setExpiryDate(account.getExpiryDate());
      calculatingAccount.setTotalVacationDays(account.getTotalVacationDays());
      calculatingAccount.setPreviousYearOpenVacationDays(account.getPreviousYearOpenVacationDays());
      vacation.setDays(vacationService.getValueOfVacation(vacation));
      calculatingAccount.setVacations(account.getVacations());
      calculatingAccount.addVacation(vacation);

      return buildRemainingDays(calculatingAccount, vacation);
    }
    return buildRemainingDays(
        userService.getUserVacationAccountForYear(vacation.getUser(), LocalDate.now().getYear()),
        null);
  }

  private Map<String, Object> buildRemainingDays(UserVacationAccount account, Vacation vacation) {
    NumberFormat localFormat = NumberFormat.getNumberInstance(LocaleContextHolder.getLocale());
    localFormat.setMinimumFractionDigits(1);
    Map<String, Object> result = new HashMap<>();
    result.put("vacationDays",
               vacation == null ? ""
                                : localFormat.format(vacationService.getValueOfVacation(vacation)));
    VacationEntitlement remainingDays = vacationService.getRemainingVacationEntitlement(account);
    StringBuilder remainingBuilder = new StringBuilder(localFormat.format(remainingDays.getDays()));
    if (remainingDays.getDaysLastYear() > 0) {
      remainingBuilder.append(" + ").append(localFormat.format(remainingDays.getDaysLastYear()));
    }
    result.put("remainingDays", remainingBuilder.toString());
    return result;
  }

  private List<TimelineItem> getTimelineItems(@ModelAttribute("vacation") Vacation vacation) {
    List<TimelineItem> allTimeline = new ArrayList<TimelineItem>();
    allTimeline.addAll(commentItemRepository.findAllByVacation(vacation));
    allTimeline.addAll(protocolItemRepository.findAllByVacation(vacation));
    allTimeline.addAll(stateItemRepository.findAllByVacation(vacation));

    allTimeline =
        allTimeline.stream().sorted((e1, e2) -> e2.getCreated().compareTo(e1.getCreated()))
            .collect(Collectors.toList());
    return allTimeline;
  }

  private void setIndexModelValues(Model model, User selectedUser) {
    List<Vacation> substitutesForApproval =
        vacationService.getVacationsForSubstituteApprovalForUser(
            applicationController.getConnectedUser());
    List<Vacation> approvals =
        vacationService.getVacationsForApprovalForUser(applicationController.getConnectedUser());

    model.addAttribute("users", userService.getSortedUserList());
    model.addAttribute("vacations", vacationService.getVacationsForUser(selectedUser));
    model.addAttribute("selectedUser", selectedUser);
    model.addAttribute("managers", userService.getManagerList());
    model.addAttribute("substitutes", vacationService.getSubstituteVacationsForUser(selectedUser));
    model.addAttribute("substitutesForApproval", substitutesForApproval);
    model.addAttribute("approvals", approvals);
    model.addAttribute("loginUser", applicationController.getConnectedUser());
  }

  private User getUser(HttpServletRequest request) {
    User user;
    Object selected = request.getParameter("selected");
    if (selected == null) {
      user = applicationController.getConnectedUser();
    } else {
      user = userRepository.findUserByUsername(String.valueOf(selected));
    }
    return user;
  }
}
