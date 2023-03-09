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

/** <code>CustomVertex</code> represents graph vertex.
  * @opt attributes
  * @opt operations
  * @opt types
  * @opt visibility
  * @author <a href="mailto:Julius.Hrivnac@cern.ch">J.Hrivnac</a> */
public class CustomVertex {

  public CustomVertex() {
    this(_gid++);
    }

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

  public int getId() {
    return _id;
    }

  public void setId(int id) {
    _id = id;
    }

  public String getLbl() {
    return _attributes.get("labelV").getValue();
    }
    
  public String getName() {
    if (getLbl().equals("source")) {
      return getAttribute("objectId").getValue();
      }
    else if (getLbl().equals("PCA")) {
        return "PCA";
        }
    return "none";
    }
    
  @Override
  public String toString() {
    return getLbl() + "(" + _id + ")";
    }
  
  private int _id;
  
  private static int _gid = 0;
  
  private Map<String, Attribute> _attributes = new HashMap<>();
        
  /** Logging . */
  private static Logger log = Logger.getLogger(CustomVertex.class);

  }
 