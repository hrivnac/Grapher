import com.Grapher.Convertors.Convertor;
import com.Grapher.Analysis.Analyser;

cli.setInfile("AoI.graphml"); // or -i AoI.graphml
cli.setOutfile("AoI.dot"); // or -o AoI.graphml
convertor = new Convertor(cli);

analyser = new Analyser(cli);
analyser.fill(convertor.read());
analyser.applyStrongConnectivity(); // or -a sc andan alyser.apply()
convertor.convert(analyser.graph());


