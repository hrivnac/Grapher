package com.Grapher.CustomGraph;

// JGraphT
import org.jgrapht.nio.Attribute;

// Java
import java.util.Map;
import java.util.HashMap;

// Log4J
import org.apache.log4j.Logger;

/** <code>CustomVertex</code> represents graph vertex.
  * @author <a href="mailto:Julius.Hrivnac@cern.ch">J.Hrivnac</a> */
public class CustomVertex {

  /** Create new Vertex from the default id. */
  public CustomVertex() {
    this(_gid++);
    }

  /** Create new Vertex from the supplied id.
    * @param id The supplied Vertex id. */
  public CustomVertex(int id) {
    _id  = id;
    }

  @Override
  public int hashCode() {
    return _id;
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
    CustomVertex other = (CustomVertex)obj;
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
    * @param value The val;ue of the new {@link Attribute}. */
  public void putAttribute(String name,
                           Attribute value) {
    _attributes.put(name, value);
    }

  /** Give the Vertex id.
    * @return The Vertex id. */
  public int getId() {
    return _id;
    }

  /** Set the Vertex id.
    * @param id The Vertex id. */
  public void setId(int id) {
    _id = id;
    }

  /** Give the Vertex label (type).
    * @return The Vertex label (type). */
  public String getLbl() {
    return _attributes.get("labelV").getValue();
    }
    
  /** Give the Vertex name.
    * @return The Vertex name. */
  public String getName() {
    if (getLbl().equals("source")) {
      return getAttribute("objectId").getValue();
      }
    else if (getLbl().equals("PCA")) {
      if (getAttribute("objectId") != null) {
        return getAttribute("objectId").getValue();
        }
      else {
        return "PCA";
        }
      }
    return "none";
    }
    
  @Override
  public String toString() {
    return getLbl() + "(" + _id + ")" + ":" + getName();
    }
  
  private int _id;
  
  private static int _gid = 0;
  
  private Map<String, Attribute> _attributes = new HashMap<>();
        
  /** Logging . */
  private static Logger log = Logger.getLogger(CustomVertex.class);

  }
 