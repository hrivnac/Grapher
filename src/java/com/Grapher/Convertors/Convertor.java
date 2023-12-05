package com.Grapher.Convertors;

import com.Grapher.CustomGraph.CustomEdge;
import com.Grapher.CustomGraph.CustomVertex;
import com.Grapher.CustomGraph.CustomVertexSupplier;
import com.Grapher.Apps.Params;

// JGraphT
import org.jgrapht.Graph;
import org.jgrapht.graph.AsUndirectedGraph;
import org.jgrapht.graph.builder.GraphTypeBuilder;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.GraphExporter;
import org.jgrapht.nio.ImportException;
import org.jgrapht.nio.graphml.GraphMLImporter;
import org.jgrapht.nio.graphml.GraphMLExporter;
import org.jgrapht.nio.dot.DOTExporter;
import org.jgrapht.nio.json.JSONExporter;
import org.jgrapht.nio.csv.CSVExporter;
import org.jgrapht.nio.csv.CSVFormat;
import org.jgrapht.nio.graph6.Graph6Sparse6Exporter;
import org.jgrapht.nio.matrix.MatrixExporter;

// Java
import java.io.Writer;
import java.io.StringWriter;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.util.Map;
import java.util.HashSet;
import java.util.HashMap;
import java.util.LinkedHashMap;

// Log4J
import org.apache.log4j.Logger;

/** <code>Convertor</code> converts between Graph formats.
  * @author <a href="mailto:Julius.Hrivnac@cern.ch">J.Hrivnac</a> */
public class Convertor {
  
  /** Convert graph file into new format.
    * Formats are deducted from the files extentions.
    * @param params The calling {@link Params}. */
  public Convertor(Params params) {
    _params = params;
    }
    
  /** Read {@link Graph} from input file.
    * @return The read {@link Graph}. */
  public Graph<CustomVertex, CustomEdge> read() {
    Graph<CustomVertex, CustomEdge> graph = null;
    String infile = _params.infile();
    log.info("Reading " + infile);
    try {
      if (infile.endsWith(".graphml")) {
        graph = readGraphML(new FileInputStream(new File(infile)),
                            true,   // directed
                            true,   // weighted
                            false,  // multipleEdges
                            false); // selfLoops
        }
      else {
        log.fatal("Unknown file type of " + infile);
        return graph;
        }
      }
    catch (FileNotFoundException e) {
      log.fatal("Cannot find file " + infile, e);
      return graph;
      }
    if (_params.noedge()) {
      log.info("Removing Edges");
      for (CustomEdge e : new HashSet<>(graph.edgeSet())) {
        graph.removeEdge(e);
        }
      }
    return graph;
    }
    
  /** Read and execute the conversion. */
  public void convert() {
    convert(read());
    }
    
