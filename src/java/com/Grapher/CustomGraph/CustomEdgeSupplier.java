package com.Grapher.CustomGraph;

// Java
import java.util.function.Supplier;

/** <code>CustomEdgeSupplier</code> supplies {@link Supplier} for {@link CustomEdge}.
  * @author <a href="mailto:Julius.Hrivnac@cern.ch">J.Hrivnac</a> */
public class CustomEdgeSupplier implements Supplier<CustomEdge> {

  @Override
  public CustomEdge get() {
    return new CustomEdge(id++);
    }
    
  private int id = 0;

  }