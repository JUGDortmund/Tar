package de.maredit.tar.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by phorninge on 11.05.15.
 */

@Component
@ConfigurationProperties(prefix = "spring.number")
public class VersionProperties {

  private String build;

  public String getBuild() {
    return build;
  }

  public void setBuild(String build) {
    this.build = build;
  }
}
