package org.spartan.fajita.api.parser;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.rules.DerivationRule;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.SpecialSymbols;
import org.spartan.fajita.api.bnf.symbols.Symbol;
import org.spartan.fajita.api.bnf.symbols.Verb;
import org.spartan.fajita.api.parser.JActionTable.Action;
import org.spartan.fajita.api.parser.JActionTable.Jump;
import org.spartan.fajita.api.parser.JActionTable.Accept;
import org.spartan.fajita.api.parser.JActionTable.Shift;
import org.spartan.fajita.api.parser.old.AcceptState;
import org.spartan.fajita.api.parser.old.State;
import org.spartan.fajita.api.parser.stack.JItem;

public class JLRParser {
  public final BNF bnf;
  private final List<State<JItem>> states;
  private int addressesNumber;
  private final JActionTable actionTable;
  private final Set<NonTerminal> nullableSymbols;
  private final Map<Symbol, Set<Verb>> baseFirstSets;

  //
  public JLRParser(final BNF bnf) {
    this.bnf = bnf;
    addressesNumber = 0;
    nullableSymbols = calculateNullableSymbols();
    baseFirstSets = calculateSymbolFirstSet();
    states = new ArrayList<>();
    generateStatesSet();
    actionTable = new JActionTable(getStates());
    fillParsingTable();
  }
  private Set<NonTerminal> calculateNullableSymbols() {
    Set<NonTerminal> nullables = new HashSet<>();
    boolean moreChanges;
    do {
      moreChanges = false;
      for (DerivationRule rule : bnf.getRules())
        if (rule.getChildren().stream().allMatch(child -> nullables.contains(child) || child.equals(SpecialSymbols.epsilon)))
          moreChanges = nullables.add(rule.lhs);
    } while (moreChanges);
    return nullables;
  }
  private Map<Symbol, Set<Verb>> calculateSymbolFirstSet() {
    Map<Symbol, Set<Verb>> $ = new HashMap<>();
    for (NonTerminal nt : bnf.getNonTerminals())
      $.put(nt, new LinkedHashSet<>());
    for (Verb term : bnf.getVerbs())
      $.put(term, new LinkedHashSet<>(Arrays.asList(term)));
    boolean moreChanges;
    do {
      moreChanges = false;
      for (DerivationRule dRule : bnf.getRules())
        for (Symbol symbol : dRule.getChildren()) {
          moreChanges |= $.get(dRule.lhs).addAll($.getOrDefault(symbol, new HashSet<>()));
          if (!isNullable(symbol))
            break;
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
  public boolean isNullable(final Symbol... expression) {
    return Arrays.asList(expression).stream()
        .allMatch(symbol -> nullableSymbols.contains(symbol) || symbol == SpecialSymbols.epsilon);
  }
  public List<Verb> firstSetOf(final Symbol... expression) {
    List<Verb> $ = new ArrayList<>();
    for (Symbol symbol : expression) {
      $.addAll(baseFirstSets.get(symbol));
      if (!isNullable(symbol))
        break;
    }
    return $;
  }
  private void fillParsingTable() {
    for (State<JItem> state : getStates()){
      if(state.getItems().stream().anyMatch(i -> i.readyToReduce() && i.lookahead.equals(SpecialSymbols.$)))
        addAcceptAction(state);
      for(Verb v : bnf.getVerbs()){
        
      }
    }
  }
  private void addAcceptAction(final State<JItem> state) {
    actionTable.set(state, SpecialSymbols.$, new Accept());
  }
  private void addShiftAction(final State<JItem> state, final JItem item) {
    Verb nextTerminal = (Verb) item.rule.getChildren().get(item.dotIndex);
    State<JItem> shift = state.goTo(nextTerminal);
    actionTable.set(state, nextTerminal, new Shift(shift));
  }
  private void addJumpAction(final State<JItem> state, final JItem item) {
    actionTable.set(state, item.lookahead, new Jump(item));
  }
  private State<JItem> generateInitialState() {
    Set<JItem> initialItems = bnf.getRulesOf(SpecialSymbols.augmentedStartSymbol) //
        .stream().map(r -> new JItem(r, SpecialSymbols.$, 0, addressesNumber++)) //
        .collect(Collectors.toSet());
    Set<JItem> closure = calculateClosure(initialItems);
    return new State<>(closure, bnf, 0);
  }
  private Set<JItem> calculateClosure(final Set<JItem> initialItems) {
    HashSet<JItem> $ = new HashSet<>();
    Stack<JItem> todo = new Stack<>();
    todo.addAll(initialItems);
    do {
      JItem item = todo.pop();
      if (!$.add(item) || item.readyToReduce() || !item.rule.getChildren().get(item.dotIndex).isNonTerminal())
        continue;
      NonTerminal nt = (NonTerminal) item.rule.getChildren().get(item.dotIndex);
      final Symbol[] stringAfterDot = Arrays.copyOfRange(item.rule.getChildren().toArray(new Symbol[] {}), item.dotIndex + 1,
          item.rule.getChildren().size());
      int newAddr = addressesNumber;
      boolean newAddrUsed = false;
      for (DerivationRule dRule : bnf.getRulesOf(nt)) {
        for (Verb t : firstSetOf(stringAfterDot)) {
          todo.add(new JItem(dRule, t, 0, newAddr));
          newAddrUsed = true;
        }
        if (isNullable(stringAfterDot))
          todo.add(new JItem(dRule, item.lookahead, 0, item.address));
      }
      if (newAddrUsed)
        addressesNumber++;
    } while (!todo.isEmpty());
    return $;
  }
  private void generateStatesSet() {
    State<JItem> initialState = generateInitialState();
    List<Symbol> symbols = new ArrayList<>(legalSymbols());
    states.add(initialState);
    Stack<State<JItem>> statesToCheck = new Stack<>();
    statesToCheck.push(initialState);
    while (!statesToCheck.isEmpty()) {
      State<JItem> state = statesToCheck.pop();
      for (Symbol lookahead : symbols) {
        if (!state.isLegalTransition(lookahead))
          continue;
        State<JItem> newState = generateNextState(state, lookahead);
        int stateIndex = states.indexOf(newState);
        if (stateIndex == -1) {
          if (newState.getClass() != AcceptState.class) {
            states.add(newState);
            statesToCheck.add(newState);
          }
          state.addGotoTransition(lookahead, newState);
        } else
          state.addGotoTransition(lookahead, getStates().get(stateIndex));
      }
    }
  }
  private State<JItem> generateNextState(final State<JItem> state, final Symbol lookahead) {
    if (lookahead == SpecialSymbols.$)
      if (state.getItems().stream().anyMatch(i -> i.readyToReduce() && i.rule.lhs.equals(SpecialSymbols.augmentedStartSymbol)))
        return new AcceptState<>(bnf);
    Set<JItem> initialItems = state.getItems().stream().//
        filter(item -> item.isLegalTransition(lookahead)) //
        .map(item -> (JItem) item.advance()) //
        .collect(Collectors.toSet());
    Set<JItem> closure = calculateClosure(initialItems);
    return new State<>(closure, bnf, getStates().size());
  }
  // public Action actionTable(final State<JItem> state, final Verb lookahead) {
  // return actionTable.get(state.index, lookahead);
  // }
  private Set<Symbol> legalSymbols() {
    Set<Symbol> notAllowed = new HashSet<>();
    Set<Symbol> symbols = new HashSet<>();
    symbols.addAll(bnf.getNonTerminals());
    symbols.addAll(bnf.getVerbs());
    symbols.removeAll(notAllowed);
    return symbols;
  }
  @Override public String toString() {
    String $ = "States:" + System.lineSeparator();
    for (State<JItem> state : states)
      $ += state.extendedToString() + System.lineSeparator();
    // $ += actionTable.toString();
    return $;
  }
  public List<State<JItem>> getStates() {
    return states;
  }
}
