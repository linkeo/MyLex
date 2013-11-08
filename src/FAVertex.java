
public class FAVertex {
	private int value;

	public FAVertex(int value) {
		super();
		this.value = value;
	}

	public int getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return "("+value+")";
	}
}
