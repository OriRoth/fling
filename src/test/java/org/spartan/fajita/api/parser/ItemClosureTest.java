// package org.spartan.fajita.api.parser;
//
// import static org.junit.Assert.assertEquals;
// import static org.spartan.fajita.api.bnf.TestUtils.expectedItemSet;
// import static org.spartan.fajita.api.bnf.TestUtils.findRule;
//
// import java.util.Set;
//
// import org.junit.Test;
// import org.spartan.fajita.api.bnf.BNF;
// import org.spartan.fajita.api.bnf.BNFBuilder;
// import org.spartan.fajita.api.bnf.rules.DerivationRule;
// import org.spartan.fajita.api.bnf.symbols.NonTerminal;
// import org.spartan.fajita.api.bnf.symbols.Terminal;
// import org.spartan.fajita.api.bnf.symbols.Type;
// import
// org.spartan.fajita.api.parser.ActionTable.ReduceReduceConflictException;
// import
// org.spartan.fajita.api.parser.ActionTable.ShiftReduceConflictException;
//
// public class ItemClosureTest {
// private enum Term implements Terminal {
// a, b, c, d;
// @Override public Type type() {
// return Type.VOID;
// }
// }
//
// private enum NT implements NonTerminal {
// S, A;
// }
//
// private enum NT2 implements NonTerminal {
// S, A, B, C;
// }
//
// @Test public void testInitialItemKept() throws ReduceReduceConflictException,
// ShiftReduceConflictException {
// BNF bnf = new BNFBuilder(Term.class, NT.class) //
// .startConfig() //
// .setApiNameTo("TEST") //
// .setStartSymbols(NT.S) //
// .endConfig() //
// .derive(NT.S).to(Term.a).and(Term.b) //
// .derive(NT.A).to(NT.A) //
// .finish();
// DerivationRule initialRule = findRule(bnf, lookahead -> lookahead.lhs == NT.S);
// Set<Item> expectedSet = expectedItemSet(initialRule, getAugmentedRule(bnf));
// assertEquals(expectedSet, new LRParser(bnf).getInitialState().items);
// }
// @Test public void testClosureOnDerivationRule() throws
// ReduceReduceConflictException, ShiftReduceConflictException {
// BNF bnf = new BNFBuilder(Term.class, NT.class) //
// .startConfig() //
// .setApiNameTo("TEST") //
// .setStartSymbols(NT.S) //
// .endConfig() //
// .derive(NT.S).to(NT.A).and(Term.b) //
// .derive(NT.A).to(Term.a).and(Term.c) //
// .finish();
// DerivationRule S_Rule = findRule(bnf, lookahead -> lookahead.lhs.equals(NT.S));
// DerivationRule A_Rule = findRule(bnf, lookahead -> lookahead.lhs.equals(NT.A));
// Set<Item> expectedSet = expectedItemSet(S_Rule, A_Rule,
// getAugmentedRule(bnf));
// assertEquals(expectedSet, new LRParser(bnf).getInitialState().items);
// }
// @Test public void testClosureOnInheritenceRule() throws
// ReduceReduceConflictException, ShiftReduceConflictException {
// BNF bnf = new BNFBuilder(Term.class, NT2.class) //
// .startConfig() //
// .setApiNameTo("TEST") //
// .setStartSymbols(NT2.S) //
// .endConfig() //
// .derive(NT2.S).to(NT2.A).or().to(NT2.B).or().to(NT2.C) //
// .derive(NT2.A).to(Term.a) //
// .derive(NT2.B).to(Term.b) //
// .derive(NT2.C).to(Term.c) //
// .finish();
// // fetching the rules
// DerivationRule StoA_Rule = findRule(bnf, lookahead -> lookahead.lhs.equals(NT2.S) &&
// lookahead.getChildren().get(0).equals(NT2.A));
// DerivationRule StoB_Rule = findRule(bnf, lookahead -> lookahead.lhs.equals(NT2.S) &&
// lookahead.getChildren().get(0).equals(NT2.B));
// DerivationRule StoC_Rule = findRule(bnf, lookahead -> lookahead.lhs.equals(NT2.S) &&
// lookahead.getChildren().get(0).equals(NT2.C));
// DerivationRule A_Rule = findRule(bnf, lookahead -> lookahead.lhs.equals(NT2.A));
// DerivationRule B_Rule = findRule(bnf, lookahead -> lookahead.lhs.equals(NT2.B));
// DerivationRule C_Rule = findRule(bnf, lookahead -> lookahead.lhs.equals(NT2.C));
// // test
// Set<Item> expectedSet = expectedItemSet(StoA_Rule, StoB_Rule, StoC_Rule,
// A_Rule, B_Rule, C_Rule, getAugmentedRule(bnf));
// assertEquals(expectedSet, new LRParser(bnf).getInitialState().items);
// }
// @SuppressWarnings("static-method") private DerivationRule
// getAugmentedRule(final BNF bnf) {
// return findRule(bnf, lookahead -> lookahead.lhs.equals(bnf.getAugmentedStartSymbol()));
// }
// @Test public void testIterativeClosure() throws
// ReduceReduceConflictException, ShiftReduceConflictException {
// BNF bnf = new BNFBuilder(Term.class, NT2.class) //
// .startConfig() //
// .setApiNameTo("TEST") //
// .setStartSymbols(NT2.S) //
// .endConfig() //
// .derive(NT2.S).to(NT2.A)//
// .derive(NT2.A).to(NT2.B).or().to(NT2.C) //
// .derive(NT2.B).to(Term.b) //
// .derive(NT2.C).to(Term.c) //
// .finish();
// // fetching the rules
// DerivationRule AtoB_Rule = findRule(bnf, lookahead -> lookahead.lhs.equals(NT2.A) &&
// lookahead.getChildren().get(0).equals(NT2.B));
// DerivationRule AtoC_Rule = findRule(bnf, lookahead -> lookahead.lhs.equals(NT2.A) &&
// lookahead.getChildren().get(0).equals(NT2.C));
// DerivationRule S_Rule = findRule(bnf, lookahead -> lookahead.lhs.equals(NT2.S));
// DerivationRule B_Rule = findRule(bnf, lookahead -> lookahead.lhs.equals(NT2.B));
// DerivationRule C_Rule = findRule(bnf, lookahead -> lookahead.lhs.equals(NT2.C));
// // test
// Set<Item> expectedSet = expectedItemSet(AtoB_Rule, AtoC_Rule, S_Rule, B_Rule,
// C_Rule, getAugmentedRule(bnf));
// assertEquals(expectedSet, new LRParser(bnf).getInitialState().items);
// }
// @SuppressWarnings("static-method") @Test public void
// testClosureWithMultipleStartVariables()
// throws ReduceReduceConflictException, ShiftReduceConflictException {
// BNF bnf = new BNFBuilder(Term.class, NT2.class) //
// .startConfig() //
// .setApiNameTo("TEST") //
// .setStartSymbols(NT2.S, NT2.A) //
// .endConfig() //
// .derive(NT2.S).to(NT2.B)//
// .derive(NT2.A).to(NT2.C) //
// .derive(NT2.B).to(Term.b)//
// .derive(NT2.C).to(Term.c)//
// .finish();
// // fetching the rules
// DerivationRule AtoC_Rule = findRule(bnf, lookahead -> lookahead.lhs.equals(NT2.A) &&
// lookahead.getChildren().get(0).equals(NT2.C));
// DerivationRule S_Rule = findRule(bnf, lookahead -> lookahead.lhs.equals(NT2.S));
// DerivationRule B_Rule = findRule(bnf, lookahead -> lookahead.lhs.equals(NT2.B));
// DerivationRule C_Rule = findRule(bnf, lookahead -> lookahead.lhs.equals(NT2.C));
// DerivationRule augmentedRule1 = findRule(bnf,
// lookahead -> lookahead.lhs.equals(bnf.getAugmentedStartSymbol()) &&
// lookahead.getChildren().get(0).equals(NT2.S));
// DerivationRule augmentedRule2 = findRule(bnf,
// lookahead -> lookahead.lhs.equals(bnf.getAugmentedStartSymbol()) &&
// lookahead.getChildren().get(0).equals(NT2.A));
// // test
// Set<Item> expectedSet = expectedItemSet(AtoC_Rule, S_Rule, B_Rule, C_Rule,
// augmentedRule1, augmentedRule2);
// assertEquals(expectedSet, new LRParser(bnf).getInitialState().items);
// }
// }
