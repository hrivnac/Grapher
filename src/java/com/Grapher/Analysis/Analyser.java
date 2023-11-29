package com.Grapher.Analysis;

import com.Grapher.CustomGraph.CustomEdge;
import com.Grapher.CustomGraph.CustomVertex;
import com.Grapher.Apps.CLI;

// JGraphT
import org.jgrapht.Graph;
import org.jgrapht.ListenableGraph;
import org.jgrapht.graph.DefaultListenableGraph;
import org.jgrapht.ext.JGraphXAdapter;

import org.jgrapht.alg.clustering.KSpanningTreeClustering;
import org.jgrapht.nio.AttributeType;
import org.jgrapht.nio.DefaultAttribute;

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
    * @param cli The calling {@link CLI}. */ 
  public Analyser(CLI cli) {
    _cli = cli; 
    }
    
  /** Fill the {@link Graph} to be acted upon.
    * @param graph The graph to be acted upon. */
  public void fill(Graph<CustomVertex, CustomEdge> graph) {
    _graph = graph;
    }
  
  /** Apply the algorithm.
    * @param algorithmName The name of the algorithm to execute.
    * @param params        The algorithm parameters. */ 
  public void apply(String algorithmName,
                    String params) {
    switch (algorithmName) {
      case "ad":
        addDistances();
      break;
      case "sc":
        applyStrongConnectivity();
      break;
      case "cl":
        int nClusters = 1;
        if (params != null && !params.trim().equals("")) {
          nClusters = new Integer(params);
          }
        applyClustering(nClusters);
      break;
      default:
        log.fatal("Unknown algorithm: " + algorithmName);
      }
    }
    
  // Algorithms ================================================================    

  // TBD: allow chain of algorithms
  
  /** Apply <em>Strong Connectivity</em> algorithm. */
  public void applyStrongConnectivity() {            
    log.info("Applying Strong Connectivity Algorithm ...");
    StrongConnectivityAlgorithm<CustomVertex, CustomEdge> scAlg = new KosarajuStrongConnectivityInspector<>(_graph);
    List<Set<CustomVertex>> stronglyConnectedSets = scAlg.stronglyConnectedSets();
    log.info("Strongly Connected Sets:");
    for (Set<CustomVertex> set : stronglyConnectedSets) {
      log.info("\t" + set);
      }
    }
    
  /** Apply <em>Clustering</em> algorit_quiethm.
    * @param nClusters The required number of clusters. */
  public void applyClustering(int nClusters) { 
    log.info("Applying Clustering Algorithm ...");
    log.info("\tsearching for " + nClusters + " clusters");
    ClusteringAlgorithm<CustomVertex> clAlg = new KSpanningTreeClustering(new AsUndirectedGraph(_graph), nClusters);
    List<Set<CustomVertex>> clusterSets = clAlg.getClustering().getClusters();
    log.info("Clusters:");
    for (Set<CustomVertex> cluster : clusterSets) {
      log.info("\t" + cluster);
      }
    }
    
   /** Add distances between {@link CustomVertex}s. */
   // TBD: write detailed doc
   public void addDistances() {
   log.info("Adding distances ...");
     CustomEdge e;
     int n1 = 0;
     int n2 = 0;
     for (CustomVertex v1 : _graph.vertexSet()) {
       n1++;
       n2 = 0;
       for (CustomVertex v2 : _graph.vertexSet()) {
         n2++;
         if (n2 > n1) {
           e = new CustomEdge();
           e.putAttribute("difference", DefaultAttribute.createAttribute(difference(v1, v2)));
           e.putAttribute("labelE",     DefaultAttribute.createAttribute("distance"        ));
           _graph.addEdge(v1, v2, e);
           }
         }
       }
     }
     
   /** Give the difference between {@link CustomVertex}s.
     * @param v1 The first {@link CustomVertex}.
     * @param v2 The second {@link CustomVertex}.
     * @return   The quadratic distance. */
   private double difference(CustomVertex v1,
                             CustomVertex v2) {
     double diff = 0;
     for (String pca : PCAs) {
       diff += Math.pow(Double.valueOf(v1.getAttribute(pca).getValue()) - 
                        Double.valueOf(v2.getAttribute(pca).getValue()), 2);
       }
     return Math.sqrt(diff);      
     }
     
// =============================================================================     
    
  /** Give current {@link Graph}.
    * @return The current {@link Graph}. May be result of an algoritm processing. */
  public Graph<CustomVertex, CustomEdge> graph() {    
    return _graph;
    }
    
   private Graph<CustomVertex, CustomEdge> _graph;
   
   private CLI _cli;
   
   private static String[] PCAs = new String[]{"pca00",
                                               "pca01",
                                               "pca02",
                                               "pca03",
                                               "pca04",
                                               "pca05",
                                               "pca06",
                                               "pca07",
                                               "pca08",
                                               "pca09",
                                               "pca10",
                                               "pca11",
                                               "pca12",
                                               "pca13",
                                               "pca14",
                                               "pca15",
                                               "pca16",
                                               "pca17",
                                               "pca18",
                                               "pca19",
                                               "pca20",
                                               "pca21",
                                               "pca22",
                                               "pca23",
                                               "pca24"};
    
   /** Logging . */
    private static Logger log = Logger.getLogger(Analyser.class);
  
  }
