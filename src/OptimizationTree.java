import java.util.ArrayList;
import java.util.HashSet;


public class OptimizationTree extends ArrayList<OptimizationNode>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 0;
	public OptimizationTree() {
	}

	public OptimizationNode node(int index){
		for(int i=this.size();i<=index;++i){
			this.add(i,null);
		}
		if(this.get(index)==null)
			this.set(index, new OptimizationNode());
		return this.get(index);
	}
	public OptimizationNode root(){
		return node(0);
	}
	public OptimizationNode getParent(OptimizationNode node){
		int index = indexOf(node);
		if(index==-1){
			System.err.println("Optimization Error: no such node in tree.");
			return null;
		}else if(index==0){
			System.err.println("Optimization Error: no parent of root!");
			return null;
		}else{
			index = (index-1)/2;
			return this.node(index);
		}
	}
	public OptimizationNode getLeftChild(OptimizationNode node){
		int index = indexOf(node);
		if(index==-1){
			System.err.println("Optimization Error: no such node in tree.");
			return null;
		}else{
//			System.out.print(index);
			index = (index+1)*2-1;
			return this.node(index);
		}
	}
	public OptimizationNode getRightChild(OptimizationNode node){
		int index = indexOf(node);
		if(index==-1){
			System.err.println("Optimization Error: no such node in tree.");
			return null;
		}else{
//			System.out.print(index);
			index = (index+1)*2;
			return this.node(index);
		}
	}
	public boolean hasLeftChild(OptimizationNode node){
		int index = indexOf(node);
		if(index==-1){
			System.err.println("Optimization Error: no such node in tree.");
			return false;
		}else{
			index = (index+1)*2-1;
			return (this.size()>index)&&(this.get(index)!=null);
		}
	}
	public boolean hasRightChild(OptimizationNode node){
		int index = indexOf(node);
		if(index==-1){
			System.err.println("Optimization Error: no such node in tree.");
			return false;
		}else{
			index = (index+1)*2;
			return (this.size()>index)&&(this.get(index)!=null);
		}
	}
	public boolean isRoot(OptimizationNode node){
		int index = indexOf(node);
		if(index==-1){
			System.err.println("Optimization Error: no such node in tree.");
			return false;
		}else{
			return index==0;
		}
	}
	public boolean isLeaf(OptimizationNode node){
		return (node!=null)&&(!hasLeftChild(node))&&(!hasRightChild(node));
	}

	public void move(FAVertex v, OptimizationNode node,
			OptimizationNode newnode) {
//		System.out.println("Move: "+node+"--"+v+"->"+newnode);
//		node.remove(v);
		newnode.add(v);
	}
	public OptimizationNode find(FAVertex v){
		for(OptimizationNode n : this)
			if(isLeaf(n)&&n!=null&&n.contains(v)){
				return n;
			}
		System.err.println("Optimization Error: vertex not found in tree.");
		return null;
	}
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("===OPTIMIZATION TREE===\n");
		for(OptimizationNode n : this){
			if(isLeaf(n))
			sb.append("<"+indexOf(n)+">"+n+"\n");
		}
		sb.append("=======================\n");
		return sb.toString();
	}
	public HashSet<OptimizationNode> leaves(){
		HashSet<OptimizationNode> leaves = new HashSet<OptimizationNode>();
		for(OptimizationNode n : this)
			if(isLeaf(n))
				leaves.add(n);
		return leaves;
	}
}
