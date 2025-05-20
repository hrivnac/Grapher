import com.Grapher.Utils.Init;
import com.Grapher.Utils.Params;
import com.Grapher.Convertors.Convertor;
import com.Grapher.Analysis.Analyser;

Init.init(false);
params = new Params();
params.setInfile("AoI.graphml");
params.setOutfile("AoI.dot");
convertor = new Convertor(params);

analyser = new Analyser(params);
analyser.fill(convertor.read());
analyser.applyStrongConnectivity();                 
//analyser.applyConnectivity(10);                   
//analyser.applyClustering("GirvanNewman", 30);     
//analyser.applyClustering("LabelPropagation", 30); 
//analyser.applyClustering("KSpanningTree", 30);    
                                                    




