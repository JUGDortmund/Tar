package de.maredit.tar.controllers;

import de.maredit.tar.beans.NavigationBean;
import de.maredit.tar.data.ManualEntry;
import de.maredit.tar.data.User;
import de.maredit.tar.data.UserVacationAccount;
import de.maredit.tar.data.Vacation;
import de.maredit.tar.models.AccountModel;
import de.maredit.tar.models.VacationEntitlement;
import de.maredit.tar.models.enums.ManualEntryType;
import de.maredit.tar.models.validators.ManualEntryValidator;
import de.maredit.tar.properties.CustomMailProperties;
import de.maredit.tar.repositories.UserRepository;
import de.maredit.tar.repositories.UserVacationAccountRepository;
import de.maredit.tar.services.AccountModelService;
import de.maredit.tar.services.MailService;
import de.maredit.tar.services.UserService;
import de.maredit.tar.services.VacationService;
import de.maredit.tar.services.mail.ManualEntryCreateMail;
import de.maredit.tar.services.mail.VacationCreateMail;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * Created by czillmann on 19.05.15.
 */
@Controller
public class OverviewController {

  private static final Logger LOG = LogManager.getLogger(OverviewController.class);

  @Autowired
  private ApplicationController applicationController;

  @Autowired
  private UserService userService;

  @Autowired
  private VacationService vacationService;

  @Autowired
  private AccountModelService accountModelService;

  @Autowired
  private MailService mailService;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private UserVacationAccountRepository userVacationAccountRepository;

  @Autowired
  private CustomMailProperties customMailProperties;

  @Autowired
  private NavigationBean navigationBean;

  @ModelAttribute("user")
  public User getUser(@RequestParam(value = "id", required = false) String id) {
    if (StringUtils.isBlank(id)) {
      return null;
    }
    return userRepository.findOne(id);
  }

  @InitBinder("manualEntry")
  protected void initBinder(WebDataBinder binder) {
    binder.addValidators(new ManualEntryValidator());
  }

  @RequestMapping("/overview")
  public String overview(Model model, @RequestParam(value = "year", required = false) Integer year,
                         @RequestParam(value = "employees", required = false) ArrayList<User> filteredUsers) {
    navigationBean.setActiveComponent(NavigationBean.OVERVIEW_PAGE);

    List<User> allUsers = userService.getSortedUserList();
    List<UserVacationAccount> userVacationAccounts = null;

    int selectedYear = LocalDate.now().getYear();
    if (year != null && year <= selectedYear + 1) {
      selectedYear = year.intValue();
    }
    LOG.trace("Filtered users: {}", filteredUsers);
    LOG.trace("selectedYear : {}", selectedYear);

    if (filteredUsers == null || filteredUsers.isEmpty()) {
      userVacationAccounts = userService.getUserVacationAccountsForYear(allUsers, selectedYear);
      filteredUsers = new ArrayList<User>();
    } else {
      userVacationAccounts =
          userService.getUserVacationAccountsForYear(filteredUsers, selectedYear);
    }

    List<AccountModel> models = new ArrayList<>();
    for (UserVacationAccount userVacationAccount : userVacationAccounts) {
      AccountModel accountModel = accountModelService.createAccountModel(userVacationAccount);
      models.add(accountModel);
    }

    model.addAttribute("loginUser", applicationController.getConnectedUser());
    model.addAttribute("users", allUsers);
    model.addAttribute("filteredUsers", filteredUsers);
    model.addAttribute("accounts", models);
    model.addAttribute("year", selectedYear);

    return "application/overview";
  }


  @RequestMapping(value = "/newManualEntry")
  @PreAuthorize("hasRole('SUPERVISOR')")
  public String newManualEntry(Model model, @ModelAttribute("manualEntry") ManualEntry manualEntry,
                               @RequestParam(value = "user") User user,
                               @RequestParam(value = "year") int year,
                               @RequestParam(value = "index") int index) {

    manualEntry.setAuthor(applicationController.getConnectedUser());
    manualEntry.setUser(user);
    manualEntry.setYear(year);

    List<Vacation> vacations = new ArrayList<>(userService.getVacationsForUserAndYear(user, year));
    model.addAttribute("vacations", vacations);
    model.addAttribute("manualEntry", manualEntry);
    model.addAttribute("index", index);

    return "components/manualEntryForm";
  }

  @RequestMapping(value = "/saveManualEntry", method = RequestMethod.POST)
  @PreAuthorize("hasRole('SUPERVISOR')")
  public String saveManualEntry(Model model,
                                @ModelAttribute("manualEntry") @Valid ManualEntry manualEntry,
                                @RequestParam(value = "index") int index,
                                BindingResult bindingResult,
                                HttpServletResponse response) {
    LOG.debug("manualEntry: {}", manualEntry);
    List<Vacation>
        vacations =
        new ArrayList<>(
            userService.getVacationsForUserAndYear(manualEntry.getUser(), manualEntry.getYear()));
    model.addAttribute("vacations", vacations);
    model.addAttribute("index", index);
    if (bindingResult.hasErrors()) {
      bindingResult.getFieldErrors().forEach(
          fieldError -> LOG.error(fieldError.getDefaultMessage()));
      model.addAttribute("manualEntry", manualEntry);
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      return "components/manualEntryForm";
    } else {

      int year = manualEntry.getYear();
      User user = manualEntry.getUser();
      UserVacationAccount userVacationAccount = userService.getUserVacationAccountForYear(user,
                                                                                          year);
      double openDays =
          vacationService.getRemainingVacationEntitlement(userVacationAccount).getTotalDays();
      if (manualEntry.getType() == ManualEntryType.REDUCE && openDays < manualEntry.getDays()) {
        model.addAttribute("manualEntry", manualEntry);
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        bindingResult.rejectValue("days", "manualEntry.vacation.tooMuchReduce",
                                  "Anzahl der abgezogenen Tage ist höher als planbarer Urlaub");
        return "components/manualEntryForm";
      } else {
        userService.addManualEntryToVacationAccout(manualEntry, userVacationAccount);
        VacationEntitlement remainingEntitlement = vacationService.getRemainingVacationEntitlement(userVacationAccount);
        this.mailService.sendMail(
            new ManualEntryCreateMail(manualEntry, remainingEntitlement, customMailProperties.getUrlToOverview()));

        AccountModel accountModel = accountModelService.createAccountModel(userVacationAccount);
        model.addAttribute("account", accountModel);
        response.setStatus(HttpServletResponse.SC_CREATED);
        return "components/userAccount";
      }
    }
  }
}
