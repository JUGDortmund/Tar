# Welcome to **T**ake **A** **R**est

## User documentation
For the user documentation see our Wiki: https://github.com/JUGDortmund/Tar/wiki

## Developer documentation
1. Checkout
2. mvn spring-boot:run

Embedded MongoDB runs on port 28018 (when in dev profile)

### Configuration
The application defines three basic configuration environments (called "Profiles" in Spring context): _localDev_, _test_, _seviceTest_. The basic profiles are stored in _application.yaml_ in src/main/resources and are configured to ensure local development and testing. By default, the application runs in _localDev_ profile.

For other profiles a seperate configuration file named with a starting "application-" and the profile name is needed. To disable connections to external services, include "dummyServices" in profile. It can be separated into "dummyLdapService", "dummyCalenderService" and "dummyMailService".
The mail service always has to be specified.

Overview of services:
Mail:

- dummyMailService
- smtpMailService
- exchangeMailService

Calendar:

- dummyCalendarService
- exchangeCalendarService (default)

Ldap (Authorisation):

- dummyLdapService
- ldapService (default)

The default profile can be overwritten by setting the following system property from the command line:

	-Dspring.profiles.active=

To overwrite the active profile is recommended when using other profiles than "localDev" environment.

To use the _test_ profile, all test classes have to use the following annotation:

	@ActiveProfile("test")

To use the _serviceTest_ profile, all test classes which are used to test external services (e.g. mail) have to use the following annotation:

	@ActiveProfile("serviceTest")

### Login

When starting the application in a profile that uses the dummyLdapService (like _localDev_, _dev_ or _stage_), the authentication is using a local user management with defined users in _dummy-user.yaml_.
There are only three users "user1", "user2" and "supervisor" with password "login". When using other profiles, the LDAP-authentication is required and a group-mapping to application authorities in the _group-mapping.yaml_ has to be configured.


### Development-Environment

#### Google-styleguide
To enable a default styleguide for both ide's: Intellij and eclipse,
we defined the google-styleguide as default code style.

##### Installation Guide for **IntelliJ Ultimate Edition**
1. Locate google-styleguide for IntelliJ: [intellij-java-google-style.xml](http://stash.maredit.net/projects/COM/repos/hireme/browse/readme-sources/intellij-java-google-style.xml?raw)
2. Move the intellij-java-google-style.xml to ${User}/Library/Preferences/IntelliJIdea${Version}/
3. Restart IntelliJ
4. Enable the google-styleguide by Preferences >  Editor CodeStyle > Scheme : GoogleStyle
5. Enable auto-code-style by commit by checking Before Commit > Reformat Code in screen 'Commit Changes (cmd+K)'

##### Installation Guide for **IntelliJ Community Edition**
1. Locate google-styleguide for IntelliJ: [intellij-java-google-style.xml](http://stash.maredit.net/projects/COM/repos/hireme/browse/readme-sources/intellij-java-google-style.xml?raw)
2. Move the intellij-java-google-style.xml to ${User}/Library/Preferences/IdeaIC${Version}/codestyles/
3. Restart IntelliJ
4. Enable the google-styleguide by Preferences >  Editor CodeStyle > Scheme : GoogleStyle
5. Enable auto-code-style by commit by checking Before Commit > Reformat Code in screen 'Commit Changes (cmd+K)'

##### Installation Guide for **Eclipse**
1. Locate google-styleguide for Eclipse: [eclipse-java-google-style.xml](http://stash.maredit.net/projects/COM/repos/hireme/browse/readme-sources/eclipse-java-google-style.xml?raw)
2. Import google-styleguide in Eclipse via Preferences > Java > Code Style > Import...
3. Enable google-styleguide in Eclipse via Preferences > Java > Code Style : GoogleStyle
4. Enable auto-code-style on save in Eclipse via Preferences > Java > Editor > Save Actions