package com.Grapher.Apps;

import com.Grapher.Convertors.Convertor;
import com.Grapher.GUI.Shower;
import com.Grapher.Analysis.Analyser;
import com.Grapher.Utils.Init;

// Lomikel
import com.Lomikel.Utils.StringFile;
import com.Lomikel.Utils.LomikelException;

// Groovy
import groovy.lang.GroovyShell;
import groovy.lang.Binding;

// Jython
import org.python.util.PythonInterpreter; 
import org.python.core.*; 

// CLI
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;

// Java
import java.io.File;
import java.io.IOException;

// Log4J
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/** Simple Command Line.
  * @author <a href="mailto:Julius.Hrivnac@cern.ch">J.Hrivnac</a> */
public class CLI extends Params {

  /** Create and execute following command line arguments.
    * @param args  The cli arguments. */
  public static void main(String[] args) {
    CLI cli = new CLI();
    cli.parseArgs(args);
    Init.init(cli.quiet());
    cli.execute();
    }
  
  /** Create. */
  public CLI() {
    super();
    }

  /** Execute command line in required way. */
  public void execute() {
    if (script() !=  null) {
      String scriptName = script();
      String script;
      String[] parts = scriptName.split("\\.");
      String ext = parts[parts.length - 1];
      switch (ext) {
        case "groovy":
          log.info("Executing Groovy " + scriptName + " ...");
          Binding sharedData = new Binding();
          sharedData.setVariable("cli", this);
          GroovyShell shell = new GroovyShell(sharedData);
          try {
            Object result = shell.run(new File(scriptName), new String[]{});
            if (result != null) {
              log.info(result);
              }
            }
          catch (IOException e) {
            log.fatal("Cannot execute " + scriptName, e);
            return;
            }
          break;
        case "py":
          log.info("Executing Python " + scriptName + " ...");
          PythonInterpreter interpreter = new PythonInterpreter();
          interpreter.set("cli", this);
          interpreter.execfile(scriptName);
          break;
        default:
          log.fatal("Unknown script " + scriptName);
          break;
        }
      }
    else {
      Convertor convertor = new Convertor(this);
      if (show()) {
        new Shower().show(convertor.read());
        }
      else {
        if (algorithm() == null) {
          convertor.convert();
          }
        else {
          Analyser analyser = new Analyser(this);
          analyser.fill(convertor.read());
          analyser.apply();
          convertor.convert(analyser.graph());
          }
        }
      }
    }
    
  /** Parse the cli arguments.
    * @param args The cli arguments. */
  public void parseArgs(String[] args) {
    CommandLineParser parser = new BasicParser();
    Options options = new Options();
    options.addOption("h", "help",     false, "show help");
    options.addOption("q", "quiet",    false, "minimal direct feedback");
    options.addOption("w", "show",     false, "show in graphical window (instead of converting)");
    options.addOption("e", "noedge",   false, "ignore input edges");
    options.addOption("v", "novertex", false, "ignore output edge-less vertices");
    options.addOption(OptionBuilder.withLongOpt("script")
                                   .withDescription("script to run (ignores all other options) [.groovy|.py]")
                                   .hasArg()
                                   .withArgName("src")
                                   .create("s"));
    options.addOption(OptionBuilder.withLongOpt("in")
                                   .withDescription("input file name [.graphml]")
                                   .hasArg()
                                   .withArgName("infile")
                                   .create("i"));
    options.addOption(OptionBuilder.withLongOpt("out")
                                   .withDescription("output file name [.dot|.mat|.g6|csv|json|graphml]")
                                   .hasArg()
                                   .withArgName("outfile")
                                   .create("o"));
    options.addOption(OptionBuilder.withLongOpt("alg")
                                   .withDescription("apply algorithm (instead of just converting)\n[sc = Strong Connectivity | cl = Clustering | ad = adding distances]]\nseveral algorithms can be separated by ;\nalgorithm arguments can be supplied after ,")
                                   .hasArg()
                                   .withArgName("algoritm")
                                   .create("a"));
    try {
      CommandLine cline = parser.parse(options, args);
      if (cline.hasOption("script")) {
        setScript(cline.getOptionValue("script"));
        }
      else {
        if (cline.hasOption("quiet")) {
          setQuiet(true);
          }
        if (cline.hasOption("noedge")) {
          setNoedge(true);
          }
        if (cline.hasOption("novertex")) {
          setNovertex(true);
          }
        if (cline.hasOption("help")) {
          new HelpFormatter().printHelp("java -jar Grapher.exe.jar", options);
          System.exit(0);
          }
        if (cline.hasOption("in")) {
          setInfile(cline.getOptionValue("in"));
          }
        else {
          log.error("No input file specified"); 
          new HelpFormatter().printHelp("java -jar Grapher.exe.jar", options);
          System.exit(0);
          }
        if (cline.hasOption("out")) {
          setOutfile(cline.getOptionValue("out"));
          }
        if (cline.hasOption("alg")) {
          setAlgorithm(cline.getOptionValue("alg"));
          }
        if (cline.hasOption("show")) {
          if (cline.hasOption("alg")) {
            log.error("show and alg options are incompatible"); 
            new HelpFormatter().printHelp("java -jar Grapher.exe.jar", options);
            System.exit(0);
            }
          setShow(true);
          }
        }
      }
    catch (ParseException e) { 
      new HelpFormatter().printHelp("java -jar Grapher.exe.jar", options);
      System.exit(0);
      }
    }
   
   /** Give the help.
    * @return The help. */
  public static String help() {
    return _help;
   }
                                    
  private static String  _help = "";                                    

  /** Logging . */
  private static Logger log = LogManager.getLogger(CLI.class);
   
 
  }