  /** Execute the conversion.
    * @param g The {@link Graph} to convert. */
  public void convert(Graph<CustomVertex, CustomEdge> g) {
    String outfile = _params.outfile();
    if (outfile != null) {
      log.info("Writing graph: " + g.getType() + "[" + g.vertexSet().size() + ", " + g.edgeSet().size() + "] to " + outfile);
      if (outfile.endsWith(".dot")) {
        String dot = writeDOT(g);
        try {
          FileWriter writer = new FileWriter(outfile);
          writer.write(dot);
          writer.close();
          }
        catch (IOException e) {
          log.error("Cannot write to " + outfile, e);
          }
        }
      else if (outfile.endsWith(".json")) {
        String json = writeJSON(g);
        try {
          FileWriter writer = new FileWriter(outfile);
          writer.write(json);
          writer.close();
          }
        catch (IOException e) {
          log.error("Cannot write to " + outfile, e);
          }
        }
      else if (outfile.endsWith(".csv")) {
        String csv = writeCSV(g);
        try {
          FileWriter writer = new FileWriter(outfile);
          writer.write(csv);
          writer.close();
          }
        catch (IOException e) {
          log.error("Cannot write to " + outfile, e);
          }
        }
      else if (outfile.endsWith(".g6")) {
        String g6 = null;
        try {
          g6 = writeGraph6(g);
          }
        catch (UnsupportedEncodingException e) {
          log.error("Cannot encode " + outfile, e);
          return;
          }
        try {
          FileWriter writer = new FileWriter(outfile);
          writer.write(g6);
          writer.close();
          }
        catch (IOException e) {
          log.error("Cannot write to " + outfile, e);
          return;
          }
        }
      else if (outfile.endsWith(".mat")) {
        String mat = writeMatrix(g);
        try {
          FileWriter writer = new FileWriter(outfile);
          writer.write(mat);
          writer.close();
          }
        catch (IOException e) {
          log.error("Cannot write to " + outfile, e);
          return;
          }
        }
      else if (outfile.endsWith(".graphml")) {
        String graphml = writeGraphML(g);
        try {
          FileWriter writer = new FileWriter(outfile);
          writer.write(graphml);
          writer.close();
          }
        catch (IOException e) {
          log.error("Cannot write to " + outfile, e);
          return;
          }
        }
      else {
        log.info("Unknwn file type of " + outfile);
        log.info(g);
        return;
        }
      }
    }

  // Writers -------------------------------------------------------------------
  
  /** Represent {@link Graph} as a <em>DOT</em> string.
    * Reading in <em>GraphViz</em>:
    * <pre>
    * dot -T jpg mygraph.dot &gt; mygraph.jpg
    * </pre>
    * @param graph The {@link Graph} to be written out.
    * @return      The <em>DOT</em> representation of the {@link Graph}. */
  public String writeDOT(Graph<CustomVertex, CustomEdge> graph) {
    DOTExporter<CustomVertex, CustomEdge> exporter = new DOTExporter<>();
    exporter.setVertexAttributeProvider((v) -> {
      Map<String, Attribute> map = new LinkedHashMap<>();
      map.put("label", DefaultAttribute.createAttribute(v.getName()));
      return map;
      });
    exporter.setEdgeAttributeProvider((e) -> {
      Map<String, Attribute> map = new LinkedHashMap<>();
      map.put("label", DefaultAttribute.createAttribute(e.getName()));
      return map;
      });
    Writer writer = new StringWriter();
    exporter.exportGraph(graph, writer);
    return writer.toString();
    }
    
  /** Represent {@link Graph} as a <em>CSV</em> string.
    * @param graph The {@link Graph} to be written out.
    * @return      The <em>CSV</em> representation of the {@link Graph}. */
  public String writeCSV(Graph<CustomVertex, CustomEdge> graph) {
    CSVExporter<CustomVertex, CustomEdge> exporter = new CSVExporter<>(CSVFormat.ADJACENCY_LIST);
    exporter.setParameter(CSVFormat.Parameter.EDGE_WEIGHTS, true);
    exporter.setVertexAttributeProvider((v) -> {
      Map<String, Attribute> map = new LinkedHashMap<>();
      map.put("label", DefaultAttribute.createAttribute(v.getName()));
      return map;
      });
    exporter.setEdgeAttributeProvider((e) -> {
      Map<String, Attribute> map = new LinkedHashMap<>();
      map.put("label", DefaultAttribute.createAttribute(e.getName()));
      return map;
      });
    Writer writer = new StringWriter();
    exporter.exportGraph(graph, writer);
    return writer.toString();
    }
    
