import com.Grapher.Utils.Init;
import com.Grapher.Utils.Params;
import com.Grapher.Convertors.Convertor;
import com.Grapher.GUI.Shower;

Init.init(false);
params = new Params();
params.setInfile("AoI.graphml");
convertor = new Convertor(params);
new Shower().show(convertor.read());
