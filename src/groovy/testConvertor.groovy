import com.Grapher.Utils.Init;
import com.Grapher.Utils.Params;
import com.Grapher.Convertors.Convertor;

Init.init(false);
params = new Params();
params.setInfile("AoI.graphml"); 
params.setOutfile("AoI.dot");    
convertor = new Convertor(params);
convertor.read();
convertor.convert();

