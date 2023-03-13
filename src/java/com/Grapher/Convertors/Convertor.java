package com.Grapher.Convertors;

import com.Grapher.CustomGraph.CustomEdge;
import com.Grapher.CustomGraph.CustomVertex;
import com.Grapher.CustomGraph.CustomVertexSupplier;

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
    * @param infile  The input graph file.
    * @param outfile The output graph file.
    *                If <code>null</code>, output fill be written to <code>stdout</code>. */
  public Convertor(String infile,
                   String outfile) {
    _infile  = infile;
    _outfile = outfile;
    }
    
  /** Read {@link Graph} from input file.
    * @return The read {@link Graph}. */
  public Graph<CustomVertex, CustomEdge> read() {
    log.info("Reading " + _infile);
    try {
      if (_infile.endsWith(".graphml")) {
        return readGraphML(new FileInputStream(new File(_infile)),
                        true,   // directed
                        false,  // weighted
                        false,  // multipleEdges
                        false); // selfLoops
        }
      else {
        log.fatal("Unknown file type of " + _infile);
        return null; // TBD: make null graph
        }
      }
    catch (FileNotFoundException e) {
      log.fatal("Cannot find file " + _infile, e);
      return null; // TBD: kake null graph
      }
    }
    
  /** Execute the conversion. */
  public void convert() {
    log.info("Converting to " + _outfile);
    Graph<CustomVertex, CustomEdge> g = read();
    if (_outfile == null) {
      log.info(g);
      }
    else {
      if (_outfile.endsWith(".dot")) {
        String dot = writeDOT(g);
        try {
          FileWriter writer = new FileWriter(_outfile);
          writer.write(dot);
          writer.close();
          }
        catch (IOException e) {
          log.error("Cannot write to " + _outfile, e);
          }
        }
      else if (_outfile.endsWith(".g6")) {
        String g6 = null;
        try {
          g6 = writeGraph6(g);
          }
        catch (UnsupportedEncodingException e) {
          log.error("Cannot encode " + _outfile, e);
          return;
          }
        try {
          FileWriter writer = new FileWriter(_outfile);
          writer.write(g6);
          writer.close();
          }
        catch (IOException e) {
          log.error("Cannot write to " + _outfile, e);
          return;
          }
        }
      else if (_outfile.endsWith(".mat")) {
        String mat = writeMatrix(g);
        try {
          FileWriter writer = new FileWriter(_outfile);
          writer.write(mat);
          writer.close();
          }
        catch (IOException e) {
          log.error("Cannot write to " + _outfile, e);
          return;
          }
        }
      else {
        log.fatal("Unknwn file type of " + _outfile);
        return;
        }
      }
    }

  // Writers -------------------------------------------------------------------
  
  /** Represent {@link Graph}              doclet="org.jboss.apiviz.APIviz"
             docletpath="../extlib/apiviz-1.3.4.jar:../extlib/jdepend-2.9.5.jar:../extlib/tools-1.8.0.jar"
as a <em>DOT</em> string.
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
    return g;
    }

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
        log.error("Edge ingnored: "  + k + " -> " + a);
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
    
  private String _infile;
  private String _outfile;
    
  /** Logging . */
  private static Logger log = Logger.getLogger(Convertor.class);

  }

 