package de.maredit.tar.services;

import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;


@ConfigurationProperties(locations = "classpath:group-mapping.yaml")
@Service
public class AuthorityMappingService {

  private Map<String, List<String>> groups;

  public Map<String, List<String>> getGroups() {
    return groups;
  }

  public void setGroups(Map<String, List<String>> groups) {
    this.groups = groups;
  }
}
