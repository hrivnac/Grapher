package com.Grapher.GUI;

import com.Grapher.CustomGraph.CustomEdge;
import com.Grapher.CustomGraph.CustomVertex;

// JGraphT
import org.jgrapht.Graph;
import org.jgrapht.ListenableGraph;
import org.jgrapht.graph.DefaultListenableGraph;
import org.jgrapht.ext.JGraphXAdapter;

// JGraphX
import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.swing.mxGraphComponent;

// Java
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JApplet;

/** <code>Shower</code> shows Graph in a graphical window.
  * Adapted  from
  * <a href="https://github.com/jgrapht/jgrapht/blob/master/jgrapht-io/src/test/java/org/jgrapht/nio/graphml/GraphMLImporterTest.java">GraphMLImporterTest</a>.
  * @opt attributes
  * @opt operations
  * @opt types
  * @opt visibility
  * @author <a href="mailto:Julius.Hrivnac@cern.ch">J.Hrivnac</a> */
public class Shower extends JApplet{
  
  /** Create {@link JApplet}. */ 
  public Shower() {
    init();
    JFrame frame = new JFrame();
    frame.getContentPane().add(this);
    frame.setTitle("Grapher");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.pack();
    frame.setVisible(true);
    }
  
  /** Show {@link Graph}.
    * @param graph The {@link Graph} to show. */
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
