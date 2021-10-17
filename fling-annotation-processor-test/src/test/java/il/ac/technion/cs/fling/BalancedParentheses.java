package il.ac.technion.cs.fling;

import il.ac.technion.cs.fling.annotations.DerivesTo;
import il.ac.technion.cs.fling.annotations.Fling;
import il.ac.technion.cs.fling.annotations.Start;
import il.ac.technion.cs.fling.annotations.Terminals;
import il.ac.technion.cs.fling.annotations.Variables;
import il.ac.technion.cs.fling.internal.grammar.rules.Terminal;
import il.ac.technion.cs.fling.internal.grammar.rules.Variable;

@Fling
public class BalancedParentheses {
	@Terminals
	public enum Σ implements Terminal {
		c, ↄ
	}

	@Variables
	public enum Γ implements Variable {
		@Start
		@DerivesTo({ "c", "Parantheses", "ↄ", "Parantheses" })
		@DerivesTo({})
		Parantheses
	}
}
