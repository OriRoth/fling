package org.spartan.fajita.api.parser;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.BNFBuilder;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;
import org.spartan.fajita.api.bnf.symbols.Type;
import org.spartan.fajita.api.parser.ParsingTable.Reduce;
import org.spartan.fajita.api.parser.ParsingTable.ReduceReduceConflictException;
import org.spartan.fajita.api.parser.ParsingTable.Shift;
import org.spartan.fajita.api.parser.ParsingTable.ShiftReduceConflictException;

public class LRParserTest {

    private enum Term implements Terminal {
	plus, multiply, lp, rp, id;

	@Override
	public Type type() {
	    return Type.VOID;
	}

    }

    private enum NT implements NonTerminal {
	E, T, F;
    }

    private enum NT2 implements NonTerminal {
	S, A, B, C;
    };

    private BNF bnf;
    private LRParser parser;

    @Before
    public void initialize() throws ReduceReduceConflictException, ShiftReduceConflictException {
	bnf = new BNFBuilder(Term.class, NT.class)//
		.startConfig()//
		.setApiNameTo("Test taken from Book about compilation") //
		.setStartSymbols(NT.E) //
		.endConfig() //
		.derive(NT.E).to(NT.E).and(Term.plus).and(NT.T) //
		.derive(NT.E).to(NT.T) //
		.derive(NT.T).to(NT.T).and(Term.multiply).and(NT.F) //
		.derive(NT.T).to(NT.F) //
		.derive(NT.F).to(Term.lp).and(NT.E).and(Term.rp) //
		.derive(NT.F).to(Term.id) //
		.finish();

	parser = new LRParser(bnf);
    }

    @Test
    public void testActionTableGoto() {
	assertEquals(parser.actionTable(parser.getInitialState(), Term.id).getClass(), Shift.class);
    }

    @Test
    public void testActionTableReduce() {
	Shift shift = (Shift) parser.actionTable(parser.getInitialState(), Term.id);
	State nextState = parser.states.get(shift.stateIndex);
	assertEquals(parser.actionTable(nextState, Term.plus).getClass(), Reduce.class);
    }

    @Test(expected = ShiftReduceConflictException.class)
    public void testShiftReduceConflict() throws ReduceReduceConflictException, ShiftReduceConflictException {
	BNF bnf = new BNFBuilder(Term.class, NT2.class) //
		.startConfig() //
		.setApiNameTo("TEST") //
		.setStartSymbols(NT2.S) //
		.endConfig() //
		.derive(NT2.S).to(NT2.A) //
		.derive(NT2.A).to(NT2.S) //
		.derive(NT2.B).to(Term.id) //
		.derive(NT2.C).to(Term.id) //
		.finish();

	new LRParser(bnf);
    }

    @Test(expected = ReduceReduceConflictException.class)
    public void testReduceReduceConflict() throws ReduceReduceConflictException, ShiftReduceConflictException {
	BNF bnf = new BNFBuilder(Term.class, NT2.class) //
		.startConfig() //
		.setApiNameTo("TEST") //
		.setStartSymbols(NT2.S) //
		.endConfig() //
		.derive(NT2.S).to(NT2.A).or(NT2.B) //
		.derive(NT2.A).to(Term.id) //
		.derive(NT2.B).to(Term.id) //
		.derive(NT2.C).to(Term.id) //
		.finish();

	new LRParser(bnf);
    }

}
