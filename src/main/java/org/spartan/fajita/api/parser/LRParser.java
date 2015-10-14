package org.spartan.fajita.api.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.rules.DerivationRule;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Symbol;
import org.spartan.fajita.api.bnf.symbols.Terminal;
import org.spartan.fajita.api.parser.ActionTable.Accept;
import org.spartan.fajita.api.parser.ActionTable.Action;
import org.spartan.fajita.api.parser.ActionTable.Reduce;
import org.spartan.fajita.api.parser.ActionTable.Shift;

/**
 * Algorithms in this class are taken from
 * "COMPILERS - PRINCIPLES, TECHNIQUES & TOOLS" \ AHO, LAM, SETHI, ULLMAN
 * 
 * @author Tomer
 *
 */
public class LRParser {
  public final BNF bnf;
  private final List<State> states;
  private final ActionTable actionTable;
  private final Map<Symbol, Set<Terminal>> baseFirstSets;
  private final Map<NonTerminal, Set<Terminal>> followSets;

  public LRParser(final BNF bnf) {
    this.bnf = bnf;
    baseFirstSets = calculateSymbolFirstSet();
    followSets = calculateFollowSets();
    states = new ArrayList<>();
    generateStatesSet();
    actionTable = new ActionTable(getStates());
    fillParsingTable();
  }
  private Map<Symbol, Set<Terminal>> calculateSymbolFirstSet() {
    Map<Symbol, Set<Terminal>> $ = new HashMap<>();
    for (NonTerminal nt : bnf.getNonTerminals())
      $.put(nt, new HashSet<>());
    for (Terminal term : bnf.getTerminals())
      $.put(term, new HashSet<>(Arrays.asList(term)));
    boolean moreChanges;
    do {
      moreChanges = false;
      for (DerivationRule dRule : bnf.getRules())
        for (Symbol symbol : dRule.getChildren()) {
          moreChanges |= $.get(dRule.lhs).addAll($.get(symbol));
          if (!isNullable(symbol))
            break;
        }
    } while (moreChanges);
    return $;
  }
  private Map<NonTerminal, Set<Terminal>> calculateFollowSets() {
    Map<NonTerminal, Set<Terminal>> $ = new HashMap<>();
    // initialization
    for (NonTerminal nt : bnf.getNonTerminals())
      $.put(nt, new HashSet<>());
    $.get(bnf.getAugmentedStartSymbol()).add(Terminal.$);
    // iterative step
    boolean moreChanges;
    do {
      moreChanges = false;
      for (DerivationRule dRule : bnf.getRules())
        for (int i = 0; i < dRule.getChildren().size(); i++) {
          if (dRule.getChildren().get(i).isTerminal())
            continue;
          Symbol subExpression[] = subExpressionBuilder(dRule.getChildren(), i + 1);
          moreChanges |= $.get(dRule.getChildren().get(i)).addAll(firstSetOf(subExpression));
          if (isNullable(subExpression))
            moreChanges |= $.get(dRule.getChildren().get(i)).addAll($.get(dRule.lhs));
        }
    } while (moreChanges);
    return $;
  }
  public static Symbol[] subExpressionBuilder(final List<Symbol> expression, final int index, final Symbol... symbols) {
    Symbol[] $ = new Symbol[expression.size() - index + symbols.length];
    for (int i = 0; i < expression.size() - index; i++)
      $[i] = expression.get(i + index);
    for (int i = 0; i < symbols.length; i++)
      $[expression.size() - index + i] = symbols[i];
    return $;
  }
  @SuppressWarnings({ "static-method" }) public boolean isNullable(final Symbol... expression) {
    return expression.length == 0;
  }
  public Set<Terminal> firstSetOf(final Symbol... expression) {
    HashSet<Terminal> $ = new HashSet<>();
    for (Symbol symbol : expression) {
      $.addAll(baseFirstSets.get(symbol));
      if (!isNullable(symbol))
        break;
    }
    return $;
  }
  public Set<Terminal> followSetOf(final NonTerminal nt) {
    return followSets.get(nt);
  }
  private void fillParsingTable() {
    for (State state : getStates())
      for (Item item : state.items)
        if (item.readyToReduce())
          if (item.rule.lhs.equals(bnf.getAugmentedStartSymbol()) && item.lookahead.equals(Terminal.$))
            addAcceptAction(state);
          else
            addReduceAction(state, item);
        else if (item.rule.getChildren().get(item.dotIndex).isTerminal())
          addShiftAction(state, item);
  }
  private void addAcceptAction(final State state) {
    actionTable.set(state, Terminal.$, new Accept());
  }
  private void addShiftAction(final State state, final Item item) {
    Terminal nextTerminal = (Terminal) item.rule.getChildren().get(item.dotIndex);
    Integer nextState = state.goTo(nextTerminal);
    actionTable.set(state, nextTerminal, new Shift(nextState.intValue()));
  }
  private void addReduceAction(final State state, final Item item) {
    actionTable.set(state, item.lookahead, new Reduce());
  }
  public State getInitialState() {
    return getStates().get(0);
  }
  private State generateInitialState() {
    Set<Item> initialItems = bnf.getRules().stream() //
        .filter(dRule -> bnf.getAugmentedStartSymbol().equals(dRule.lhs))//
        .map(dRule -> new Item(dRule, Terminal.$, 0)) //
        .collect(Collectors.toSet());
    Set<Item> closure = calculateClosure(initialItems);
    return new State(closure, bnf);
  }
  private Set<Item> calculateClosure(final Set<Item> initialItems) {
    Set<Item> items = new HashSet<>(initialItems);
    boolean moreChanges;
    do {
      moreChanges = false;
      Set<Item> dotBeforeNT = items.stream()
          .filter(item -> (!item.readyToReduce()) && item.rule.getChildren().get(item.dotIndex).isNonTerminal()) //
          .collect(Collectors.toSet());
      for (Item item : dotBeforeNT) {
        NonTerminal nt = (NonTerminal) item.rule.getChildren().get(item.dotIndex);
        for (DerivationRule dRule : bnf.getRules().stream().filter(r -> r.lhs.equals(nt)).collect(Collectors.toList()))
          for (Terminal t : firstSetOf(LRParser.subExpressionBuilder(item.rule.getChildren(), item.dotIndex + 1, item.lookahead)))
            moreChanges |= items.add(new Item(dRule, t, 0));
      }
    } while (moreChanges);
    return items;
  }
  private void generateStatesSet() {
    State initialState = generateInitialState();
    Set<Symbol> symbols = legalSymbols();
    getStates().add(initialState);
    Stack<State> statesToCheck = new Stack<>();
    statesToCheck.push(initialState);
    while (!statesToCheck.isEmpty()) {
      State state = statesToCheck.pop();
      for (Symbol lookahead : symbols) {
        if (!state.isLegalLookahead(lookahead))
          continue;
        State newState = generateNextState(state, lookahead);
        int stateIndex = getStates().indexOf(newState);
        if (stateIndex == -1 && newState.getClass() != AcceptState.class) {
          getStates().add(newState);
          statesToCheck.add(newState);
          state.addGotoTransition(lookahead, newState.stateIndex);
        } else
          state.addGotoTransition(lookahead, stateIndex);
      }
    }
  }
  private State generateNextState(final State state, final Symbol lookahead) {
    if (lookahead == Terminal.$)
      if (state.items.stream().anyMatch(i -> i.readyToReduce() && bnf.getAugmentedStartSymbol().equals(i.rule.lhs)))
        return new AcceptState(bnf, getStates().size());
    Set<Item> initialItems = state.items.stream().//
        filter(item -> item.isLegalLookahead(lookahead)) //
        .map(item -> item.advance()) //
        .collect(Collectors.toSet());
    Set<Item> closure = calculateClosure(initialItems);
    return new State(closure, bnf, getStates().size());
  }
  public State gotoTable(final State state, final Symbol lookahead) {
    Integer nextState = state.goTo(lookahead);
    if (nextState == null)
      return null;
    return getStates().get(nextState.intValue());
  }
  public Action actionTable(final State state, final Terminal lookahead) {
    return actionTable.get(state.stateIndex, lookahead);
  }
  private Set<Symbol> legalSymbols() {
    Set<Symbol> notAllowed = new HashSet<>();
    Set<Symbol> symbols = new HashSet<>();
    symbols.addAll(bnf.getNonTerminals());
    symbols.addAll(bnf.getTerminals());
    symbols.removeAll(notAllowed);
    return symbols;
  }
  @Override public String toString() {
    String $ = "States:" + System.lineSeparator();
    for (State state : states)
      $ += state.extentedToString() + System.lineSeparator();
    $ += actionTable.toString();
    return $;
  }
  public List<State> getStates() {
    return states;
  }
  public State getState(final int index) {
    return states.get(index);
  }
  public State getState(final State prevState, final Symbol lookahead) {
    return states.get(prevState.goTo(lookahead).intValue());
  }
}
