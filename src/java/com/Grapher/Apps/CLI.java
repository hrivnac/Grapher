package com.Grapher.Apps;

import com.Grapher.Convertors.Convertor;
import com.Grapher.Utils.Init;

// CLI
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;

// Java
import java.io.PrintWriter;
import java.io.StringWriter;

// Log4J
import org.apache.log4j.Logger;

/** Simple Command Line.
  * @opt attributes
  * @opt operations
  * @opt types
  * @opt visibility
  * @author <a href="mailto:Julius.Hrivnac@cern.ch">J.Hrivnac</a> */
public class CLI {

  public static void main(String[] args) {
    CLI cli = new CLI();
    cli.parseArgs(args);
    Init.init(cli.quiet());
    cli.execute();
    }
  
  /** Create. */
  public CLI() {}

  /** Execute command line in required way. */
  public void execute() {
    Convertor convertor = new Convertor(_infile, _outfile);
    convertor.convert();
    }
    
  /** Parse the cli arguments.
    * @param args    The cli arguments. */
  public void parseArgs(String[] args) {
    CommandLineParser parser = new BasicParser();
    Options options = new Options();
    options.addOption("h", "help",     false, "show help");
    options.addOption("q", "quiet",    false, "minimal direct feedback");
    options.addOption(OptionBuilder.withLongOpt("in")
                                   .withDescription("input file name")
                                   .hasArg()
                                   .withArgName("infile")
                                   .create("i"));
    options.addOption(OptionBuilder.withLongOpt("out")
                                   .withDescription("output file name")
                                   .hasArg()
                                   .withArgName("outfile")
                                   .create("o"));
    try {
      CommandLine cline = parser.parse(options, args );
      if (cline.hasOption("quiet")) {
        _quiet = true;
        }
      if (cline.hasOption("help")) {
        new HelpFormatter().printHelp("java -jar Grapher.exe.jar", options);
        System.exit(0);
        }
      if (cline.hasOption("in")) {
        _infile = cline.getOptionValue("in");
        }
      else {
        log.error("No input file specified"); 
        new HelpFormatter().printHelp("java -jar Grapher.exe.jar", options);
        System.exit(0);
        }
      if (cline.hasOption("out")) {
        _outfile = cline.getOptionValue("out");
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
   
  /** Tell whether running in a quiet mode.
    * @return Whether running in a quiet mode. */
  public boolean quiet() {
    return _quiet;
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
                                    
  private static String  _help       = "";                                    
  private        boolean _quiet      = false;
  private        String  _infile;
  private        String  _outfile;

  /** Logging . */
  private static Logger log = Logger.getLogger(CLI.class);
   
 
  }
