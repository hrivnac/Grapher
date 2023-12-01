package com.Grapher.Apps;

import com.Grapher.Convertors.Convertor;
import com.Grapher.GUI.Shower;
import com.Grapher.Analysis.Analyser;
import com.Grapher.Utils.Init;

// CLI
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;

// Log4J
import org.apache.log4j.Logger;

/** Simple Command Line.
  * @author <a href="mailto:Julius.Hrivnac@cern.ch">J.Hrivnac</a> */
public class CLI extends Params {

  /** Create and execute following command line arguments. */
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
    
  /** Parse the cli arguments.
    * @param args The cli arguments. */
  public void parseArgs(String[] args) {
    CommandLineParser parser = new BasicParser();
    Options options = new Options();
    options.addOption("h", "help",     false, "show help");
    options.addOption("q", "quiet",    false, "minimal direct feedback");
    options.addOption("s", "show",     false, "show in graphical window (instead of converting)");
    options.addOption("e", "noedge",   false, "ignore edges");
    options.addOption(OptionBuilder.withLongOpt("in")
                                   .withDescription("input file name [.graphml]")
                                   .hasArg()
                                   .withArgName("infile")
                                   .create("i"));
    options.addOption(OptionBuilder.withLongOpt("out")
                                   .withDescription("output file name [.dot|.mat|.g6]")
                                   .hasArg()
                                   .withArgName("outfile")
                                   .create("o"));
    options.addOption(OptionBuilder.withLongOpt("alg")
                                   .withDescription("apply algorithm (instead of justconverting)\n[sc = Strong Connectivity | cl = Clustering | ad = adding distances]]\nseveral algorithms can be separated by :\nalgorithm arguments can be supplied after /")
                                   .hasArg()
                                   .withArgName("algoritm")
                                   .create("a"));
    try {
      CommandLine cline = parser.parse(options, args );
      if (cline.hasOption("quiet")) {
        setQuiet(true);
        }
      if (cline.hasOption("noedge")) {
        setNoedge(true);
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
  private static Logger log = Logger.getLogger(CLI.class);
   
 
  }
