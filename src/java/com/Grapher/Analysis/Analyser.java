package com.Grapher.Analysis;

import com.Grapher.CustomGraph.CustomEdge;
import com.Grapher.CustomGraph.CustomVertex;

// JGraphT
import org.jgrapht.Graph;
import org.jgrapht.ListenableGraph;
import org.jgrapht.graph.DefaultListenableGraph;
import org.jgrapht.ext.JGraphXAdapter;

import org.jgrapht.alg.clustering.KSpanningTreeClustering;

import org.jgrapht.*;
import org.jgrapht.alg.connectivity.*;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm.*;
import org.jgrapht.alg.interfaces.*;
import org.jgrapht.alg.shortestpath.*;
import org.jgrapht.graph.*;

import java.util.*;

// Java
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JApplet;

// Log4J
import org.apache.log4j.Logger;

/** <code>Analyser</code> applies algorithms to Graph.
  * @author <a href="mailto:Julius.Hrivnac@cern.ch">J.Hrivnac</a> */
public class Analyser {
  
  /** Create and associate an algoritm.
    * @param algorithmName The name of the algorithm to execute. */ 
  public Analyser(String algorithmName) {
    _algorithmName = algorithmName; 
    }
    
  /** Fill the {@link Graph} to be acted upon.
    * @param graph The graph to be acted upon. */
  public void fill(Graph<CustomVertex, CustomEdge> graph) {
    _graph = graph;
    }
  
  /** Apply the algorithm. */
  public void apply() {
    log.info("Applying " + _algorithmName + " ...");
    switch (_algorithmName) {
      case "sc":
        applyStrongConnectivity();
      break;
      case "cl":
        applyClustering(5); // TBD: make cli parameter
      break;
      default:
        log.fatal("Unknown algorithm: " + _algorithmName);
      }
    }
    
  // Algorithms ================================================================    
    
  /** Apply <em>Strong Connectivity</em> algorithm. */
  public void applyStrongConnectivity() {            
    StrongConnectivityAlgorithm<CustomVertex, CustomEdge> scAlg = new KosarajuStrongConnectivityInspector<>(_graph);
    List<Set<CustomVertex>> stronglyConnectedSets = scAlg.stronglyConnectedSets();
    log.info("Strongly Connected Sets:");
    for (Set<CustomVertex> set : stronglyConnectedSets) {
      log.info("\t" + set);
      }
    }
    
  /** Apply <em>Clustering</em> algorithm.
    * @param nClusters The required number of clusters. */
  public void applyClustering(int nClusters) { 
    log.info("Searching for " + nClusters + " clusters");
    ClusteringAlgorithm<CustomVertex> clAlg = new KSpanningTreeClustering(new AsUndirectedGraph(_graph), nClusters);
    List<Set<CustomVertex>> clusterSets = clAlg.getClustering().getClusters();
    log.info("Clusters:");
    for (Set<CustomVertex> cluster : clusterSets) {
      log.info("\t" + cluster);
      }
    }
    
   private Graph<CustomVertex, CustomEdge> _graph;
   
   private String _algorithmName;   
    
   /** Logging . */
    private static Logger log = Logger.getLogger(Analyser.class);
  
  }
