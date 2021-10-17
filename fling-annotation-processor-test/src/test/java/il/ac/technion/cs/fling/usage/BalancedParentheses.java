package il.ac.technion.cs.fling.usage;

import static il.ac.technion.cs.fling.generated.BalancedParentheses.c;

import il.ac.technion.cs.fling.generated.BalancedParenthesesAST.Parantheses;
import il.ac.technion.cs.fling.generated.BalancedParenthesesAST.Parantheses1;
import il.ac.technion.cs.fling.generated.BalancedParenthesesAST.Visitor;

public class BalancedParentheses {
	public static void main(String[] args) {
		Parantheses p = c().c().ↄ().c().ↄ().ↄ().$();
		ParanthesesCounter counter = new ParanthesesCounter();
		counter.visit(p);
		System.out.println(counter.value);
	}

	public static class ParanthesesCounter extends Visitor {
		int value = 0;

		@Override
		public void whileVisiting(Parantheses1 p1) {
			++value;
		}
	}
}
