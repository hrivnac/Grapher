package com.Grapher.Utils;

// Lomikel
import com.Lomikel.Utils.NotifierURL;

// Log4J
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/** <code>Init</code> provides common initialisation.
  * @author <a href="mailto:Julius.Hrivnac@cern.ch">J.Hrivnac</a> */
public class Init {

  /** Setup system. Singleton.
    * @param quiet If no outupt is required. */
  public static void init(boolean quiet) {
    if (_initialised) {
      log.debug("Grapher already initialised");
      return;
      }
    try {
      NotifierURL.notify("", "Grapher", Info.release());
      }
    catch (Exception e) {
      System.err.println(e);
      }
    _initialised = true;
    if (!quiet) {
      log.info("Grapher initialised, version: " + Info.release());
      }
    }
    
  public static boolean _initialised = false;  
    
  /** Logging . */
  private static Logger log = LogManager.getLogger(Init.class);

  }
