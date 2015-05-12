package de.maredit.tar.providers;

import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public final class VersionProvider {

	private static final Logger LOG = LogManager
			.getLogger(VersionProvider.class);

	private String appVersion;
	private String buildVersion;

	public VersionProvider() {
		ResourceBundle resourceBundle;
		try {
			resourceBundle = ResourceBundle.getBundle("version");
			appVersion = resourceBundle.getString("version");
//			buildVersion = resourceBundle.getString("build.version");
		} catch (MissingResourceException e) {
			LOG.debug("Resource bundle 'pom' was not found");
		}
	}

	public String getApplicationVersion() {
		return appVersion;
	}
	
	public String getBuildVersion(){
		return buildVersion;
	}
}