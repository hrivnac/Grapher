package com.Grapher.GUI;

import com.Grapher.CustomGraph.CustomEdge;
import com.Grapher.CustomGraph.CustomVertex;
import com.Grapher.CustomGraph.CustomVertexSupplier;

// JGraphT
import org.jgrapht.*;
import org.jgrapht.graph.*;
import org.jgrapht.graph.builder.*;
import org.jgrapht.nio.*;
import org.jgrapht.nio.graphml.*;
import org.jgrapht.nio.dot.*;
import org.jgrapht.nio.graph6.*;
import org.jgrapht.nio.matrix.*;
import org.jgrapht.util.*;
import org.jgrapht.ext.*;

// JGraphX
import com.mxgraph.layout.*;
import com.mxgraph.swing.*;

// Java
import java.io.*;
import java.nio.charset.*;
import java.util.*;
import java.net.URI;
import javax.swing.*;
import java.awt.*;

// Log4J
import org.apache.log4j.Logger;

/** <code>Shower</code> shows Graph in a graphical window.
  * Adapted  from
  * <a href="https://github.com/jgrapht/jgrapht/blob/master/jgrapht-io/src/test/java/org/jgrapht/nio/graphml/GraphMLImporterTest.java">GraphMLImporterTest</a>.
  * @opt attributes
  * @opt operations
  * @opt types
  * @opt visibility
  * @author <a href="mailto:Julius.Hrivnac@cern.ch">J.Hrivnac</a> */
public class Shower extends JApplet{
  
  public Shower() {
    init();
    JFrame frame = new JFrame();
    frame.getContentPane().add(this);
    frame.setTitle("Grapher");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.pack();
    frame.setVisible(true);
    }
  
  public void show(Graph<CustomVertex, CustomEdge> graph) {
    ListenableGraph<CustomVertex, CustomEdge> g = new DefaultListenableGraph<>(graph);
    jgxAdapter = new JGraphXAdapter<>(g);    
    setPreferredSize(DEFAULT_SIZE);
    mxGraphComponent component = new mxGraphComponent(jgxAdapter);
    component.setConnectable(false);
    component.getGraph().setAllowDanglingEdges(false);
    getContentPane().add(component);
    resize(DEFAULT_SIZE);
    mxCircleLayout layout = new mxCircleLayout(jgxAdapter);
    int radius = 100;
    layout.setX0((DEFAULT_SIZE.width / 2.0) - radius);
    layout.setY0((DEFAULT_SIZE.height / 2.0) - radius);
    layout.setRadius(radius);
    layout.setMoveCircle(true);    
    layout.execute(jgxAdapter.getDefaultParent());
    }

  private JGraphXAdapter<CustomVertex, CustomEdge> jgxAdapter;

  private static final Dimension DEFAULT_SIZE = new Dimension(530, 320);
  
  }
