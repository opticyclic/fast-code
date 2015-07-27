package org.fastcode.exception;

import java.io.FileInputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class FastCodeLogger {
	public static void main(final String args[]) {
	    try {
	    	final FileInputStream fis =  new FileInputStream("resources/logging.properties");
	        LogManager.getLogManager().readConfiguration(fis);
	    //  final LogManager lm = LogManager.getLogManager();
	      Logger logger;
	     /// final FileHandler fh = new FileHandler("log_test.txt");

	      logger = Logger.getLogger("LoggingExample1");

	     // lm.addLogger(logger);
	    //  logger.setLevel(Level.SEVERE);
	    //  fh.setFormatter(new XMLFormatter());

	     // logger.addHandler(fh);
	      // root logger defaults to SimpleFormatter. We don't want messages
	      // logged twice.
	      //logger.setUseParentHandlers(false);
	      logger.log(Level.INFO, "test 1");
	      logger.log(Level.INFO, "test 2");
	      logger.log(Level.INFO, "test 3");
	      logger.log(Level.SEVERE, "test sever");
	      logger.log(Level.FINE, "test fine");
	      logger.log(Level.FINER, "test finer");
	      logger.log(Level.WARNING, "test warn");
	      logger.log(Level.CONFIG, "test config");

	     // fis.close();
	    } catch (final Exception e) {
	      System.out.println("Exception thrown: " + e);
	      e.printStackTrace();
	    }
	  }
}
