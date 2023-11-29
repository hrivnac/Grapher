package com.Grapher.Convertors;

import com.Grapher.CustomGraph.CustomEdge;
import com.Grapher.CustomGraph.CustomVertex;
import com.Grapher.CustomGraph.CustomVertexSupplier;
import com.Grapher.Apps.CLI;

// JGraphT
import org.jgrapht.Graph;
import org.jgrapht.graph.AsUndirectedGraph;
import org.jgrapht.graph.builder.GraphTypeBuilder;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.GraphExporter;
import org.jgrapht.nio.ImportException;
import org.jgrapht.nio.graphml.GraphMLImporter;
import org.jgrapht.nio.dot.DOTExporter;
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
import java.util.HashMap;
import java.util.LinkedHashMap;

// Log4J
import org.apache.log4j.Logger;

/** <code>Convertor</code> converts between Graph formats.
  * @author <a href="mailto:Julius.Hrivnac@cern.ch">J.Hrivnac</a> */
public class Convertor {
  
  /** Convert graph file into new format.
    * Formats are deducted from the files extentions.
    * @param cli The calling {@link CLI}. */
  public Convertor(CLI cli) {
    _cli = cli;
    }
    
  /** Read {@link Graph} from input file.
    * @return The read {@link Graph}. */
  public Graph<CustomVertex, CustomEdge> read() {
    String infile = _cli.infile();
    log.info("Reading " + infile);
    try {
      if (infile.endsWith(".graphml")) {
        return readGraphML(new FileInputStream(new File(infile)),
                           true,   // directed
                           false,  // weighted
                           false,  // multipleEdges
                           false); // selfLoops
        }
      else {
        log.fatal("Unknown file type of " + infile);
        return null; // TBD: make null graph
        }
      }
    catch (FileNotFoundException e) {
      log.fatal("Cannot find file " + infile, e);
      return null; // TBD: make null graph
      }
    }
    
  /** Execute the conversion. */
  public void convert() {
    String outfile = _cli.outfile();
    log.info("Converting to " + outfile);
    Graph<CustomVertex, CustomEdge> g = read();
    if (outfile == null) {
      log.info(g);
      }
    else {
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
      else {
        log.fatal("Unknwn file type of " + outfile);
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
    * @return      The <em>Graph6</em> representation of the {@link Graph}. */
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
         
  // Readers -------------------------------------------------------------------  
    
  /** Create {@link Graph} from <em>GraphML</em> {@link ImputStream}.
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
    return readGraphML(input,
                       directed,
                       weighted,
                       multipleEdges,
                       selfLoops,
                       new HashMap<CustomVertex, Map<String, Attribute>>(),
                       new HashMap<CustomEdge,   Map<String, Attribute>>());
      }

  /** Create {@link Graph} from <em>GraphML</em> {@link ImputStream}.
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
    //importer.setEdgeWeightAttributeName("myvalue");        
    importer.importGraph(g, input);
    log.info("Imported graph: " + g.getType() + "[" + g.vertexSet().size() + ", " + g.edgeSet().size() + "]");
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
    if (!_cli.noedge()) {
      importer.addEdgeAttributeConsumer((k, a) -> {
        CustomEdge edge = k.getFirst();
        if (edge == null) {
          log.error("Edge ignored: "  + k + " -> " + a);
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
      }
    return importer;    
    }    
  
  private CLI _cli;
    
  /** Logging . */
  private static Logger log = Logger.getLogger(Convertor.class);

  }

 