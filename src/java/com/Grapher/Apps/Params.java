package com.Grapher.Apps;

// Log4J
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/** Runtime parameters.
  * @author <a href="mailto:Julius.Hrivnac@cern.ch">J.Hrivnac</a> */
public class Params {
  
  /** Create. */
  public Params() {}
  
  /** Create and set parameters.
    * @param quiet     Whether running in a quiet mode.
    * @param show      Whether just show GUI.
    * @param noedge    Whether Edges should be ignored on import.
    * @param novertex  Whether edge-less Vertices should be ignored on export.
    * @param script    The script (py or groovy) to run.
    * @param infile    The input file name.
    * @param outfile   The output file name.
    * @param algorithm The algorithm(s). */
  public Params(boolean quiet,
                boolean show,
                boolean noedge,
                boolean novertex,
                String  script,
                String  infile,
                String  outfile,
                String  algorithm) {
    _quiet      = quiet;
    _show       = show;
    _noedge     = noedge;
    _novertex   = novertex;
    _script     = script;
    _infile     = infile;
    _outfile    = outfile;
    _algorithm  = algorithm;
    }    
  
  /** Tell whether running in a quiet mode.
    * @return Whether running in a quiet mode. */
  public boolean quiet() {
    return _quiet;
    }

  /** Tell whether just show GUI.
    * @return Whether just show GUI. */
  public boolean show() {
    return _show;
    }
  
   /** Tell whether Edges should be ignored on import.
    * @return Whether Edges should be ignored on import. */
  public boolean noedge() {
    return _noedge;
    }
    
   /** Tell whether edge-less Vertices should be ignored on export.
    * @return Whether edge-less Vertices should be ignored on export. */
  public boolean novertex() {
    return _novertex;
    }
   
  /** Give the script file name.
    * @return The script file name. */
  public String script() {
    return _script;
    }
   
  /** Give the input file name.
    * @return The input file name. */
  public String infile() {
    return _infile;
    }
    
  /** Give the output file name.
    * @return The output file name. */
  public String outfile() {
    return _outfile;
    }
    
  /** Give the algorithm(s).
    * @return The algorithm(s). */
  public String algorithm() {
    return _algorithm;
    }
    
  /** Set whether running in a quiet mode.
    * @param quiet Whether running in a quiet mode. */
  public void setQuiet(boolean quiet) {
    _quiet = quiet;
    }

  /** Set whether just show GUI.
    * @param show Whether just show GUI. */
  public void setShow(boolean show) {
    _show = show;
    }

  /** Set whether Edges should be ignored on import.
    * @param noedge Whether Edges should be ignored on import. */
  public void setNoedge(boolean noedge) {
    _noedge = noedge;
    }
    
  /** Set whether edge-less Vertices should be ignored on export.
    * @param noedge Whether edge-less Vertices should be ignored on export. */
  public void setNovertex(boolean novertex) {
    _novertex = novertex;
    }
   
  /** Set the script file name.
    * @param infile The script file name. */
  public void setScript(String script) {
    _script = script;
    }
   
  /** Set the input file name.
    * @param infile The input file name. */
  public void setInfile(String infile) {
    _infile = infile;
    }
    
  /** Set the output file name.
    * @param outfile The output file name. */
  public void setOutfile(String outfile) {
    _outfile = outfile;
    }
    
  /** Set the algorithm(s).
    * @param algorithm The algorithm(s). */
  public void setAlgorithm(String algorithm) {
    _algorithm = algorithm;
    }
                                   
  private boolean _quiet      = false;
  private boolean _show       = false;
  private boolean _noedge     = false;
  private boolean _novertex   = false;
  private String  _script;
  private String  _infile;
  private String  _outfile;
  private String  _algorithm;

  /** Logging . */
  private static Logger log = LogManager.getLogger(Params.class);
   
 
  }
