package de.maredit.tar.services;

import com.unboundid.ldap.sdk.LDAPException;

import de.maredit.tar.models.User;

import java.util.List;
import java.util.Set;

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
  String FIELD_PHOTO = "jpegPhoto";


  /**
   * get user list with user details from LDAP
   */
  List<User> getLdapUserList() throws LDAPException;

  /**
   * Get the list of usernames for supervisor
   */
  Set<String> getLdapSupervisorList() throws LDAPException;

  /**
   * Authenticate user using configured ldap
   */
  boolean authenticateUser(String uid, String password) throws LDAPException;

  List<String> getUserGroups(String uid) throws LDAPException;
}
