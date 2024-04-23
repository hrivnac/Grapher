import com.Grapher.Convertors.Convertor;
import com.Grapher.Analysis.Analyser;

cli.setInfile("AoI.graphml"); // or -i AoI.graphml
cli.setOutfile("AoI.dot"); // or -o AoI.graphml
convertor = new Convertor(cli);

analyser = new Analyser(cli);
analyser.fill(convertor.read());
analyser.applyStrongConnectivity();                  // or -a sc and analyser.apply()
//analyser.applyConnectivity(10);                    // or -a co,10 and analyser.apply()
//analyser.applyClustering("GirvanNewman", 30);      // or -a cl,GirvanNewman,10 and analyser.apply()
//analyser.applyClustering("LabelPropagation", 30);  // or -a cl,LabelPropagation and analyser.apply()
//analyser.applyClustering("KSpanningTree", 30);     // or -a cl,KSpanningTree,10 and analyser.apply()





