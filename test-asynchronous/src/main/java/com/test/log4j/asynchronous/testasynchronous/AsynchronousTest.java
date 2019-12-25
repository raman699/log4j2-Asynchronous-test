package com.test.log4j.asynchronous.testasynchronous;

import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;

public class AsynchronousTest {

	int counter = 0;

	LoggerContext ctx;

	Configuration config;

	Logger logger;

	String loggerName = "testLogger";

	String appenderName = "consoleAppender";

	static String testMessage = "This is a Test Message";

	public void log() {

		final ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();
		config = builder.build();
		ctx = Configurator.initialize(config);
		config = ctx.getConfiguration();
		ctx.start(config);
		ctx.updateLoggers();
		logger = ctx.getLogger(loggerName);
		logger.addAppender(attachConsoleAppender(ctx.getConfiguration(), appenderName));
		while (counter < 2000) {
			logger.error(testMessage + counter);
			counter++;
		}
		closeLogger();
	}

	private Appender attachConsoleAppender(Configuration config, String appenderName) {
		Appender consoleAppender = ConsoleAppender.newBuilder().setConfiguration(config).setName(appenderName)
				.withImmediateFlush(true).build();
		consoleAppender.start();
		return consoleAppender;
	}

	public void closeLogger() {

		config.getLoggerConfig(loggerName).getAppenders().get(appenderName).stop();
		config.getLoggerConfig(loggerName).removeAppender(appenderName);
		config.removeLogger(loggerName);
		ctx.updateLoggers();
	}
}
