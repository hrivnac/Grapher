package com.Grapher.CustomGraph;

// Java
import java.util.function.Supplier;

// Log4J
import org.apache.log4j.Logger;

/** <code>CustomVertexSupplier</code> supplies {@link Supplier} for {@link CustomVertex}.
  * @opt attributes
  * @opt operations
  * @opt types
  * @opt visibility
  * @author <a href="mailto:Julius.Hrivnac@cern.ch">J.Hrivnac</a> */
public class CustomVertexSupplier implements Supplier<CustomVertex> {

  @Override
  public CustomVertex get() {
    return new CustomVertex(id++);
    }
    
  private int id = 0;

  }