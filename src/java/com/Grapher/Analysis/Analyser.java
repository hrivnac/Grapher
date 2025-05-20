package com.Grapher.Analysis;

import com.Grapher.CustomGraph.CustomEdge;
import com.Grapher.CustomGraph.CustomVertex;
import com.Grapher.Utils.Params;
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
import java.util.LinkedHashMap;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toCollection;

// Log4J
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

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
            addDistances("alert", "distance", "difference", null, 0, 1);
            break;
          case "radec":
            addDistances("alert", "distance", "difference", new String[]{"ra", "dec"}, 0, 1);
            break;
          case "isolation":
            addDistances("alert", "distance", "difference", ALERTs, new Double(algpar[2]), new Double(algpar[3]));
            break;
          case "isolationRD":
            addDistances("alert", "distance", "difference", RADECs, new Double(algpar[2]), new Double(algpar[3]));
            break;
          case "pca":
            addDistances("PCA", "distance", "difference", PCAs, 0, 1);
            break;
          case "immersion":
            addImmersion("alert", "connectivity", ALERTs, new Integer(algpar[2]));
            break;
          case "immersionRD":
            addImmersion("alert", "connectivity", RADECs, new Integer(algpar[2]));
            break;
          default:
            addDistances(algpar[1], algpar[2], algpar[3], new String[]{algpar[4]}, 0, 1);
            }
          break;
        case "sc":
          applyStrongConnectivity();
          break;
        case "cl":
          applyClustering(algpar[1], new Integer(algpar[2]));
          break;
        case "co":
          applyConnectivity(new Integer(algpar[1]), new Boolean(algpar[2]));
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
    
  /** Apply <em>Connectivity</em> algorithm.
    * @param n        The number of most and least connected vertexes to show.
    * @param weighted Whether to take edge weights into account
    *                 (or just count each edge as<tt>1</tt>). */
  public void applyConnectivity(int     n,
                                boolean weighted) {            
    log.info("Applying Connectivity Algorithm ...");
    ConnectivityAlgorithm coAlg = new ConnectivityAlgorithm(_graph, weighted);
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
     *                                 All numerical attributes will be used if <tt>null</tt>.
     * @param kn                       The number of most and least immersed {@link CustomVertex}s to report. */
   public void addImmersion(String   vertexLbl,
                            String   immersionAttributeName,
                            String[] differenceAttributeNames,
                            int      kn) {
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
     Map<CustomVertex, Double> connectivity = new LinkedHashMap<>();
     for (CustomVertex v1 : vs) {
       if (v1.getLbl().equals(vertexLbl)) {
         sum = 0;
         for (CustomVertex v2 : vs) {
           if (v2.getLbl().equals(vertexLbl)) {
             sum += difference(v1, v2, differenceAttributeNames, norm, DifferenceAlg.SQR);
             }
           }
         v1.putAttribute(immersionAttributeName, DefaultAttribute.createAttribute(sum));
         connectivity.put(v1, sum);
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
     Map<CustomVertex, Double> sortedConnectivity = connectivity.
                                                    entrySet().
                                                    stream().
                                                    sorted(Map.Entry.comparingByValue()).
                                                    collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));  
     CustomVertex cv;
     int k  = 0;
     log.info("Most connected: " + vertexLbl + "s");
     for (Map.Entry<CustomVertex, Double> entry : sortedConnectivity.entrySet()) {
       if (k++ < kn) {
         cv = entry.getKey();
         log.info("\t" + k + "\t:" + cv + "(connectivity=" + entry.getValue() + ")");
         }
       }     
     k = 0;
     log.info("Most isolated: " + vertexLbl + "s");
     for (Map.Entry<CustomVertex, Double> entry : sortedConnectivity.entrySet()) {
       if ((connectivity.size() - k++) <= kn) {
         cv = entry.getKey();
         log.info("\t" + k + "\t:" + cv + "(connectivity=" + entry.getValue() + ")");
         }
       }      
     }
    
   /** Add distances between {@link CustomVertex}s.
     * @param vertexLbl                The label of the {@link CustomVertex}s to connect with distances.
     * @param edgeLbl                  The label of the new {@link CustomEdge}.
     * @param edgeAttributeName        The name of the new {@link CustomEdge} attribute carrying the distance value.
     * @param differenceAttributeNames The names of {@link CustomVertex} attributes to be used to calculate the distance.
     *                                 All numerical attributes will be used if <tt>null</tt>. 
     * @param minDifference            The minimal difference for the edge to be recorded (as a part of minimal existing difference).
     *                                 <code>0</code> means no down limit. 
     * @param manDifference            The maximal difference for the edge to be recorded (as a part of maximal existing difference).
     *                                 <code>1</code> means no upper limit. */
   public void addDistances(String   vertexLbl,
                            String   edgeLbl,
                            String   edgeAttributeName,
                            String[] differenceAttributeNames,
                            double   minDifference,
                            double   maxDifference) {
     Set<CustomVertex> vs = _graph.vertexSet().stream().collect(toCollection(LinkedHashSet::new)); //.stream().limit(2000).collect(toCollection(LinkedHashSet::new));
     //Set<CustomVertex> vs = _graph.vertexSet().stream().limit(200).collect(toCollection(LinkedHashSet::new)); //.stream().limit(2000).collect(toCollection(LinkedHashSet::new));
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
     double min = Integer.MAX_VALUE;
     double max = Integer.MIN_VALUE;
     for (CustomVertex v1 : vs) {
         n1++;
       if (v1.getLbl().equals(vertexLbl)) {
         n2 = 0;
         for (CustomVertex v2 : vs) {
             n2++;
           if (v2.getLbl().equals(vertexLbl)) {
             if (n2 > n1) {
               diff = difference(v1, v2, differenceAttributeNames, norm, DifferenceAlg.SQR);
               if (diff > max) {
                 max = diff;
                 }
               if (diff == 0) {
                 log.info(" " + n1 + " " + n2 + " " + v1 + " " + v2);
                 }
               if (diff != 0 && diff < min) {
                 min = diff;
                 }
               }
             }
           }
         }
       }
     log.info("difference in (" + min + ", " + max + ")");
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
               diff = difference(v1, v2, differenceAttributeNames, norm, DifferenceAlg.SQR);
               if (diff > min * minDifference && diff < max * maxDifference) {
                 e = new CustomEdge();
                 e.putAttribute(edgeAttributeName, DefaultAttribute.createAttribute(1 / diff));
                 e.putAttribute("labelE",          DefaultAttribute.createAttribute(edgeLbl ));
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
     * @param norm                     The normalisation factors for attributes. Ignored if <tt>null</tt>.
     * @param alg                      The DifferenceAlg to use to calculate the distance.
     * @return                         The distance. */
   private double difference(CustomVertex            v1,
                             CustomVertex            v2,
                             String[]                differenceAttributeNames,
                             TreeMap<String, Double> norm,
                             DifferenceAlg           alg) {
     double diff = 0;
     double val1;
     double val2;
     for (String x : differenceAttributeNames) {
       val1 = Double.valueOf(v1.getAttribute(x).getValue());
       val2 = Double.valueOf(v2.getAttribute(x).getValue());
       if (norm !=  null) {
         val1 = val1 / norm.get(x);
         val2 = val2 / norm.get(x);
         }
       switch(alg) {
         case LIN:
           diff += Math.abs(val1 - val2);
           break;
         case SQR:
           diff += Math.pow(val1 - val2, 2);
           break;
         case LOG:
           diff += Math.log1p(Math.abs(val1 - val2));
           break;
         }
       }
     switch(alg) {
       case SQR: 
         return Math.sqrt(diff);  
       default:
         return diff;
       }       
     }
     
// =============================================================================     
    
  /** Give current {@link Graph}.
    * @return The current {@link Graph}. May be result of an algoritm processing. */
  public Graph<CustomVertex, CustomEdge> graph() {    
    return _graph;
    }
    
   public enum DifferenceAlg {LIN, SQR, LOG}; 
    
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
                                               
  private static String[] ALERTs = new String[]{"magnr" ,
                                                "magpsf",
                                                "neargaia",
                                                "rf_kn_vs_nonkn",
                                                "rf_snia_vs_nonia",
                                                "sgscore1",
                                                "sigmagnr",
                                                "sigmapsf",
                                                "snn_sn_vs_all",
                                                "snn_snia_vs_nonia"}; 
                                                
  private static String[] RADECs = new String[]{"ra",
                                                "dec"};
    
  /** Logging . */
  private static Logger log = LogManager.getLogger(Analyser.class);
  
  }
