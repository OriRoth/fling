package org.spartan.fajita.api.bnf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.spartan.fajita.api.bnf.TestUtils.expectedSet;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.spartan.fajita.api.bnf.rules.DerivationRule;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;
import org.spartan.fajita.api.bnf.symbols.Type;
import org.spartan.fajita.api.parser.Item;
import org.spartan.fajita.api.parser.State;

public class StateClosureTest {
    private enum Term implements Terminal {
	a, b, c, d;

	@Override
	public Type type() {
	    return Type.VOID;
	}
    };

    private enum NT implements NonTerminal {
	S, A;
    };

    private enum NT2 implements NonTerminal {
	S, A, B, C;
    };

    public static Set<Item> expectedItemSet(final DerivationRule... rules) {
	HashSet<Item> $ = new HashSet<>();
	for (DerivationRule derivationRule : rules)
	    $.add(new Item(derivationRule, 0));
	return $;
    }

    @Test
    public void testInitialItemKept() {
	BNF bnf = new BNFBuilder(Term.class, NT.class) //
		.startConfig() //
		.setApiNameTo("TEST") //
		.setStartSymbols(NT.S) //
		.endConfig() //
		.derive(NT.S).to(Term.a).and(Term.b) //
		.derive(NT.A).to(NT.A).or(NT.A) //
		.finish();

	DerivationRule initialRule = bnf.getDerivationRules().iterator().next();

	assertEquals(expectedSet(new Item(initialRule, 0)), bnf.getInitialState().items);
    }

    @Test
    public void testClosureOnDerivationRule() {
	BNF bnf = new BNFBuilder(Term.class, NT.class) //
		.startConfig() //
		.setApiNameTo("TEST") //
		.setStartSymbols(NT.S) //
		.endConfig() //
		.derive(NT.S).to(NT.A).and(Term.b) //
		.derive(NT.A).to(Term.a).and(Term.c) //
		.finish();

	DerivationRule S_Rule = bnf.getDerivationRules().stream().filter(r -> r.lhs.equals(NT.S)).findAny().get();
	DerivationRule A_Rule = bnf.getDerivationRules().stream().filter(r -> r.lhs.equals(NT.A)).findAny().get();

	Set<Item> expectedSet = expectedSet(new Item(S_Rule, 0), new Item(A_Rule, 0));
	assertEquals(expectedSet, bnf.getInitialState().items);
    }

    @Test
    public void testClosureOnInheritenceRule() {
	BNF bnf = new BNFBuilder(Term.class, NT2.class) //
		.startConfig() //
		.setApiNameTo("TEST") //
		.setStartSymbols(NT2.S) //
		.endConfig() //
		.derive(NT2.S).to(NT2.A).or(NT2.B).or(NT2.C) //
		.derive(NT2.A).to(Term.a) //
		.derive(NT2.B).to(Term.b) //
		.derive(NT2.C).to(Term.c) //
		.finish();

	// fetching the rules
	Set<DerivationRule> dRules = new HashSet<>(bnf.getDerivationRules());
	bnf.getInheritenceRules().forEach(iRule -> dRules.addAll(iRule.getAsDerivationRules()));
	DerivationRule StoA_Rule = dRules.stream().filter(r -> r.lhs.equals(NT2.S) && r.expression.get(0).equals(NT2.A))
		.findAny().get();
	DerivationRule StoB_Rule = dRules.stream().filter(r -> r.lhs.equals(NT2.S) && r.expression.get(0).equals(NT2.B))
		.findAny().get();
	DerivationRule StoC_Rule = dRules.stream().filter(r -> r.lhs.equals(NT2.S) && r.expression.get(0).equals(NT2.C))
		.findAny().get();

	DerivationRule A_Rule = dRules.stream().filter(r -> r.lhs.equals(NT2.A)).findAny().get();
	DerivationRule B_Rule = dRules.stream().filter(r -> r.lhs.equals(NT2.B)).findAny().get();
	DerivationRule C_Rule = dRules.stream().filter(r -> r.lhs.equals(NT2.C)).findAny().get();

	// test
	Set<Item> expectedSet = expectedItemSet(StoA_Rule, StoB_Rule, StoC_Rule, A_Rule, B_Rule, C_Rule);
	assertEquals(expectedSet, bnf.getInitialState().items);
    }

