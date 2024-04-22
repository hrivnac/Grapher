import com.Grapher.Convertors.Convertor;
import com.Grapher.GUI.Shower;

cli.setInfile("AoI.graphml"); // or -i AoI.graphml
convertor = new Convertor(cli);
new Shower().show(convertor.read()); // or -w
