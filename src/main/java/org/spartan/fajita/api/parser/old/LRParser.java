package org.spartan.fajita.api.parser.old;

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
import org.spartan.fajita.api.parser.old.ActionTable.Accept;
import org.spartan.fajita.api.parser.old.ActionTable.Action;
import org.spartan.fajita.api.parser.old.ActionTable.Reduce;
import org.spartan.fajita.api.parser.old.ActionTable.Shift;

/**
 * Algorithms in this class are taken from
 * "COMPILERS - PRINCIPLES, TECHNIQUES & TOOLS" \ AHO, LAM, SETHI, ULLMAN
 * 
 * @author Tomer
 *
 */
public class LRParser {
  public final BNF bnf;
  private final List<State<Item>> states;
  private final ActionTable actionTable;
  private final Set<NonTerminal> nullableSymbols;
  private final Map<Symbol, Set<Verb>> baseFirstSets;
//  private final Map<NonTerminal, Set<Verb>> followSets;

  public LRParser(final BNF bnf) {
    this.bnf = bnf;
    nullableSymbols = calculateNullableSymbols();
    baseFirstSets = calculateSymbolFirstSet();
//    followSets = calculateFollowSets();
    states = new ArrayList<>();
    generateStatesSet();
    actionTable = new ActionTable(getStates());
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
//  private Map<NonTerminal, Set<Verb>> calculateFollowSets() {
//    Map<NonTerminal, Set<Verb>> $ = new HashMap<>();
//    // initialization
//    for (NonTerminal nt : bnf.getNonTerminals())
//      $.put(nt, new HashSet<>());
//    $.get(SpecialSymbols.augmentedStartSymbol).add(SpecialSymbols.$);
//    // iterative step
//    boolean moreChanges;
//    do {
//      moreChanges = false;
//      for (DerivationRule dRule : bnf.getRules())
//        for (int i = 0; i < dRule.getChildren().size(); i++) {
//          if (!dRule.getChildren().get(i).isNonTerminal())
//            continue;
//          Symbol subExpression[] = subExpressionBuilder(dRule.getChildren(), i + 1);
//          Set<Verb> ntFollowSet = $.get(dRule.getChildren().get(i));
//          moreChanges |= ntFollowSet.addAll(firstSetOf(subExpression));
//          if (isNullable(subExpression))
//            moreChanges |= ntFollowSet.addAll($.get(dRule.lhs));
//        }
//    } while (moreChanges);
//    return $;
//  }
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
//  public Set<Verb> followSetOf(final NonTerminal nt) {
//    return followSets.get(nt);
//  }
  private void fillParsingTable() {
    for (State<Item> state : getStates())
      for (Item item : state.getItems())
        if (item.readyToReduce())
          if (item.rule.lhs.equals(SpecialSymbols.augmentedStartSymbol) && item.lookahead.equals(SpecialSymbols.$))
            addAcceptAction(state);
          else
            addReduceAction(state, item);
        else if (item.rule.getChildren().get(item.dotIndex).isVerb())
          addShiftAction(state, item);
  }
  private void addAcceptAction(final State<Item> state) {
    actionTable.set(state, SpecialSymbols.$, new Accept());
  }
  private void addShiftAction(final State<Item> state, final Item item) {
    Verb nextTerminal = (Verb) item.rule.getChildren().get(item.dotIndex);
    State<Item> shift = state.goTo(nextTerminal);
    actionTable.set(state, nextTerminal, new Shift(shift));
  }
  private void addReduceAction(final State<Item> state, final Item item) {
    actionTable.set(state, item.lookahead, new Reduce(item));
  }
  private State<Item> generateInitialState() {
    Set<Item> initialItems = bnf.getRulesOf(SpecialSymbols.augmentedStartSymbol) //
        .stream().map(r -> new Item(r, SpecialSymbols.$, 0)) //
        .collect(Collectors.toSet());
    Set<Item> closure = calculateClosure(initialItems);
    return new State<>(closure, bnf, 0);
  }
  private Set<Item> calculateClosure(final Set<Item> initialItems) {
    Set<Item> items = new HashSet<>(initialItems);
    boolean moreChanges;
    do {
      moreChanges = false;
      List<Item> dotBeforeNT = items.stream()
          .filter(item -> (!item.readyToReduce()) && item.rule.getChildren().get(item.dotIndex).isNonTerminal()) //
          .sorted((i1, i2) -> i1.toString().compareTo(i2.toString())).distinct().collect(Collectors.toList());
      for (Item item : dotBeforeNT) {
        NonTerminal nt = (NonTerminal) item.rule.getChildren().get(item.dotIndex);
        for (DerivationRule dRule : bnf.getRulesOf(nt))
          for (Verb t : firstSetOf(LRParser.subExpressionBuilder(item.rule.getChildren(), item.dotIndex + 1, item.lookahead)))
            moreChanges |= items.add(new Item(dRule, t, 0));
      }
    } while (moreChanges);
    return items;
  }
  private void generateStatesSet() {
    State<Item> initialState = generateInitialState();
    List<Symbol> symbols = new ArrayList<>(legalSymbols());
    states.add(initialState);
    Stack<State<Item>> statesToCheck = new Stack<>();
    statesToCheck.push(initialState);
    while (!statesToCheck.isEmpty()) {
      State<Item> state = statesToCheck.pop();
      for (Symbol lookahead : symbols) {
        if (!state.isLegalTransition(lookahead))
          continue;
        State<Item> newState = generateNextState(state, lookahead);
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
  private State<Item> generateNextState(final State<Item> state, final Symbol lookahead) {
    if (lookahead == SpecialSymbols.$)
      if (state.getItems().stream().anyMatch(i -> i.readyToReduce() && i.rule.lhs.equals(SpecialSymbols.augmentedStartSymbol)))
        return new AcceptState<>(bnf);
    Set<Item> initialItems = state.getItems().stream().//
        filter(item -> item.isLegalTransition(lookahead)) //
        .map(item -> item.advance()) //
        .collect(Collectors.toSet());
    Set<Item> closure = calculateClosure(initialItems);
    return new State<>(closure, bnf, getStates().size());
  }
  public Action actionTable(final State<Item> state, final Verb lookahead) {
    return actionTable.get(state.index, lookahead);
  }
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
    for (State<Item> state : states)
      $ += state.extendedToString() + System.lineSeparator();
    $ += actionTable.toString();
    return $;
  }
  public List<State<Item>> getStates() {
    return states;
  }
}
