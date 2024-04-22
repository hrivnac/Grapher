package com.Grapher.Utils;

import com.Grapher.CustomGraph.CustomVertex;
import com.Grapher.CustomGraph.CustomEdge;

// Lomikel
import com.Lomikel.Utils.NotifierURL;

// Java
import java.util.Map;
import java.util.HashMap;

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
    Map<String, String> vertexNames = new HashMap<>();
    vertexNames.put("source",            "objectId");
    vertexNames.put("alert",             "jd");
    vertexNames.put("AlertsOfInterest",  "cls");
    vertexNames.put("SourcesOfInterest", "cls");
    CustomVertex.setNamesMap(vertexNames);
    Map<String, String> edgeNames = new HashMap<>();
    edgeNames.put("distance",     "difference");
    edgeNames.put("contains",     "weight");
    edgeNames.put("deepcontains", "weight");
    edgeNames.put("overlaps",     "intersection");
    edgeNames.put("has",          "has");
    CustomEdge.setNamesMap(edgeNames);
    Map<String, String> edgeWeights = new HashMap<>();
    edgeNames.put("distance",     "difference");
    edgeNames.put("contains",     "weight");
    edgeNames.put("deepcontains", "weight");
    edgeNames.put("overlaps",     "intersection");
    CustomEdge.setWeightsMap(edgeWeights);
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
