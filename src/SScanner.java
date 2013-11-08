import java.io.IOException;
import java.io.StringReader;


public class SScanner {
	private boolean isNextRed=false;
	private char next='\0';
	private String str;
	private StringReader r;
	public SScanner(String str) {
		this.str = str;
		r = new StringReader(str);
	}
	public boolean hasNext(){
		if(!isNextRed){
			try {
				next=(char) r.read();
			} catch (IOException e) {
				e.printStackTrace();
			}
			isNextRed = true;
		}
		return (next!=-1)&&(next!='\0')&&(next!=65535);
	}
	public char next(){
		if(!isNextRed){
			try {
				next=(char) r.read();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		isNextRed = false;
		return next;
	}
	public void reset(){
		try {
			r.reset();
		} catch (IOException e) {
			e.printStackTrace();
		}
		isNextRed = false;
		next='\0';
	}
	public String toString() {
		return str;
	};
}
