package de.maredit.tar.services;

import de.maredit.tar.data.ManualEntry;
import de.maredit.tar.data.UserVacationAccount;
import de.maredit.tar.data.Vacation;
import de.maredit.tar.models.AccountEntry;
import de.maredit.tar.models.AccountManualEntry;
import de.maredit.tar.models.AccountModel;
import de.maredit.tar.models.AccountVacationEntry;
import de.maredit.tar.models.enums.State;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountModelService {

  @Autowired
  private MessageByLocaleService messageByLocaleService;

  @Autowired
  private VacationService vacationService;

  public AccountModel createAccountModel(UserVacationAccount userVacationAccount){
    List<Vacation> vacations = new ArrayList<>(userVacationAccount.getVacations());
    List<ManualEntry> manualEntries = new ArrayList<>(userVacationAccount.getManualEntries());

    AccountModel accountModel = new AccountModel();
    accountModel.setId(userVacationAccount.getId());
    accountModel.setUser(userVacationAccount.getUser());
    accountModel.setAccount(userVacationAccount);
    accountModel.setTotalVacationDays(userVacationAccount.getTotalVacationDays());
    accountModel.setPreviousYearOpenVacationDays(userVacationAccount
                                                     .getPreviousYearOpenVacationDays() == null
                                                 ? 0 : userVacationAccount
                                                     .getPreviousYearOpenVacationDays());
    accountModel.setApprovedVacationDays(vacationService.getApprovedVacationDays(userVacationAccount
                                                                                     .getVacations()));
    accountModel
        .setPendingVacationDays(vacationService.getPendingVacationDays(userVacationAccount.getVacations()));
    accountModel.setOpenVacationDays(vacationService
                                         .getRemainingVacationEntitlement(userVacationAccount)
                                         .getTotalDays());

    List<AccountEntry> entryList = createEntryList(userVacationAccount.getYear(), userVacationAccount.getTotalVacationDays(), vacations, manualEntries);
    accountModel.setEntries(entryList);
    return accountModel;
  }

  private List<AccountEntry> createEntryList(int selectedYear, double totalVacationDays,
                            List<Vacation> vacations, List<ManualEntry> manualEntries) {
    List<AccountEntry> entryList = new ArrayList<>();
    for (Vacation vacation : vacations) {
      if (vacation.getFrom().getYear() >= selectedYear
          && vacation.getTo().getYear() <= selectedYear) {
        if (!vacation.getState().equals(State.CANCELED)
            && !vacation.getState().equals(State.REJECTED)) {
          String displayText = messageByLocaleService.getMessage("text.vacation");
          AccountVacationEntry entry = new AccountVacationEntry(vacation, displayText);
          entryList.add(entry);
        }
      }
    }
    for (ManualEntry manualEntry : manualEntries) {
      if(manualEntry.getYear() == selectedYear){
        AccountManualEntry entry = new AccountManualEntry(manualEntry);
        entryList.add(entry);
      }
    }
    if(entryList != null){
      entryList.sort((e1, e2) -> e1.getCreated().compareTo(e2.getCreated()));
      double balance = totalVacationDays;
      for (AccountEntry entry : entryList){
        balance = balance + entry.getDays();
        entry.setBalance(balance);
      }
    }
    return entryList;
  }



}
