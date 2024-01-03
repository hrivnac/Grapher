package com.Grapher.Analysis;

import com.Grapher.CustomGraph.CustomEdge;
import com.Grapher.CustomGraph.CustomVertex;
import com.Grapher.Apps.Params;
import com.Grapher.Analysis.Algorithms.ConnectivityAlgorithm;

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
import java.util.TreeMap;
import static java.util.stream.Collectors.toCollection;

// Log4J
import org.apache.log4j.Logger;

/** <code>Analyser</code> applies algorithms to Graph.
  * @author <a href="mailto:Julius.Hrivnac@cern.ch">J.Hrivnac</a> */
public class Analyser {
  
  /** Create and associate an algoritm.
    * @param params The calling {@link Params}. */ 
  public Analyser(Params params) {
    _params = params; 
    }
    
  /** Fill the {@link Graph} to be acted upon.
    * @param graph The graph to be acted upon. */
  public void fill(Graph<CustomVertex, CustomEdge> graph) {
    _graph = graph;
    }
  
  /** Apply the algorithms. */
  public void apply() {
    String[] algpar;
    for (String alg : _params.algorithm().split(";")) {
      algpar = alg.split(",");
      switch (algpar[0]) {
        case "add":
          switch (algpar[1]) {
          case "all":
            addDistances("alert", "distance", "difference", null);
            break;
          case "radec":
            addDistances("alert", "distance", "difference", new String[]{"ra", "dec"});
            break;
          case "isolated":
            addDistances("alert", "distance", "difference", new String[]{"magnr" , "magpsf", "neargaia", "rf_kn_vs_nonkn", "rf_snia_vs_nonia", "sgscore1", "sigmagnr", "sigmapsf", "snn_sn_vs_all", "snn_snia_vs_nonia"});
            break;
          case "pca":
            addDistances("PCA", "distance", "difference", PCAs);
            break;
          case "immersion":
            addImmersion("alert", "connectivity", new String[]{"magnr" , "magpsf", "neargaia", "rf_kn_vs_nonkn", "rf_snia_vs_nonia", "sgscore1", "sigmagnr", "sigmapsf", "snn_sn_vs_all", "snn_snia_vs_nonia"});
            break;
          default:
            addDistances(algpar[1], algpar[2], algpar[3], new String[]{algpar[4]});
            }
          break;
        case "sc":
          applyStrongConnectivity();
          break;
        case "cl":
          applyClustering(algpar[1], new Integer(algpar[2]));
          break;
        case "co":
          applyConnectivity(new Integer(algpar[1]));
          break;
        default:
          log.error("Unknown algorithm: " + alg);
        }
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
    
  /** Apply <em>Connectivity</em> algorithm. */
  public void applyConnectivity(int n) {            
    log.info("Applying Connectivity Algorithm ...");
    ConnectivityAlgorithm coAlg = new ConnectivityAlgorithm(_graph);
    log.info("Most Connected:");
    for (Map.Entry<CustomVertex, Double> entry : coAlg.getMostConnected(n).entrySet()) {
      log.info("\t" + entry);
      }
    log.info("Least Connected:");
    for (Map.Entry<CustomVertex, Double> entry : coAlg.getLeastConnected(n).entrySet()) {
      log.info("\t" + entry);
      }
    }
    
  /** Apply <em>Clustering</em> algorit_quiethm.
    * @param alg       The algorithm name.
    * @param nClusters The required number of clusters. */
  public void applyClustering(String alg,
                              int    nClusters) { 
    log.info("Applying Clustering Algorithm ...");
    log.info("\tusingt " + alg + " algoritm");    
    log.info("\tsearching for " + nClusters + " clusters");    
    ClusteringAlgorithm<CustomVertex> clAlg;
    switch (alg) {
      case "GirvanNewman":
        clAlg = new GirvanNewmanClustering(_graph, nClusters);
        break;
      case "LabelPropagation":
        clAlg = new LabelPropagationClustering(new AsUndirectedGraph(_graph));
        break;
      case "KSpanningTree":
        clAlg = new KSpanningTreeClustering(new AsUndirectedGraph(_graph), nClusters);
        break;
       default:
         log.error("Unknown algorithm: " + alg);
         return;
       }
    List<Set<CustomVertex>> clusterSets = clAlg.getClustering().getClusters();
    log.info("Clusters:");
    for (Set<CustomVertex> cluster : clusterSets) {
      log.info("\t" + cluster);
      }
    }
    
   /** Add immersion, i.e. accumulated distance to all other {@link CustomVertex}s.
     * @param vertexLbl                The label of the {@link CustomVertex}s to connect with distances.
     * @param immersionAttributeName   The name of the new attribute carrying the accumulated distance value.
     * @param differenceAttributeNames The names of {@link CustomVertex} attributes to be used to calculate the distance.
     *                                 All numerical attributes will be used if <tt>null</tt>. */
   public void addImmersion(String   vertexLbl,
                            String   immersionAttributeName,
                            String[] differenceAttributeNames) {
     Set<CustomVertex> vs = _graph.vertexSet();
     if (differenceAttributeNames == null) {
       log.info("Using all attributes");
       Set<String> allAttributeNames = new TreeSet<>();
       for (CustomVertex v : vs) {
         if (v.getLbl().equals(vertexLbl)) {
           for (Map.Entry<String, AttributeType> entry : v.attributesReg().entrySet()) {
             if (entry.getValue().equals(AttributeType.INT   ) ||
                 entry.getValue().equals(AttributeType.LONG  ) ||
                 entry.getValue().equals(AttributeType.FLOAT ) ||
                 entry.getValue().equals(AttributeType.DOUBLE) ) {
               allAttributeNames.add(entry.getKey());
               }
             }
           }
         }
       differenceAttributeNames = allAttributeNames.toArray(new String[0]);
       }
     log.info("Normalising");
     TreeMap<String, Double> norm = new TreeMap<>();
     double sum = 0;
     int n  = 0;
     for (String x : differenceAttributeNames) {
       for (CustomVertex v : vs) {
         if (v.getLbl().equals(vertexLbl)) {
           sum += Double.valueOf(v.getAttribute(x).getValue());
           n++;
           }
         }
       norm.put(x, sum / n);
       } 
     log.info("Adding immersion of " + vertexLbl + ".[" + String.join(",", differenceAttributeNames) + "] recorded as " + immersionAttributeName);
     double min = Integer.MAX_VALUE;
     double max = Integer.MIN_VALUE;
     CustomVertex minV = null;
     CustomVertex maxV = null;
     for (CustomVertex v1 : vs) {
       if (v1.getLbl().equals(vertexLbl)) {
         sum = 0;
         for (CustomVertex v2 : vs) {
           if (v2.getLbl().equals(vertexLbl)) {
             sum += difference(v1, v2, differenceAttributeNames, norm);
             }
           }
         v1.putAttribute(immersionAttributeName, DefaultAttribute.createAttribute(sum)); 
         if (sum < min) {
           min = sum;
           minV = v1;
           }
         if (sum > max) {
           max = sum;
           maxV = v1;
           }
         }
       }
     log.info("Most isolated  " + vertexLbl + ": " + minV + " -> " + minV.getAttributes());
     log.info("Most connected " + vertexLbl + ": " + maxV + " -> " + maxV.getAttributes());
     }
    
   /** Add distances between {@link CustomVertex}s.
     * @param vertexLbl                The label of the {@link CustomVertex}s to connect with distances.
     * @param edgeLbl                  The label of the new {@link CustomEdge}.
     * @param edgeAttributeName        The name of the new {@link CustomEdge} attribute carrying the distance value.
     * @param differenceAttributeNames The names of {@link CustomVertex} attributes to be used to calculate the distance.
     *                                 All numerical attributes will be used if <tt>null</tt>. */
   public void addDistances(String   vertexLbl,
                            String   edgeLbl,
                            String   edgeAttributeName,
                            String[] differenceAttributeNames) {
     Set<CustomVertex> vs = _graph.vertexSet(); //.stream().limit(2000).collect(toCollection(LinkedHashSet::new));
     if (differenceAttributeNames == null) {
       log.info("Using all attributes");
       Set<String> allAttributeNames = new TreeSet<>();
       for (CustomVertex v : vs) {
         if (v.getLbl().equals(vertexLbl)) {
           for (Map.Entry<String, AttributeType> entry : v.attributesReg().entrySet()) {
             if (entry.getValue().equals(AttributeType.INT   ) ||
                 entry.getValue().equals(AttributeType.LONG  ) ||
                 entry.getValue().equals(AttributeType.FLOAT ) ||
                 entry.getValue().equals(AttributeType.DOUBLE) ) {
               allAttributeNames.add(entry.getKey());
               }
             }
           }
         }
       differenceAttributeNames = allAttributeNames.toArray(new String[0]);
       }
     log.info("Normalising");
     TreeMap<String, Double> norm = new TreeMap<>();
     double sum = 0;
     int n  = 0;
     for (String x : differenceAttributeNames) {
       for (CustomVertex v : vs) {
         if (v.getLbl().equals(vertexLbl)) {
           sum += Double.valueOf(v.getAttribute(x).getValue());
           n++;
           }
         }
       norm.put(x, sum / n);
       } 
     log.info("Adding distances between " + vertexLbl + ".[" + String.join(",", differenceAttributeNames) + "] recorded as " + edgeLbl + "." + edgeAttributeName);
     CustomEdge e;
     double diff;
     int n1 = 0;
     int n2 = 0;
     double max = Integer.MIN_VALUE;
     for (CustomVertex v1 : vs) {
       if (v1.getLbl().equals(vertexLbl)) {
         n1++;
         n2 = 0;
         for (CustomVertex v2 : vs) {
           if (v2.getLbl().equals(vertexLbl)) {
             n2++;
             if (n2 > n1) {
               diff = difference(v1, v2, differenceAttributeNames, norm);
               if (diff > max) {
                 max = diff;
                 }
               }
             }
           }
         }
       }
     log.info("difference < " + max);
     n1 = 0;
     n2 = 0;
     for (CustomVertex v1 : vs) {
       if (v1.getLbl().equals(vertexLbl)) {
         n1++;
         n2 = 0;
         for (CustomVertex v2 : vs) {
           if (v2.getLbl().equals(vertexLbl)) {
             n2++;
             if (n2 > n1) {
               diff = difference(v1, v2, differenceAttributeNames, norm);
               if (diff > max * 0.9) {
                 e = new CustomEdge();
                 e.putAttribute(edgeAttributeName, DefaultAttribute.createAttribute(diff   ));
                 e.putAttribute("labelE",          DefaultAttribute.createAttribute(edgeLbl));
                 _graph.addEdge(v1, v2, e);
                 _graph.setEdgeWeight(e, e.generateWeight());
                 }
               }
             }
           }
         }
       }
     }
     
   /** Give the difference between {@link CustomVertex}s.
     * @param v1                       The first {@link CustomVertex}.
     * @param v2                       The second {@link CustomVertex}.
     * @param differenceAttributeNames The names of {@link CustomVertex} attributes to be used to calculate the distance.
     * @param norm                     The normalisation factors for attributes.
     * @return                         The quadratic distance. */
   private double difference(CustomVertex            v1,
                             CustomVertex            v2,
                             String[]                differenceAttributeNames,
                             TreeMap<String, Double> norm) {
     double diff = 0;
     for (String x : differenceAttributeNames) {
       diff += Math.pow(Double.valueOf(v1.getAttribute(x).getValue()) / norm.get(x)- 
                        Double.valueOf(v2.getAttribute(x).getValue()) / norm.get(x), 2);
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
   
   private Params _params;
   
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
