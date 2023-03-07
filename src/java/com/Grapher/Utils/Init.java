package com.Grapher.Utils;

// Log4J
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/** <code>Init</code> provides common initialisation.
  * @opt attributes
  * @opt operations
  * @opt types
  * @opt visibility
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
      PropertyConfigurator.configure(Init.class.getClassLoader().getResource("com/Grapher/Utils/log4j.properties"));
      //NotifierURL.notify("", "Grapher", Info.release());
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
  private static Logger log = Logger.getLogger(Init.class);

  }
