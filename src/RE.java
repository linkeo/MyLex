import java.util.Stack;

public class RE{
// ������: (, ), |, *, \

// ������ʽ����ת����:
// 1.�����ַ�ѹջ1;
// 2.����������:
// 	1.���ջ2��, ѹջ2;
// 	2.�����ж�ջ2��ջ��ֱ������������, ���ջ�����������ȼ���(����������ϵĻ�), ����ջ��������, ����ѹջ2;
// 3.����(ѹջ2,
// 4.����)��2.2����, ֱ������������, ��������������ͬ���ھ�;
// 5.����\��ȡ��һ���ַ�, ���Բ������ж�;

	// input	output
	// 0:)		
	// 1:|		
	// 2:.		
	// 3:*		
	// 4:(		

	//prepare:	(a|b)*aab(bab*a)*
	//			(a|b)*.a.a.b.(b.a.b*.a)*
	//postfix:	ab|*a.a.b.ba.b*.a.*.
	//			ab|*a.a.b.ba.b*.a.*.
	//			ab|*a.a.b.ba.b*.a.*.

	private SScanner s;
	private Stack<Character> stack = new Stack<Character>();
	private StringBuilder result = new StringBuilder();
	public RE(String str){
		s = new SScanner(str);
	}
	public static void main(String[] args){
		RE p = new RE("(\\.|b)*aab(bab*a)*");
		System.out.println(p.postfix());
	}
	public void setSource(String str){
		s = new SScanner(str);
	}
	public String read() {
		StringBuilder sb = new StringBuilder();
		s.reset();
		while(s.hasNext()){
			char c = s.next();
			System.out.print("Meet "+c+" "+(int)c);
			sb.append(c);
			System.out.print(" > "+sb.toString());
			System.out.println();
		}
		s.reset();
		return sb.toString();
	}
	private void prepare() {
		StringBuilder sb = new StringBuilder();
		System.out.println("================Preparing");
		s.reset();
		while(s.hasNext()){
			char c = s.next();
			System.out.print("Meet "+c);
			if(c!='\\'){
				if( (!((sb.length()<2)||((sb.charAt(sb.length()-2)=='\\')&&(sb.charAt(sb.length()-1)=='.'))))
						&&
					((sb.length()<1)||(sb.charAt(sb.length()-1)=='.'))
						&&
					((c=='|')||(c==')')||(c=='*')) ){
					sb.deleteCharAt(sb.length()-1);
				}
				sb.append(c);
				if( s.hasNext()&&( (c!='|')&&(c!='(') ) ){
					sb.append('.');
				}
			}else{
				sb.append(c);
			}
			System.out.print(" > "+sb.toString());
			System.out.println();
		}
		s = new SScanner(sb.toString());
	}
	public String postfix() {
		prepare();
		System.out.println("================Postfixing");
		result=new StringBuilder();
		stack.clear();
		s.reset();
		boolean metBackSlash = false;
		while(s.hasNext()){
			char c = s.next();
			System.out.print("Meet "+c+" > ");
			if(c!='\\'){
				if(metBackSlash||!isOperator(c)){
					metBackSlash = false;
					result.append(c);
				}else{
					if(stack.isEmpty()){
						stack.push(c);
					}else{
						boolean bracket = false;
						while(true){
							if(stack.isEmpty())
								break;
							char op = stack.pop();
							if(shouldProcessOp(op,c)){
								if(process(op)){
									bracket = true;
									break;
								}
							}else{
								stack.push(op);
								break;
							}
						}
						if(bracket){
							bracket = false;
						}else{
							stack.push(c);
						}
					}
				}
			}else{
				result.append(c);
				metBackSlash=true;
			}
			System.out.println(result.toString()+" ~ "+stack.toString());
		}
		while(!stack.isEmpty())
			result.append(stack.pop());
		System.out.println("Finish > "+result.toString()+" ~ "+stack.toString());
		return result.toString();
	}
	/**
	 * 
	 * @param op
	 * @return return whether it's a left bracket
	 */
	private boolean process(char op){
		if(op!='('){
			result.append(op);
		}
		if(op=='(')
			return true;
		else
			return false;
	}
	public boolean isOperator(char c){
		switch(c){
		case '.':
		case '*':
		case '|':
		case '(':
		case ')':
			return true;
		default:
			return false;
		}
	}
	private boolean shouldProcessOp(char op, char c){
		if(c==')')
			return true;
		else if(c=='(')
			return false;
		else{
			if(c=='|'&&op!='(')
				return true;
			else if(c=='.'&&(op=='*'||op=='.'))
				return true;
			else if(c=='*'&&op=='*')
				return true;
			else
				return false;
		}
	}
}