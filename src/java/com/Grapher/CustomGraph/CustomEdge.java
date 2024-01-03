package com.Grapher.CustomGraph;

// JGraphT
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.AttributeType;
import org.jgrapht.nio.DefaultAttribute;

// Java
import java.util.Map;
import java.util.HashMap;

// Log4J
import org.apache.log4j.Logger;

/** <code>CustomEdge</code> represents graph edge.
  * @author <a href="mailto:Julius.Hrivnac@cern.ch">J.Hrivnac</a> */
public class CustomEdge extends DefaultWeightedEdge {

  /** Create new Edge from the default id. */
  public CustomEdge() {
    this(_gid++);
    }

  /** Create new Edge from the supplied id.
    * @param id The supplied Edge id. */
  public CustomEdge(long id) {
    _id  = id;
    }

  @Override
  public int hashCode() {
    return Long.hashCode(_id);
    }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
      }
    if (obj == null) {
      return false;
      }
    if (getClass() != obj.getClass()) {
      return false;
      }
    CustomEdge other = (CustomEdge)obj;
    if (_id == 0) {
      return other._id == 0;
      }
    else {
      return _id == other._id;
      }
    }

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
    if (!_attributesReg.containsKey(name)) {
      _attributesReg.put(name, value.getType());
      }
    }

  /** Give the Edge id.
    * @return The Edge id. */
  public long getId() {
    return _id;
    }

  /** Set the Edge id.
    * @param id The Edge id. */
  public void setId(long id) {
    _id = id;
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
    
  /** TBD */
  public double weight() {
    return getWeight();
    }
    
  /** Give all registered {@link Attribute}s.
    * @return The registered {@link Attribute}s. */
  public static Map<String, AttributeType> attributesReg() {
    return _attributesReg;
    }
     
  @Override
  public String toString() {
    return getLbl() + "(" + _id + ")" + ":" + getName();
    }
  
  private long _id;
  
  private static long _gid = 0;
    
  private Map<String, Attribute> _attributes = new HashMap<>();
  
  private static Map<String, AttributeType> _attributesReg = new HashMap<>();
        
  /** Logging . */
  private static Logger log = Logger.getLogger(CustomEdge.class);
  
  }