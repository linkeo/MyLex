import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;


public class FAEnvironment {
	public static String EPSILON = "";
	private HashSet<FAVertex> vertices = new HashSet<FAVertex>();
	private HashSet<FAEdge> edges = new HashSet<FAEdge>();
	private int index = 0;
	public int createIndex(){
		return ++index;
	}
	public FA parseNFAtoDFA(FA nfa, FAEnvironment newEnvironment){
		HashSet<String> marks = new HashSet<String>();
		HashMap<HashSet<Integer>,Integer> nv = new HashMap<HashSet<Integer>,Integer>();
		ArrayDeque<HashSet<FAVertex>> unset = new ArrayDeque<HashSet<FAVertex>>();
		//Get all Terminals
		for(FAEdge edge : edges)
			marks.add(edge.getCharacter());
		FA dfa = new FA(newEnvironment);
		
		//Create Start Vertex
		HashSet<FAVertex> set = e_closure(nfa.in());
		if(!nv.containsKey(values(set))){
			FAVertex s = dfa.createVertex();
			dfa.setInVertex(s);
			nv.put(values(set), s.getValue());
			unset.add(set);
		}
		//Main Process
		while(!unset.isEmpty()){
			//Get current processing vertex
			HashSet<FAVertex> curr = unset.removeFirst();
			FAVertex s = newEnvironment.getVertex(nv.get(values(curr)));
			//Check end vertex
			for(FAVertex end : nfa.out().keySet()){
				if(curr.contains(end)){
					dfa.setOutVertex(s,nfa.out().get(end));
				}
			}
			//Create or Reference to it's next vertex
			for(String mark : marks){
				HashSet<FAVertex> core = move(curr, mark);
				set = e_closure(core);
				if(!nv.containsKey(values(set))){
					FAVertex t = dfa.createVertex();
					dfa.createEdge(s, t, mark);
					nv.put(values(set), t.getValue());
					unset.add(set);
				}else{
					int value = nv.get(values(set));
					FAVertex t = newEnvironment.getVertex(value);
					dfa.createEdge(s, t, mark);
				}
			}
		}
		System.out.println(nv);
		return dfa;
	}
	public FA parseREtoNFA(RE re){
		String postfix = re.postfix();
		SScanner scan = new SScanner(postfix);
		Stack<FA> stack = new Stack<FA>();
		while(scan.hasNext()){
			char c = scan.next();
			if(c=='\\')
				stack.push(create(c+""+scan.next()));
			else if(!re.isOperator(c))
				stack.push(create(c+""));
			else
				process(c,stack);
		}
		return stack.pop();
	}
	public HashSet<Integer> values(HashSet<FAVertex> vertices){
		HashSet<Integer> values = new HashSet<Integer>();
		for(FAVertex v : vertices)
			values.add(v.getValue());
		return values;
	}
	public HashSet<FAVertex> e_closure(FAVertex v){
		Stack<FAVertex> stack = new Stack<FAVertex>();
		HashSet<FAVertex> closure = new HashSet<FAVertex>();
		stack.add(v);
		while(!stack.isEmpty()){
			FAVertex t = stack.pop();
			for(FAVertex u : move(t,EPSILON)){
				closure.add(u);
				stack.push(u);
			}
		}
		return closure;
	}

	public HashSet<FAVertex> e_closure(HashSet<FAVertex> set){
		Stack<FAVertex> stack = new Stack<FAVertex>();
		HashSet<FAVertex> closure = new HashSet<FAVertex>();
		stack.addAll(set);
		while(!stack.isEmpty()){
			FAVertex t = stack.pop();
			for(FAVertex u : move(t,EPSILON)){
				closure.add(u);
				stack.push(u);
			}
		}
		return closure;
	}