    @Test
    public void testIterativeClosure() {
	BNF bnf = new BNFBuilder(Term.class, NT2.class) //
		.startConfig() //
		.setApiNameTo("TEST") //
		.setStartSymbols(NT2.S) //
		.endConfig() //
		.derive(NT2.S).to(NT2.A)//
		.derive(NT2.A).to(NT2.B).or(NT2.C) //
		.derive(NT2.B).to(Term.b) //
		.derive(NT2.C).to(Term.c) //
		.finish();

	// fetching the rules
	Set<DerivationRule> dRules = new HashSet<>(bnf.getDerivationRules());
	bnf.getInheritenceRules().forEach(iRule -> dRules.addAll(iRule.getAsDerivationRules()));
	DerivationRule AtoB_Rule = dRules.stream().filter(r -> r.lhs.equals(NT2.A) && r.expression.get(0).equals(NT2.B))
		.findAny().get();
	DerivationRule AtoC_Rule = dRules.stream().filter(r -> r.lhs.equals(NT2.A) && r.expression.get(0).equals(NT2.C))
		.findAny().get();

	DerivationRule S_Rule = dRules.stream().filter(r -> r.lhs.equals(NT2.S)).findAny().get();
	DerivationRule B_Rule = dRules.stream().filter(r -> r.lhs.equals(NT2.B)).findAny().get();
	DerivationRule C_Rule = dRules.stream().filter(r -> r.lhs.equals(NT2.C)).findAny().get();

	// test
	Set<Item> expectedSet = expectedItemSet(AtoB_Rule, AtoC_Rule, S_Rule, B_Rule, C_Rule);
	assertEquals(expectedSet, bnf.getInitialState().items);
    }

    @Test
    public void testSameItemAppearsOnlyOnce() {
	BNF bnf = new BNFBuilder(Term.class, NT.class) //
		.startConfig() //
		.setApiNameTo("TEST") //
		.setStartSymbols(NT.S) //
		.endConfig() //
		.derive(NT.S).to(NT.A)//
		.derive(NT.A).to(NT.S).or(NT.A) //
		.finish();

	// fetching the rules
	// 3 items :
	// S -> . A
	// A -> . A
	// A -> . S
	assertEquals(3, bnf.getInitialState().items.size());
    }

    @Test
    public void testClosureWithMultipleStartVariables() {
	BNF bnf = new BNFBuilder(Term.class, NT2.class) //
		.startConfig() //
		.setApiNameTo("TEST") //
		.setStartSymbols(NT2.S, NT2.A) //
		.endConfig() //
		.derive(NT2.S).to(NT2.B)//
		.derive(NT2.A).to(NT2.C).or(NT2.A) //
		.derive(NT2.B).to(Term.b)//
		.derive(NT2.C).to(Term.c)//
		.finish();

	// fetching the rules
	Set<DerivationRule> dRules = new HashSet<>(bnf.getDerivationRules());
	bnf.getInheritenceRules().forEach(iRule -> dRules.addAll(iRule.getAsDerivationRules()));
	DerivationRule AtoA_Rule = dRules.stream().filter(r -> r.lhs.equals(NT2.A) && r.expression.get(0).equals(NT2.A))
		.findAny().get();
	DerivationRule AtoC_Rule = dRules.stream().filter(r -> r.lhs.equals(NT2.A) && r.expression.get(0).equals(NT2.C))
		.findAny().get();

	DerivationRule S_Rule = dRules.stream().filter(r -> r.lhs.equals(NT2.S)).findAny().get();
	DerivationRule B_Rule = dRules.stream().filter(r -> r.lhs.equals(NT2.B)).findAny().get();
	DerivationRule C_Rule = dRules.stream().filter(r -> r.lhs.equals(NT2.C)).findAny().get();

	// test
	Set<Item> expectedSet = expectedItemSet(AtoA_Rule, AtoC_Rule, S_Rule, B_Rule, C_Rule);
	assertEquals(expectedSet, bnf.getInitialState().items);
    }

    @Test
    public void testNextState() {
	BNF bnf = new BNFBuilder(Term.class, NT.class) //
		.startConfig() //
		.setApiNameTo("TEST") //
		.setStartSymbols(NT.S) //
		.endConfig() //
		.derive(NT.S).to(NT.A).and(Term.b) //
		.derive(NT.A).to(Term.a).and(Term.c) //
		.finish();

	State initialState = bnf.getInitialState();
	AssertEquals(initialState.goTo(Term.c),
    }
}
