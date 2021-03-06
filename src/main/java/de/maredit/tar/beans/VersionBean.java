package de.maredit.tar.beans;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public final class VersionBean {

  private static final Logger LOG = LogManager.getLogger(VersionBean.class);

  private String appVersion;
  
  private String revision;

  public VersionBean() {
    ResourceBundle resourceBundle;
    try {
      resourceBundle = ResourceBundle.getBundle("version");
      appVersion = resourceBundle.getString("version");
      revision = resourceBundle.getString("revision");
    } catch (MissingResourceException e) {
      LOG.debug("Resource bundle 'pom' was not found");
    }
  }

  public String getApplicationVersion() {
    return appVersion;
  }
  
  public String getRevision() {
    return revision;
  }
}
