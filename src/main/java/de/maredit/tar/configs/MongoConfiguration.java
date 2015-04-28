package de.maredit.tar.configs;

import de.maredit.tar.models.converters.DateToLocalDateConverter;
import de.maredit.tar.models.converters.DateToLocalDateTimeConverter;
import de.maredit.tar.models.converters.LocalDateTimeToDateConverter;
import de.maredit.tar.models.converters.LocalDateToDateConverter;
import de.maredit.tar.properties.MongoProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.convert.CustomConversions;

import com.mongodb.Mongo;
import com.mongodb.MongoURI;

import java.util.Arrays;

/**
 * Created by czillmann on 24.04.15.
 */

@Configuration
public class MongoConfiguration extends AbstractMongoConfiguration {

  @Autowired
  MongoProperties mongoProperties;

  @SuppressWarnings("unused")
  private static final Logger LOG = LoggerFactory.getLogger(MongoConfiguration.class);

  @Override
  public CustomConversions customConversions() {
    return new CustomConversions(Arrays.asList(new DateToLocalDateTimeConverter(),
        new LocalDateTimeToDateConverter(), new DateToLocalDateConverter(),
        new LocalDateToDateConverter()));
  }

  @Override
  protected String getDatabaseName() {
    return mongoProperties.getDatabase();
  }

  @Override
  public Mongo mongo() throws Exception {
    StringBuffer uriBuffer = new StringBuffer("mongodb://");
    uriBuffer.append(mongoProperties.getHost());
    uriBuffer.append(":");
    uriBuffer.append(mongoProperties.getPort());
    return new Mongo(new MongoURI(uriBuffer.toString()));
  }
}
