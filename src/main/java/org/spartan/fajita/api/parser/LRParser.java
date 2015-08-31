package org.spartan.fajita.api.parser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.rules.DerivationRule;
import org.spartan.fajita.api.bnf.rules.InheritenceRule;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Symbol;
import org.spartan.fajita.api.bnf.symbols.Terminal;
import org.spartan.fajita.api.parser.ParsingTable.Accept;
import org.spartan.fajita.api.parser.ParsingTable.Action;
import org.spartan.fajita.api.parser.ParsingTable.Reduce;
import org.spartan.fajita.api.parser.ParsingTable.ReduceReduceConflictException;
import org.spartan.fajita.api.parser.ParsingTable.Shift;
import org.spartan.fajita.api.parser.ParsingTable.ShiftReduceConflictException;

public class LRParser {

    private final BNF bnf;
    public final List<State> states;

    private final ParsingTable parsingTable;

    public LRParser(final BNF bnf) throws ReduceReduceConflictException, ShiftReduceConflictException {
	this.bnf = bnf;
	states = new ArrayList<>();
	generateStatesSet();
	parsingTable = new ParsingTable(bnf.getTerminals(), states);
	fillParsingTable();

    }

    private void fillParsingTable() throws ReduceReduceConflictException, ShiftReduceConflictException {
	for (State state : states)
	    for (Item item : state.items)
		if (item.readyToReduce())
		    if (item.rule.lhs.equals(bnf.getAugmentedStartSymbol()))
			addAcceptAction(state, item);
		    else
			addReduceAction(state, item);
		else if (item.rule.getChildren().get(item.dotIndex).isTerminal())
		    addShiftAction(state, item);
    }

    private void addAcceptAction(final State state, final Item item)
	    throws ReduceReduceConflictException, ShiftReduceConflictException {
	parsingTable.set(state, Terminal.$, new Accept());
    }

    private void addShiftAction(final State state, final Item item)
	    throws ReduceReduceConflictException, ShiftReduceConflictException {
	Terminal nextTerminal = (Terminal) item.rule.getChildren().get(item.dotIndex);
	Integer nextState = state.goTo(nextTerminal);
	parsingTable.set(state, nextTerminal, new Shift(nextState));
    }

    private void addReduceAction(final State state, final Item item)
	    throws ReduceReduceConflictException, ShiftReduceConflictException {
	for (Terminal t : bnf.followSetOf(item.rule.lhs))
	    parsingTable.set(state, t, new Reduce());
    }

    public State getInitialState() {
	return states.get(0);
    }

    private State generateInitialState() {
	// initial variable derived with inheritence rule
	Set<Item> initialItems = bnf.getInheritenceRules().stream() //
		.filter(iRule -> bnf.getAugmentedStartSymbol().equals(iRule.lhs))//
		.flatMap(iRule -> iRule.getAsDerivationRules().stream())//
		.map(dRule -> new Item(dRule, 0)) //
		.collect(Collectors.toSet());

	Set<Item> closure = calculateClosure(initialItems);
	return new State(closure, bnf);
    }

    private Set<Item> calculateClosure(final Set<Item> initialItems) {
	Set<Item> items = new HashSet<>(initialItems);
	boolean moreChanges;
	do {
	    moreChanges = false;
	    Set<NonTerminal> dotBeforeNT = items.stream()
		    .filter(item -> (!item.readyToReduce()) && NonTerminal.class
			    .isAssignableFrom(item.rule.getChildren().get(item.dotIndex).getClass())) //
		    .map(item -> (NonTerminal) item.rule.getChildren().get(item.dotIndex)) //
		    .collect(Collectors.toSet());
	    ;
	    for (NonTerminal nt : dotBeforeNT) {
		for (DerivationRule dRule : bnf.getDerivationRules()) {
		    if (!dRule.lhs.equals(nt))
			continue;
		    moreChanges |= items.add(new Item(dRule, 0));
		}

		for (InheritenceRule iRule : bnf.getInheritenceRules()) {
		    if (!iRule.lhs.equals(nt))
			continue;
		    for (DerivationRule dRule : iRule.getAsDerivationRules())
			moreChanges |= items.add(new Item(dRule, 0));
		}
	    }
	} while (moreChanges);
	return items;
    }

    private void generateStatesSet() {
	State initialState = generateInitialState();
	Set<Symbol> symbols = legalSymbols();
	states.add(initialState);

	Stack<State> statesToCheck = new Stack<>();
	statesToCheck.push(initialState);

	while (!statesToCheck.isEmpty()) {
	    State state = statesToCheck.pop();
	    for (Symbol lookahead : symbols) {
		if (!state.isLegalLookahead(lookahead))
		    continue;
		State newState = generateNextState(state, lookahead);
		int stateIndex = states.indexOf(newState);
		if (stateIndex == -1) {
		    states.add(newState);
		    statesToCheck.add(newState);
		    state.addGotoTransition(lookahead, newState.stateIndex);
		} else
		    state.addGotoTransition(lookahead, stateIndex);

	    }
	}
    }

    private State generateNextState(final State state, final Symbol lookahead) {
	if (lookahead == Terminal.$)
	    if (state.items.stream()
		    .anyMatch(i -> i.readyToReduce() && bnf.getAugmentedStartSymbol().equals(i.rule.lhs)))
		return new AcceptState(bnf, states.size());
	Set<Item> initialItems = state.items.stream().//
		filter(item -> item.isLegalLookahead(lookahead)) //
		.map(item -> item.advance()) //
		.collect(Collectors.toSet());

	Set<Item> closure = calculateClosure(initialItems);
	return new State(closure, bnf, states.size());
    }

    public State gotoTable(final State state, final Symbol lookahead) {
	Integer nextState = state.goTo(lookahead);
	if (nextState == null)
	    return null;
	return states.get(nextState);
    }

    public Action actionTable(final State state, final Terminal lookahead) {
	return parsingTable.get(state.stateIndex, lookahead);
    }

    private Set<Symbol> legalSymbols() {
	Set<Symbol> notAllowed = new HashSet<>();
	notAllowed.add(NonTerminal.EPSILON);
	notAllowed.add(Terminal.epsilon);

	Set<Symbol> symbols = new HashSet<>();
	symbols.addAll(bnf.getNonTerminals());
	symbols.addAll(bnf.getTerminals());
	symbols.removeAll(notAllowed);
	return symbols;
    }
}
