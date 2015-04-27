package de.maredit.tar.services;

import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.SearchResultEntry;

import java.util.List;

/**
 * Created by pplewa on 23.04.15.
 */
public interface LdapService {

  /**
   * get user list wth user details from LDAP
   * @return
   * @throws LDAPException
   */
  List<SearchResultEntry> getLdapUserList() throws LDAPException;

  /**
   * Get the list uf usernames for team leader
   * @return
   * @throws LDAPException
   */
  String[] getLdapTeamleaderList() throws LDAPException;

  /**
   * Authenticate user using configured ldap
   * @param uid
   * @param password
   * @return
   * @throws LDAPException
   */
  boolean authenticateUser(String uid, String password) throws LDAPException;

  List<String> getUserGroups(String uid) throws LDAPException;
}
