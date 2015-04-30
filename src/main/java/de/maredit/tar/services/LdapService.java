package de.maredit.tar.services;

import java.util.List;
import java.util.Set;

import com.unboundid.ldap.sdk.LDAPException;

import de.maredit.tar.models.User;

/**
 * Created by pplewa on 23.04.15.
 */
public interface LdapService {

  String FIELD_CN = "cn";
  String FIELD_SN = "sn";
  String FIELD_MEMBER = "member";
  String FIELD_UID = "uid";
  String FIELD_UIDNUMBER = "uidnumber";
  String FIELD_MAIL = "mail";


  /**
   * get user list wth user details from LDAP
   * @return
   * @throws LDAPException
   */
  List<User> getLdapUserList() throws LDAPException;

  /**
   * Get the list uf usernames for supervisor
   * @return
   * @throws LDAPException
   */
  Set<String> getLdapSupervisorList() throws LDAPException;

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