  /** Represent {@link Graph} as a <em>JSON</em> string.
    * @param graph The {@link Graph} to be written out.
    * @return      The <em>JSON</em> representation of the {@link Graph}. */
  public String writeJSON(Graph<CustomVertex, CustomEdge> graph) {
    JSONExporter<CustomVertex, CustomEdge> exporter = new JSONExporter<>();
    exporter.setVertexAttributeProvider((v) -> {
      Map<String, Attribute> map = new LinkedHashMap<>();
      map.put("label", DefaultAttribute.createAttribute(v.getName()));
      return map;
      });
    exporter.setEdgeAttributeProvider((e) -> {
      Map<String, Attribute> map = new LinkedHashMap<>();
      map.put("label", DefaultAttribute.createAttribute(e.getName()));
      return map;
      });
    Writer writer = new StringWriter();
    exporter.exportGraph(graph, writer);
    return writer.toString();
    }
    
  /** Represent {@link Graph} as a <em>Graph6</em> string.
    * Reading in <em>Sage</em>:
    * <pre>
    * with open("mygraph.g6", "r") as f:
    * g6 = f.read()
    * from sage.graphs.graph_input import from_graph6  
    * g = Graph() 
    * from_graph6(g, g6) 
    * g.plot()
    * </pre>
    * @param graph The {@link Graph} to be written out.
    * @return      The <em>Graph6</em> representation of the {@link Graph}.
    * @throws UnsupportedEncodingException If unsupportec encoding used. */
  public String writeGraph6(Graph<CustomVertex, CustomEdge> graph) throws UnsupportedEncodingException {
    Graph6Sparse6Exporter<CustomVertex, CustomEdge> exporter = new Graph6Sparse6Exporter<>(Graph6Sparse6Exporter.Format.GRAPH6);
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    exporter.exportGraph(new AsUndirectedGraph<>(graph), os);
    return new String(os.toByteArray(), "UTF-8");
    }  
    
  /** Represent {@link Graph} as a <em>matrix</em> string.
    * @param graph The {@link Graph} to be written out.
    * @return      The <em>matrix</em> representation of the {@link Graph}. */
  public String writeMatrix(Graph<CustomVertex, CustomEdge> graph) {
    GraphExporter<CustomVertex, CustomEdge> exporter = new MatrixExporter<>();
    Writer writer = new StringWriter();
    exporter.exportGraph(graph, writer);
    return writer.toString();
    }  
    
  /** Represent {@link Graph} as a <em>GramphML</em> string.
    * @param graph The {@link Graph} to be written out.
    * @return      The <em>GraphML</em> representation of the {@link Graph}. */
  // TBD
  public String writeGraphML(Graph<CustomVertex, CustomEdge> graph) {
    GraphExporter<CustomVertex, CustomEdge> exporter = new GraphMLExporter<>();
    Writer writer = new StringWriter();
    exporter.exportGraph(graph, writer);
    return writer.toString();
    }  
         
  // Readers -------------------------------------------------------------------  
    
  /** Create {@link Graph} from <em>GraphML</em> {@link InputStream}.
    * @param input         The {@link InputStream} to read from.
    * @param directed      Whether {@link Graph} is directed.
    * @param weighted      Whether {@link Graph} is weighted.
    * @param multipleEdges Whether {@link Graph} has multi-edges.
    * @param selfLoops     Whether {@link Graph} has self-loops.
    * @return              The created {@link Graph}. */
  public Graph<CustomVertex, CustomEdge> readGraphML(InputStream input,
                                                     boolean     directed,
                                                     boolean     weighted,
                                                     boolean     multipleEdges,
                                                     boolean     selfLoops) {
    Graph<CustomVertex, CustomEdge> graph = readGraphML(input,
                                            directed,
                                            weighted,
                                            multipleEdges,
                                            selfLoops,
                                            new HashMap<CustomVertex, Map<String, Attribute>>(),
                                            new HashMap<CustomEdge,   Map<String, Attribute>>());
      if (weighted) {
        log.info("Generating weights ...");
        for (CustomEdge e : graph.edgeSet()) {        
          graph.setEdgeWeight(e, e.generateWeight());
          }
        }
     return graph;
     }

