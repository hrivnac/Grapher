package com.Grapher.CustomGraph;

// JGraphT
import org.jgrapht.*;
import org.jgrapht.graph.*;
import org.jgrapht.graph.builder.*;
import org.jgrapht.nio.*;
import org.jgrapht.nio.graphml.*;
import org.jgrapht.nio.dot.*;
import org.jgrapht.nio.graph6.*;
import org.jgrapht.util.*;

// Java
import java.io.*;
import java.nio.charset.*;
import java.util.*;
import java.net.URI;

// Log4J
import org.apache.log4j.Logger;

/** <code>CustomEdge</code> represents graph edge.
  * @opt attributes
  * @opt operations
  * @opt types
  * @opt visibility
  * @author <a href="mailto:Julius.Hrivnac@cern.ch">J.Hrivnac</a> */
public class CustomEdge extends DefaultWeightedEdge {

  public Map<String, Attribute> getAttributes() {
    return _attributes;
    }

  public Attribute getAttribute(String name) {
    return _attributes.get(name);
    }

  public void putAttribute(String name,
                           Attribute value) {
    _attributes.put(name, value);
    }

  public String getLbl() {
    return _attributes.get("labelE").getValue();
    }
     
  public String getName() {
    if (getLbl().equals("distance")) {
      return getAttribute("difference").getValue();
      }
    else if (getLbl().equals("has")) {
        return "has";
        }
    return "none";
    }
     
  @Override
  public String toString() {
    return getLbl();
    }
    
  private Map<String, Attribute> _attributes = new HashMap<>();
        
  /** Logging . */
  private static Logger log = Logger.getLogger(CustomEdge.class);
  
  }