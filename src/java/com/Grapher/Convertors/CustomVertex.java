package com.Grapher.Convertors;

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
    this(gid++);
    }

  public CustomVertex(int id) {
    this.id  = id;
    }

  @Override
  public int hashCode() {
    return id;
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
    if (id == 0) {
      return other.id == 0;
      }
    else {
      return id == other.id;
      }
    }

  public Map<String, Attribute> getAttributes() {
    return attributes;
    }

  public void setAttributes(Map<String, Attribute> attributes) {
    this.attributes = attributes;
    }

  public int getId() {
    return id;
    }

  public void setId(int id) {
    this.id = id;
    }

  @Override
  public String toString() {
    return "" + id;
    }
  
  private int id;
  
  private static int gid = 0;
  
  private Map<String, Attribute> attributes;
        
  /** Logging . */
  private static Logger log = Logger.getLogger(CustomVertex.class);

  }
 