package il.ac.technion.cs.fling;

import il.ac.technion.cs.fling.annotations.DerivesTo;
import il.ac.technion.cs.fling.annotations.Fling;
import il.ac.technion.cs.fling.annotations.Parameters;
import il.ac.technion.cs.fling.annotations.Start;
import il.ac.technion.cs.fling.annotations.Terminals;
import il.ac.technion.cs.fling.annotations.Variables;
import il.ac.technion.cs.fling.internal.grammar.rules.Terminal;
import il.ac.technion.cs.fling.internal.grammar.rules.Variable;

@Fling
public class ArithmeticExpression {
	@Terminals
	public enum Σ implements Terminal {
		plus, minus, times, divide, begin, end, //
		@Parameters({ int.class })
		n
	}

	@Variables
	public enum Γ implements Variable {
		@Start
		@DerivesTo({ "Mul", "Add_" })
		Expression, //
		@DerivesTo({ "plus", "Mul", "Add_" })
		@DerivesTo({ "minus", "Mul", "Add_" })
		@DerivesTo({})
		Add_, //
		@DerivesTo({ "Term", "Mul_" })
		Mul, //
		@DerivesTo({ "times", "Term", "Mul_" })
		@DerivesTo({ "divide", "Term", "Mul_" })
		@DerivesTo({})
		Mul_, //
		@DerivesTo({ "begin", "Expression", "end" })
		@DerivesTo({ "n" })
		Term
	}
}