  /** Create {@link Graph} from <em>GraphML</em> {@link InputStream}.
    * @param input            The {@link InputStream} to read from.
    * @param directed         Whether {@link Graph} is directed.
    * @param weighted         Whether {@link Graph} is weighted.
    * @param multipleEdges    Whether {@link Graph} has multi-edges.
    * @param selfLoops        Whether {@link Graph} has self-loops.
    * @param vertexAttributes The {@link Map} of {@link CustomVertex} attributes to be filled.
    * @param edgeAttributes   The {@link Map} of {@link CustomEdge} attributes to be filled.
    * @return                 The created {@link Graph}. */
  public Graph<CustomVertex, CustomEdge> readGraphML(InputStream                               input,
                                                     boolean                                   directed,
                                                     boolean                                   weighted,
                                                     boolean                                   multipleEdges,
                                                     boolean                                   selfLoops,
                                                     Map<CustomVertex, Map<String, Attribute>> vertexAttributes,
                                                     Map<CustomEdge,   Map<String, Attribute>> edgeAttributes) throws ImportException{
    Graph<CustomVertex, CustomEdge> g;
    if (directed) {
      g = GraphTypeBuilder.directed()
                          .allowingMultipleEdges(multipleEdges)
                          .allowingSelfLoops(selfLoops)
                          .weighted(weighted)
                          .vertexSupplier(new CustomVertexSupplier())
                          .vertexClass(CustomVertex.class)
                          .edgeClass(CustomEdge.class)
                          .buildGraph();
        }
    else {
      g = GraphTypeBuilder.undirected()
                          .allowingMultipleEdges(multipleEdges)
                          .allowingSelfLoops(selfLoops)
                          .weighted(weighted)
                          .vertexSupplier(new CustomVertexSupplier())
                          .vertexClass(CustomVertex.class)
                          .edgeClass(CustomEdge.class)
                          .buildGraph();
      }
    GraphMLImporter<CustomVertex, CustomEdge> importer = createGraphMLImporter(vertexAttributes, edgeAttributes);
    importer.setEdgeWeightAttributeName("weight");        
    importer.importGraph(g, input);
    log.info("Imported graph: " + g.getType() + "[" + g.vertexSet().size() + ", " + g.edgeSet().size() + "] from " + _params.infile());
    return g;
    }

  /** Create {@link GraphMLImporter}.
    * @param vertexAttributes The {@link CustomVertex} attributes to fill.
    * @param edgeAttributes   The {@link CustomEdge} attributes to fill.
    * @return                 The created {@link GraphMLImporter}. */
  public GraphMLImporter<CustomVertex, CustomEdge> createGraphMLImporter(Map<CustomVertex, Map<String, Attribute>> vertexAttributes,
                                                                         Map<CustomEdge,   Map<String, Attribute>> edgeAttributes) {
    GraphMLImporter<CustomVertex, CustomEdge> importer = new GraphMLImporter<>();
    importer.addVertexAttributeConsumer((k, a) -> {
      CustomVertex vertex = k.getFirst();
      Map<String, Attribute> attrs = vertexAttributes.get(vertex);
      if (attrs == null) {
        attrs = new HashMap<>();
        vertexAttributes.put(vertex, attrs);
        }
      attrs.put(k.getSecond(), a);
      vertex.putAttribute(k.getSecond(), a);
      });
    importer.addEdgeAttributeConsumer((k, a) -> {
      CustomEdge edge = k.getFirst();
      if (edge == null) {
        log.debug("Edge ignored: "  + k + " -> " + a);
        }
      else {
        Map<String, Attribute> attrs = edgeAttributes.get(edge);
        if (attrs == null) {
          attrs = new HashMap<>();
          edgeAttributes.put(edge, attrs);
          }
        attrs.put(k.getSecond(), a);
        edge.putAttribute(k.getSecond(), a);
        }
      });
    return importer;    
    }    
  
  private Params _params;
    
  /** Logging . */
  private static Logger log = Logger.getLogger(Convertor.class);

  }

 