package de.maredit.tar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import de.maredit.tar.listeners.ContextListener;
import de.maredit.tar.listeners.StartupListener;
import de.maredit.tar.providers.VersionProvider;

@SpringBootApplication
@EnableScheduling
public class Main {
	private static final Logger LOG = LogManager.getLogger(Main.class);

	public static VersionProvider versionProvider = new VersionProvider();

	public static void main(String[] args) {
		SpringApplication springApplication = new SpringApplication(Main.class);
		springApplication.addListeners(new StartupListener(),
				new ContextListener());
		springApplication.run(args);

		LOG.debug("App-Version: {}", versionProvider.getApplicationVersion());
		LOG.debug("Build-Version: {}", versionProvider.getBuildVersion());
	}
}