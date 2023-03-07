package com.Grapher.Apps;

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
public abstract class CLI {

  /** Create. */
  public CLI() {}

  /** Execute command line in required way.
    * @return The execution reesult. */
  public abstract String execute();
  
  /** Close. */
  public abstract void close();
    
  /** Parse the cli arguments.
    * @param args    The cli arguments.
    * @param helpMsg The general help message.
    * @return        The parsed {@link CommandLine}. */
  public static CommandLine parseArgs(String[] args,
                                      String   helpMsg) {
    return parseArgs(args, helpMsg, null);
    }
    
  /** Parse the cli arguments.
    * @param args    The cli arguments.
    * @param helpMsg The general help message.
    * @param options The already initialised {@link Options}. May be <tt>null</tt>.
    * @return        The parsed {@link CommandLine}. */
  public static CommandLine parseArgs(String[] args,
                                      String   helpMsg,
                                      Options  options) {
    CommandLineParser parser = new BasicParser();
    if (options == null) {
      options = new Options();
      }
    options.addOption("h", "help",     false, "show help");
    options.addOption("q", "quiet",    false, "minimal direct feedback");
    try {
      CommandLine cline = parser.parse(options, args );
      if (cline.hasOption("quiet")) {
        _quiet = true;
        }
      if (cline.hasOption("help")) {
        StringWriter out    = new StringWriter();
        PrintWriter  writer = new PrintWriter(out);
        new HelpFormatter().printHelp(writer, 80, helpMsg, "", options, 0, 0, "", true);
        writer.flush();
        _help = out.toString();
        }
      return cline;
      }
    catch (ParseException e) { 
      new HelpFormatter().printHelp(helpMsg, options);
      return null;
      }
    }
   
  /** Tell whether running in a quiet mode.
    * @return Whether running in a quiet mode. */
  public static boolean quiet() {
    return _quiet;
   }
   
   /** Give the help.
    * @return The help. */
  public static String help() {
    return _help;
   }
                                     
  private static String  _help       = "";
                                    
  private static boolean _quiet      = false;

  /** Logging . */
  private static Logger log = Logger.getLogger(CLI.class);
   
 
  }
