
public class FAEdge {
	private int start;
	private int end;
	private String c;
	public int getStart() {
		return start;
	}
	public void setStart(int value){
		this.start = value;
	}
	public void setEnd(int value){
		this.end = value;
	}
	public int getEnd() {
		return end;
	}
	public FAEdge(int start, int end, String c) {
		super();
		this.start = start;
		this.end = end;
		this.c = c;
	}
	public String getCharacter(){
		return c;
	}
	@Override
	public String toString() {
		return "("+start+")"+"==\""+c+"\"==>"+"("+end+")";
	}
}
