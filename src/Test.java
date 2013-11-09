
public class Test {
	public static void main(String[] args) {
		FAEnvironment nenv = new FAEnvironment();
		FAEnvironment denv = new FAEnvironment();
//		RE re = new RE("(\\.|b)*aab(bab*a)*");
		RE re = new RE("=|+|-|\\*|/|.|>|<|!|==|!=|<=|>=|+=|-=|\\*=|/=|++|--");
		FA nfa = nenv.parseREtoNFA(re);
		System.out.println(nfa);
		FA dfa = nenv.parseNFAtoDFA(nfa, denv);
		System.out.println(dfa);
		if(denv.checkDFA(dfa)){
			if(denv.optimizeDFA(dfa))
				System.out.println("Optimized DFA:\n"+dfa);
		}else
			System.err.println("It's not a DFA");
	}
	
}
