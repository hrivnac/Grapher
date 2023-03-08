package com.Grapher.Convertors;

// JGraphT
import org.jgrapht.*;
import org.jgrapht.graph.*;
import org.jgrapht.graph.builder.*;
import org.jgrapht.nio.*;
import org.jgrapht.nio.graphml.*;
import org.jgrapht.nio.dot.*;
import org.jgrapht.nio.graph6.*;
import org.jgrapht.util.*;

// Java
import java.io.*;
import java.nio.charset.*;
import java.util.*;
import java.net.URI;

// Log4J
import org.apache.log4j.Logger;

/** <code>Convertor</code> converts between Graph formats.
  * Adapted  from
  * <a href="https://github.com/jgrapht/jgrapht/blob/master/jgrapht-io/src/test/java/org/jgrapht/nio/graphml/GraphMLImporterTest.java">GraphMLImporterTest</a>.
  * @opt attributes
  * @opt operations
  * @opt types
  * @opt visibility
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
    
  /** Execute the conversion. */
  public void convert() {
    log.info("Converting " + _infile + " to " + _outfile);
    Graph<CustomVertex, DefaultEdge> g = null;
    try {
      if (_infile.endsWith(".graphml")) {
        g = readGraphML(new FileInputStream(new File(_infile)),
                        DefaultEdge.class,
                        true,
                        true);
        }
      else {
        log.fatal("Unknown file type of " + _infile);
        return;
        }
      }
    catch (FileNotFoundException e) {
      log.fatal("Cannot find file " + _infile, e);
      return;
      }
    if (_outfile == null) {
      log.info(g);
      }
    else {
      if (_outfile.endsWith(".dot")) {
        writeDOT(g);
        }
      else {
        log.fatal("Unknwn file type of " + _outfile);
        return;
        }
      }
    }

  // Writers -------------------------------------------------------------------
  
  public void writeDOT(Graph graph) {
    DOTExporter<String, DefaultEdge> exporter = new DOTExporter<>();
    exporter.setVertexAttributeProvider((v) -> {
      Map<String, Attribute> map = new LinkedHashMap<>();
      map.put("label", DefaultAttribute.createAttribute(v.toString()));
      return map;
      });
    Writer writer = new StringWriter();
    exporter.exportGraph(graph, writer);
    System.out.println(writer.toString());
    }
         
  // Readers -------------------------------------------------------------------  
    
  public <E> Graph<CustomVertex, E> readGraphML(String   input,
                                                Class<E> edgeClass,
                                                boolean  directed,
                                                boolean  weighted) throws ImportException {
    return readGraphML(input,           
                       edgeClass,
                       directed,
                       weighted,
                       new HashMap<>(),
                       new HashMap<E, Map<String, Attribute>>());
      }

  public <E> Graph<CustomVertex, E> readGraphML(InputStream input,
                                                Class<E>    edgeClass,
                                                boolean     directed,
                                                boolean     weighted) throws ImportException {
    return readGraphML(input,
                       edgeClass,
                       directed,
                       weighted,
                       new HashMap<>(),
                       new HashMap<E, Map<String, Attribute>>());
      }

  public <E> Graph<CustomVertex, E> readGraphML(String                                    input,
                                                Class<E>                                  edgeClass,
                                                boolean                                   directed,
                                                boolean                                   weighted,
                                                Map<CustomVertex, Map<String, Attribute>> vertexAttributes,
                                                Map<E, Map<String, Attribute>>            edgeAttributes) throws ImportException {
    return readGraphML(new ByteArrayInputStream(input.getBytes()),
                       edgeClass,
                       directed,
                       weighted,
                       vertexAttributes,
                       edgeAttributes);
    }

  public <E> Graph<CustomVertex, E> readGraphML(InputStream                               input,
                                                Class<E>                                  edgeClass,
                                                boolean                                   directed,
                                                boolean                                   weighted,
                                                Map<CustomVertex, Map<String, Attribute>> vertexAttributes,
                                                Map<E, Map<String, Attribute>>            edgeAttributes) throws ImportException{
    Graph<CustomVertex, E> g;
    if (directed) {
      g = GraphTypeBuilder.directed()
                          .allowingMultipleEdges(true)
                          .allowingSelfLoops(true)
                          .weighted(weighted)
                          .vertexSupplier(new CustomVertexSupplier())
                          .vertexClass(CustomVertex.class)
                          .edgeClass(edgeClass)
                          .buildGraph();
        }
      else {
        g = GraphTypeBuilder.undirected()
                            .allowingMultipleEdges(true)
                            .allowingSelfLoops(true)
                            .weighted(weighted)
                            .vertexSupplier(new CustomVertexSupplier())
                            .vertexClass(CustomVertex.class)
                            .edgeClass(edgeClass)
                            .buildGraph();
        }
    GraphMLImporter<CustomVertex, E> importer = createGraphMLImporter(vertexAttributes, edgeAttributes);
    importer.importGraph(g, input);
    return g;
    }

  public <E> GraphMLImporter<CustomVertex, E> createGraphMLImporter(Map<CustomVertex, Map<String, Attribute>> vertexAttributes,
                                                                    Map<E,            Map<String, Attribute>> edgeAttributes) {
    GraphMLImporter<CustomVertex, E> importer = new GraphMLImporter<>();
    importer.addVertexAttributeConsumer((k, a) -> {
      CustomVertex vertex = k.getFirst();
      Map<String, Attribute> attrs = vertexAttributes.get(vertex);
      vertex.setAttributes(attrs);
      if (attrs == null) {
        attrs = new HashMap<>();
        vertexAttributes.put(vertex, attrs);
        }
      attrs.put(k.getSecond(), a);
      });
    importer.addEdgeAttributeConsumer((k, a) -> {
      E edge = k.getFirst();
      Map<String, Attribute> attrs = edgeAttributes.get(edge);
      if (attrs == null) {
        attrs = new HashMap<>();
        edgeAttributes.put(edge, attrs);
        }
      attrs.put(k.getSecond(), a);
      });
    return importer;    
    }    
    
  private String _infile;
  private String _outfile;
    
  /** Logging . */
  private static Logger log = Logger.getLogger(Convertor.class);

  }

 