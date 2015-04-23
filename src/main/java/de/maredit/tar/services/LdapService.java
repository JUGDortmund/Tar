package de.maredit.tar.services;

import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.SearchResultEntry;

import java.util.List;

/**
 * Created by pplewa on 23.04.15.
 */
public interface LdapService {

  List<SearchResultEntry> getLdapUserList() throws LDAPException;

  boolean authenticateUser(String uid, String password) throws LDAPException;

  List<String> getUserGroups(String uid) throws LDAPException;
}
