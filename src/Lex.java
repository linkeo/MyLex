import java.io.File;


public class Lex {
	public static void main(String[] args) {
		File file; 
		if(args.length==0)
			file = new File("language.mylex");
		else
			file = new File(args[0]);
		System.out.println(file.getAbsolutePath());
		
	}
}
