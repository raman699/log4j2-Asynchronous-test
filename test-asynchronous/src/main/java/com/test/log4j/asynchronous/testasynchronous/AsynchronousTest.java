package com.test.log4j.asynchronous.testasynchronous;

import java.util.Iterator;
import java.util.Set;

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
		while (counter < 10) {
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

		Thread asyncLogThread = getAsyncLogThread();
		try {
			asyncLogThread.join(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("State" + asyncLogThread.getState());
		config.getLoggerConfig(loggerName).getAppenders().get(appenderName).stop();
		config.getLoggerConfig(loggerName).removeAppender(appenderName);
		config.removeLogger(loggerName);
		ctx.updateLoggers();
	}

	// the below code did not work
//	public void closeLogger2() {
//
//		Thread asyncLogThread = getAsyncLogThread();
//
//		while (asyncLogThread.getState() != Thread.State.TIMED_WAITING) {
//			try {
//				System.out.println("thread state is " + Thread.State.TIMED_WAITING);
//				asyncLogThread.wait(1000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//
//		System.out.println("State" + asyncLogThread.getState());
//		config.getLoggerConfig(loggerName).getAppenders().get(appenderName).stop();
//		config.getLoggerConfig(loggerName).removeAppender(appenderName);
//		config.removeLogger(loggerName);
//		ctx.updateLoggers();
//	}

	private Thread getAsyncLogThread() {
		Thread asyncLogThread = null;
		Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
		Iterator<Thread> itr = threadSet.iterator();
		while (itr.hasNext()) {
			asyncLogThread = itr.next();
			if (asyncLogThread.getClass() == org.apache.logging.log4j.core.util.Log4jThread.class) {
				break;
			}
		}
		System.out.println(asyncLogThread.isAlive() + " state " + asyncLogThread.getState());
		return asyncLogThread;
	}
}
