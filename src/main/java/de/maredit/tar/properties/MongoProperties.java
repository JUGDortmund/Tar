package de.maredit.tar.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by czillmann on 24.04.15.
 */

@Component
@ConfigurationProperties(prefix = "spring.data.mongodb")
public class MongoProperties {

  private String host;
  private Integer port;
  private String database;
  private Boolean embedded;

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public Integer getPort() {
    return port;
  }

  public void setPort(Integer port) {
    this.port = port;
  }

  public String getDatabase() {
    return database;
  }

  public void setDatabase(String database) {
    this.database = database;
  }

  public Boolean isEmbedded() {
    return embedded;
  }

  public void setEmbedded(Boolean embedded) {
    this.embedded = embedded;
  }
}
