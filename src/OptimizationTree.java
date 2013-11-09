import java.util.ArrayList;


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
		return hasLeftChild(node)&&hasRightChild(node);
	}

	public void move(FAVertex v, OptimizationNode node,
			OptimizationNode newnode) {
		node.remove(v);
		newnode.add(v);
	}
	public OptimizationNode find(FAVertex v){
		for(OptimizationNode n : this)
			if(n!=null&&n.contains(v))
				return n;
		System.err.println("Optimization Error: vertex not found in tree.");
		return null;
	}
}
