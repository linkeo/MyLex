import java.util.HashMap;
import java.util.HashSet;


public class FA {
	private int index = 0;
	private FAEnvironment e;
	private FAVertex in;
	private HashMap<FAVertex,Integer> out = new HashMap<FAVertex,Integer>();
	
	public FA(FAEnvironment faEnvironment) {
		e = faEnvironment;
	}
	public FAVertex createVertex(){
		int index;
		if(e!=null)
			index = e.createIndex();
		else
			index = this.index++;
		FAVertex v =  new FAVertex(index);
		e.add(v);
		return v;
	}
	public FAEdge createEdge(FAVertex start, FAVertex end, String c){
		FAEdge edge =  new FAEdge(start.getValue(), end.getValue(), c);
		if(e!=null)
			e.add(edge);
		return edge;
	}
	
	public void setInVertex(FAVertex v){
		in = v;
	}

	public void setOutVertex(FAVertex v){
		out.put(v,outResult);
	}
	int outResult = 0;
	public void setOutResult(int resultCode){
		outResult = resultCode;
		for(FAVertex end : out().keySet())
			out.put(end, resultCode);
	}
	public void setOutVertex(FAVertex v, int i) {
		out.put(v,i);
	}
	public void unsetOutVertex(FAVertex v){
		out.remove(v);
	}
	public FAVertex in() {
		return in;
	}
	public HashMap<FAVertex,Integer> out(){
		return out;
	}
	public void setOut(HashMap<FAVertex, Integer> t) {
		out.putAll(t);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("==========FA==========\n");
		sb.append("Start: ");
		sb.append(in());
		sb.append('\n');
		sb.append("End: ");
		sb.append(out());
		sb.append('\n');
		visited.clear();
		printAll(in().getValue(),sb);
		
		sb.append("======================\n");
		return sb.toString();
	}
	HashSet<FAEdge> visited = new HashSet<FAEdge>();
	private void printAll(int v, StringBuilder sb){
		for(FAVertex t : e.move(e.getVertex(v))){
			FAEdge edge = e.getEdge(v, t.getValue());
			if(edge!=null&&!visited.contains(edge)){
				sb.append(edge);
				sb.append('\n');
				visited.add(edge);
				printAll(edge.getEnd(),sb);
			}
		}
	}
}
