import java.util.ArrayDeque;
import java.util.Collection;
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
	private HashSet<String> terminals(){
		HashSet<String> marks = new HashSet<String>();
		//Get all Terminals
		for(FAEdge edge : edges)
			marks.add(edge.getCharacter());
		return marks;
	}
	public FA parseNFAtoDFA(FA nfa, FAEnvironment newEnvironment){
		System.out.println(">Parsing NFA into DFA:");
		HashSet<String> marks = terminals();
		marks.remove(EPSILON);
		HashMap<HashSet<Integer>,Integer> nv = new HashMap<HashSet<Integer>,Integer>();
		ArrayDeque<HashSet<FAVertex>> unset = new ArrayDeque<HashSet<FAVertex>>();

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
	
	public boolean checkDFA(FA fa){
		HashSet<String> marks = terminals();
		HashSet<FAVertex> visited = new HashSet<FAVertex>();
		Stack<FAVertex> stack = new Stack<FAVertex>();
		
		FAVertex v = fa.in();
		visited.add(v);
		stack.push(v);
		while(!stack.isEmpty()){
			v = stack.pop();
			for(String mark : marks){
				if(mark.equals(EPSILON))
					return false;
				HashSet<FAVertex> move = move(v,mark);
				if(move.size()!=1)
					return false;
				for(FAVertex t : move){
					if(!visited.contains(t)){
						stack.push(t);
						visited.add(t);
					}
				}
			}
		}
		return true;
	}
	
	public HashSet<FAVertex> verticesInFA(FA fa){
		HashSet<FAVertex> visited = new HashSet<FAVertex>();
		Stack<FAVertex> stack = new Stack<FAVertex>();
		FAVertex v = fa.in();
		visited.add(v);
		stack.push(v);
		while(!stack.isEmpty()){
			v = stack.pop();
			HashSet<FAVertex> move = move(v);
			for(FAVertex t : move){
				if(!visited.contains(t)){
					stack.push(t);
					visited.add(t);
				}
			}
		}
		return visited;
	}
	
	public boolean optimizeDFA(FA dfa){
		System.out.println(">Optimizing DFA:");
		HashSet<FAVertex> vs = verticesInFA(dfa);
		HashMap<FAVertex, Integer> out = dfa.out();
		OptimizationTree tree = new OptimizationTree();
		OptimizationNode node = tree.root();
		node.addAll(vs);
		//step1:divide end and non-end
		for(FAVertex v : new OptimizationNode(node)){
			if(!out.containsKey(v))
				tree.move(v, node, tree.getLeftChild(node));
			else
				tree.move(v, node, tree.getRightChild(node));
		}
		System.out.println(">State Equivelance:");
		while(optimizeByStateEq(dfa, tree));
		System.out.println(tree);
		boolean optimized = false;
		for(OptimizationNode leaf : tree.leaves()){
			if(leaf.size()>1){
				optimized = true;
				Stack<FAVertex> stack = new Stack<FAVertex>();
				stack.addAll(leaf);
				FAVertex repr = stack.peek();
				if(dfa.out().containsKey(repr))
					for(FAVertex v : leaf){
						if(dfa.out().get(v)<dfa.out().get(repr))
							repr = v;
					}
				stack.remove(repr);
				while(!stack.empty()){
					FAVertex v = stack.pop();
					for(FAEdge e : edges){
						if(e.getStart()==v.getValue())
							e.setStart(repr.getValue());
						if(e.getEnd()==v.getValue())
							e.setEnd(repr.getValue());
					}
					dfa.out().remove(v);
				}
				HashSet<FAEdge> tempEdges = new HashSet<FAEdge>(edges);
				for(FAEdge e : tempEdges){
					for(FAEdge f : tempEdges){
						if((edges.contains(e)&&edges.contains(f))&&(e!=f)
								&&(e.getStart()==f.getStart())&&(e.getEnd()==f.getEnd())){
							edges.remove(f);
						}
					}
				}
			}
		}
		return optimized;
	}
	
	private boolean optimizeByStateEq(FA dfa, OptimizationTree tree){
		boolean optimized = false;
		for(String mark : terminals()){
//			System.out.println("Optimize using \""+mark+"\"");
			optimized = optimized || optimizeNode(tree.root(), tree, dfa, mark);
		}
		return optimized;
	}
	
	private boolean optimizeNode(OptimizationNode node, OptimizationTree tree,
			FA dfa, String mark) {
//		System.out.println(tree);
		if(tree.isLeaf(node)){
//			System.out.println("Optimizing node "+tree.indexOf(node)+":"+node.size());
			OptimizationNode expectNode=null;
			HashMap<FAVertex,OptimizationNode> foundMap = new HashMap<FAVertex, OptimizationNode>();
			boolean seperate = false;
			for(FAVertex v : node){
//				System.out.println("Expect node is "+expectNode);
				OptimizationNode foundNode = tree.find(next(v, mark));
				foundMap.put(v, foundNode);
//				System.out.println("Node found  is "+foundNode);
				if(expectNode==null)
					expectNode = foundNode;
				else
					if(foundNode!=expectNode){
//						System.out.println("Seperate on: "+expectNode+" != "+foundNode);
						seperate = true;
						break;
					}
			}
			if(seperate){
				System.out.println("Seperate node "+tree.indexOf(node)+", inequality of next(\""+mark+"\")");
				OptimizationNode tempLeft = new OptimizationNode();
				OptimizationNode tempRight = new OptimizationNode();
				OptimizationNode left = tree.getLeftChild(node);
				OptimizationNode right = tree.getRightChild(node);
				for(FAVertex v : new OptimizationNode(node)){
					OptimizationNode foundNode = foundMap.get(v);
					if(foundNode==expectNode){
						tree.move(v, node, tempLeft);
					}else{
						tree.move(v, node, tempRight);
					}
				}
				left.addAll(tempLeft);
				right.addAll(tempRight);
				return true;
			}else
				return false;
		}else{
			return optimizeNode(tree.getLeftChild(node), tree, dfa, mark)
					||optimizeNode(tree.getRightChild(node), tree, dfa, mark);
		}
	}
	public FA parseREtoNFA(RE re){
		System.out.println(">Postfixing Regular Expression:");
		String postfix = re.postfix();
		System.out.println(">Parsing Regular Expression into NFA:");
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
	/**
	 * <b>WARNING: this method is for DFA only!</b><br>
	 * it returns first found vertex next to v,mark or null if not found.
	 * @param v
	 * @param mark
	 * @return
	 */
	public FAVertex next(FAVertex v, String mark){
		for(FAEdge edge : edges)
			if(getVertex(edge.getStart())==v&&edge.getCharacter().equals(mark))
				return getVertex(edge.getEnd());
		return null;
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
