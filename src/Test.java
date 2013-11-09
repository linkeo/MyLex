
public class Test {
	public static void main(String[] args) {
		FAEnvironment nenv = new FAEnvironment();
		FAEnvironment denv = new FAEnvironment();
		RE re = new RE("(\\.|b)*aab(bab*a)*");
		FA nfa = nenv.parseREtoNFA(re);
		System.out.println(nfa);
		FA dfa = nenv.parseNFAtoDFA(nfa, denv);
		System.out.println(dfa);
		if(denv.checkDFA(dfa))
			denv.optimizeDFA(dfa);
		else
			System.err.println("It's not a DFA");
	}
}
