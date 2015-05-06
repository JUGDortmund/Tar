package de.maredit.tar.services;

import com.unboundid.ldap.listener.InMemoryDirectoryServer;
import com.unboundid.ldap.listener.InMemoryDirectoryServerConfig;
import com.unboundid.ldap.listener.InMemoryListenerConfig;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldif.LDIFReader;
import com.unboundid.util.ssl.SSLUtil;
import com.unboundid.util.ssl.TrustAllTrustManager;

import de.maredit.tar.Main;
import de.maredit.tar.models.User;
import de.svenkubiak.embeddedmongodb.EmbeddedMongo;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Main.class)
@ActiveProfiles("serviceTest")
public class LdapServiceImplTest {

	private static InMemoryDirectoryServer ds;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		InMemoryDirectoryServerConfig config = new InMemoryDirectoryServerConfig(
				"dc=de");
		config.setBaseDNs("o=maredit,dc=de");
		config.setListenerConfigs(InMemoryListenerConfig.createLDAPConfig(
				"testListener", InetAddress.getLoopbackAddress(), 1600,
				new SSLUtil(new TrustAllTrustManager())
						.createSSLSocketFactory("TLSv1.1")));

		ds = new InMemoryDirectoryServer(config);
		ds.importFromLDIF(
				true,
				new LDIFReader(LdapServiceImplTest.class
						.getResourceAsStream("/testuser.ldif")));
		ds.startListening();

		EmbeddedMongo.DB.port(28018).start();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		ds.shutDown(true);
	}

	@Autowired
	private LdapService ldapService;

	@Test
	public void testGetLdapUsers() throws LDAPException {
		List<User> users = ldapService.getLdapUserList();
		assertEquals(2, users.size());
	}

	@Test
	public void testAuthenticateUser() throws LDAPException {
		Assert.assertTrue(ldapService.authenticateUser("worker", "TakeARest"));
		Assert.assertFalse(ldapService.authenticateUser("worker",
				"WrongPassword"));
		Assert.assertFalse(ldapService.authenticateUser("unknownUser",
				"WrongPassword"));
	}

	@Test
	public void testGetUserGroups() throws LDAPException {
		List<String> workerGroups = ldapService.getUserGroups("worker");
		Assert.assertEquals(1, workerGroups.size());
		List<String> expectedGroups = new ArrayList<>();
		expectedGroups.add("tar-users");
		Assert.assertEquals(expectedGroups, workerGroups);

		List<String> supervisorGroups = ldapService.getUserGroups("supervisor");
		Assert.assertEquals(2, supervisorGroups.size());
		List<String> expectedGroups2 = new ArrayList<>();
		expectedGroups2.add("tar-supervisors");
		expectedGroups2.add("tar-users");
		Assert.assertEquals(expectedGroups2, supervisorGroups);
	}
}
