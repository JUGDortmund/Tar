package de.maredit.tar.listeners;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;

import com.google.common.io.Resources;
import com.mongodb.BasicDBList;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import de.svenkubiak.embeddedmongodb.EmbeddedMongo;

public class StartupListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {
  private final Logger LOG = LoggerFactory.getLogger(StartupListener.class);
  
  private ConfigurableEnvironment environment;

  @Override
  public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
    this.environment = event.getEnvironment();
    Boolean embeddedMongo = this.environment.getProperty("spring.data.mongodb.embedded", Boolean.class);
    if (embeddedMongo != null && embeddedMongo.booleanValue()) {
      EmbeddedMongo.DB.port(this.environment.getProperty("spring.data.mongodb.port", Integer.class)).start();
    }

    if (this.environment.getProperty("spring.data.mongodb.preload", Boolean.class)) {
      preloadData("users");
      preloadData("vacations");
    }
  }

  private void preloadData(String collection) {
    DB db = EmbeddedMongo.DB.getMongoClient().getDB(this.environment.getProperty("spring.data.mongodb.database", String.class));
    
    DBCollection dbCollection = db.getCollection(collection);
    URL url = Resources.getResource("data/" + collection + ".json");
    try {
      BasicDBList basicDBList = (BasicDBList) JSON.parse(FileUtils.readFileToString(new File(url.getPath())));
      Iterator<Object> it = basicDBList.iterator();
      while (it.hasNext()) {
        dbCollection.save((DBObject) it.next());
      }
    } catch (IOException e) {
      LOG.error("Failed to preload data for collection: " + collection, e);
    }
  }
}