	public HashSet<FAVertex> move(FAVertex v, String mark){
		HashSet<FAVertex> move = new HashSet<FAVertex>();
		for(FAEdge edge : edges)
			if(getVertex(edge.getStart())==v&&edge.getCharacter().equals(mark))
				move.add(getVertex(edge.getEnd()));
		return move;
	}
	public HashSet<FAVertex> move(HashSet<FAVertex> set, String mark){
		HashSet<FAVertex> move = new HashSet<FAVertex>();
		for(FAVertex v:set)
			for(FAEdge edge : edges)
				if(getVertex(edge.getStart())==v&&edge.getCharacter().equals(mark))
					move.add(getVertex(edge.getEnd()));
		return move;
	}
	public HashSet<FAVertex> move(FAVertex v) {
		HashSet<FAVertex> move = new HashSet<FAVertex>();
		for(FAEdge edge : edges)
			if(getVertex(edge.getStart())==v)
				move.add(getVertex(edge.getEnd()));
		return move;
	}
	
	
	
	public FAVertex getVertex(int value) {
		for(FAVertex vertex : vertices)
			if(vertex.getValue()==value)
				return vertex;
		return null;
	}
	
	public FAEdge getEdge(int vs, int vt){
		for(FAEdge edge : edges)
			if(edge.getStart()==vs && edge.getEnd()==vt)
				return edge;
		return null;
	}
	
	private void process(char c, Stack<FA> stack){
		switch(c){
		case '.':
			stack.push(connect(stack.pop(), stack.pop()));
			break;
		case '*':
			stack.push(closure(stack.pop()));
			break;
		case '|':
			stack.push(union(stack.pop(), stack.pop()));
			break;
		}
	}
	public FA create(String c){
		FA r = new FA(this);
		FAVertex s = r.createVertex();
		FAVertex t = r.createVertex();
		r.setInVertex(s);
		r.setOutVertex(t);
		
		r.createEdge(s, t, c);
		return r;
	}
	public FA union(FA a, FA b){
		FA r = new FA(this);
		FAVertex s = r.createVertex();
		FAVertex t = r.createVertex();
		r.setInVertex(s);
		r.setOutVertex(t);
		
		r.createEdge(s, a.in(), EPSILON);
		r.createEdge(s, b.in(), EPSILON);
		for(FAVertex ta : a.out().keySet())
			r.createEdge(ta, t, EPSILON);
		for(FAVertex tb : b.out().keySet())
			r.createEdge(tb, t, EPSILON);
		return r;
	}
	public FA connect(FA a, FA b){
		FA r = new FA(this);
		FAVertex s = a.in();
		HashMap<FAVertex, Integer> t = b.out();
		r.setInVertex(s);
		r.setOut(t);

		FAVertex bs = b.in();
		for(FAVertex at : a.out().keySet())
			r.createEdge(at, bs, EPSILON);
		return r;
	}
	public FA closure(FA a){
		FA r = new FA(this);
		FAVertex s = r.createVertex();
		FAVertex t = r.createVertex();
		r.setInVertex(s);
		r.setOutVertex(t);
		
		FAVertex as = a.in();
		r.createEdge(s, t, EPSILON);
		r.createEdge(s, as, EPSILON);
		for(FAVertex at : a.out().keySet()){
			r.createEdge(at, t, EPSILON);
			r.createEdge(at, as, EPSILON);
		}
		return r;
	}
	public FA merge(Collection<FA> fas){
		FA r = new FA(this);
		FAVertex s = r.createVertex();
		r.setInVertex(s);
		for(FA fa : fas){
			FAVertex fast = fa.in();
			r.createEdge(s, fast, EPSILON);
			r.setOut(fa.out());
		}
		return r;
	}
	
	public void add(FAEdge edge) {
		edges.add(edge);
	}
	public void add(FAVertex v) {
		vertices.add(v);
	}
	public HashSet<FAVertex> getAllVertices() {
		return vertices;
	}
	public HashSet<FAEdge> getAllEdges() {
		return edges;
	}
}
