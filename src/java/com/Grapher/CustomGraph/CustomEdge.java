package com.Grapher.CustomGraph;

// JGraphT
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.DefaultAttribute;

// Java
import java.util.Map;
import java.util.HashMap;

// Log4J
import org.apache.log4j.Logger;

/** <code>CustomEdge</code> represents graph edge.
  * @author <a href="mailto:Julius.Hrivnac@cern.ch">J.Hrivnac</a> */
public class CustomEdge extends DefaultWeightedEdge {

  /** Give {@link Map} of all {@link Attribute}s.
    * @return The {@link Map} of all {@link Attribute}s. */
  public Map<String, Attribute> getAttributes() {
    return _attributes;
    }

  /** Give one {@link Attribute}.
    * @param name The name of the required {@link Attribute}.
    * @return     The corresponding {@link Attribute}. */
  public Attribute getAttribute(String name) {
    return _attributes.get(name);
    }

  /** Put one {@link Attribute}.
    * @param name  The name of the new {@link Attribute}.
    * @param value The value of the new {@link Attribute}. */
  public void putAttribute(String name,
                           Attribute value) {
    _attributes.put(name, value);
    }

  /** Give the Edge label (type).
    * @return The Edge label (type). */
  public String getLbl() {
    return _attributes.get("labelE").getValue();
    }
     
  /** Give the Edge name.
    * @return The Edge name. */
  public String getName() {
    if (getLbl().equals("distance")) {
      return getAttribute("difference").getValue();
      }
    else if (getLbl().equals("has")) {
      return "has";
      }
    return "none";
    }

  /** Generate the Edge weight.
    * @return The generated weight. */
  public double generateWeight() {
    if (getLbl().equals("distance")) {
      return new Double(getAttribute("difference").getValue());
      }
    return 0;
    }
     
  @Override
  public String toString() {
    return getLbl() + ":" + getName();
    }
    
  private Map<String, Attribute> _attributes = new HashMap<>();
        
  /** Logging . */
  private static Logger log = Logger.getLogger(CustomEdge.class);
  
  }