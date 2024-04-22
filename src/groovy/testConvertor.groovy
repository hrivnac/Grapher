import com.Grapher.Convertors.Convertor;
import com.Grapher.GUI.Shower;

cli.setInfile("AoI.graphml"); // or -i AoI.graphml
cli.setOutfile("AoI.dot");    // or -o AoI.graphml
convertor = new Convertor(cli);
convertor.read();
convertor.convert();