package com.Grapher.Analysis.Algorithms;

import com.Grapher.CustomGraph.CustomEdge;
import com.Grapher.CustomGraph.CustomVertex;
import com.Grapher.Utils.Params;

// JGraphT
import org.jgrapht.Graph;
import org.jgrapht.graph.AsUndirectedGraph;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.AttributeType;
import org.jgrapht.alg.connectivity.KosarajuStrongConnectivityInspector;
import org.jgrapht.alg.interfaces.StrongConnectivityAlgorithm;
import org.jgrapht.alg.interfaces.ClusteringAlgorithm;
import org.jgrapht.alg.clustering.GirvanNewmanClustering;
import org.jgrapht.alg.clustering.LabelPropagationClustering;
import org.jgrapht.alg.clustering.KSpanningTreeClustering;

// Java
import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.LinkedHashSet;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toCollection;

// Log4J
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/** <code>ConnectivityAlgorithm</code> calculates the level of connectivity between vertices.
  * @author <a href="mailto:Julius.Hrivnac@cern.ch">J.Hrivnac</a> */
public class ConnectivityAlgorithm {
  
  /** Create and run.
    * @param graph The {@link Graph} to analyse.
    * @param weighted Whether to take edge weights into account
    *                 (or just count each edge as<tt>1</tt>). */
  public ConnectivityAlgorithm(Graph<CustomVertex, CustomEdge> graph,
                               boolean                         weighted) {
    double w;
    for (CustomVertex v : graph.vertexSet()) {
      w = 0;
      for (CustomEdge e : graph.edgesOf(v)) {
        w += weighted ? e.weight() : 1;
        }
      _connectivity.put(v, w);
      }
    Map<CustomVertex, Double> sortedConnectivity = _connectivity.entrySet().
                                                                 stream().
                                                                 sorted(Map.Entry.comparingByValue()).
                                                                 collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));  
    _connectivity = sortedConnectivity;
    }
    
   /** Give the most connected Vertexes.
     * @param n The number of entried to give. 
     * @return  The most connected Vertexes. */
   public Map<CustomVertex, Double> getMostConnected(int n) {
     Map<CustomVertex, Double> least = new LinkedHashMap<>();
     int k = 0;
     for (Map.Entry<CustomVertex, Double> entry : _connectivity.entrySet()) {
       if ((_connectivity.size() - k++) <= n) {
         least.put(entry.getKey(), entry.getValue());
         }
       }
     return least;
     }
     
   /** Give the least connected Vertexes.
     * @param n The number of entried to give. 
     * @return  The least connected Vertexes. */
   public Map<CustomVertex, Double> getLeastConnected(int n) {
     Map<CustomVertex, Double> most = new LinkedHashMap<>();
     int k = 0;
     for (Map.Entry<CustomVertex, Double> entry : _connectivity.entrySet()) {
       if (k++ < n) {
         most.put(entry.getKey(), entry.getValue());
         }
       }
     return most;
     }
    
   private Map<CustomVertex, Double> _connectivity = new LinkedHashMap<>();   

   /** Logging . */
   private static Logger log = LogManager.getLogger(ConnectivityAlgorithm.class);
  
  }
