package org.spartan.fajita.api.parser;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.symbols.Symbol;
import org.spartan.fajita.api.bnf.symbols.Terminal;

public class State {
  public final Set<Item> items;
  public final BNF bnf;
  private final Map<Symbol, Integer> transitions;
  public final int stateIndex;

  State(final Set<Item> items, final BNF bnf) {
    this(items, bnf, 0);
  }
  State(final Set<Item> items, final BNF bnf, final int stateIndex) {
    this.items = items;
    this.stateIndex = stateIndex;
    this.bnf = bnf;
    transitions = new HashMap<>();
  }
  void addGotoTransition(final Symbol symbol, final int stateIdx) {
    transitions.put(symbol, new Integer(stateIdx));
  }
  public boolean isLegalLookahead(final Symbol lookahead) {
    if (lookahead == Terminal.$)
      return items.stream().anyMatch(i -> i.readyToReduce() && bnf.getAugmentedStartSymbol().equals(i.rule.lhs));
    return transitions.containsKey(lookahead) || items.stream().anyMatch(item -> item.isLegalLookahead(lookahead));
  }
  public Set<Symbol> allLegalLookaheads() {
    return transitions.keySet();
  }
  @Override public String toString() {
    // return "q" + stateIndex + ":" + compactToString() + " " +
    // transitions.toString();
    return "Q" + stateIndex;
  }
  public String extentedToString() {
    String $ = "{";
    for (Item item : items)
      $ += item.toString() + ",";
    $ += "} ";
    return $;
  }
  public String compactToString() {
    String $ = "{";
    for (Item item : items.stream().filter(item -> (item.dotIndex != 0 || bnf.getAugmentedStartSymbol() == item.rule.lhs))
        .collect(Collectors.toList()))
      $ += item.toString() + ",";
    return $ + "}";
  }
  @Override public boolean equals(final Object obj) {
    if (obj.getClass() != State.class)
      return false;
    State s = (State) obj;
    return bnf.equals(s.bnf) && s.items.equals(items);
  }
  /**
   * @param lookahead
   *          - the transition symbol
   * @return the index of the next state or -1 if the transition does not exist.
   * 
   */
  public Integer goTo(final Symbol lookahead) {
    return transitions.getOrDefault(lookahead, new Integer(-1));
  }
  @Override public int hashCode() {
    return items.hashCode() + bnf.hashCode();
  }
}
