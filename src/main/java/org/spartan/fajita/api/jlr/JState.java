package org.spartan.fajita.api.jlr;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.rules.DerivationRule;
import org.spartan.fajita.api.bnf.symbols.SpecialSymbols;
import org.spartan.fajita.api.bnf.symbols.Symbol;
import org.spartan.fajita.api.bnf.symbols.Verb;

public class JState {
  private final Set<JItem> items;
  private final BNF bnf;
  private final Map<Symbol, JState> transitions;
  public final int index;
  public final String name;

  public JState(final Set<JItem> items, final BNF bnf, final int stateIndex) {
    this.items = items;
    index = stateIndex;
    this.bnf = bnf;
    transitions = new HashMap<>();
    name = "Q" + stateIndex;
  }
  public void addGotoTransition(final Symbol symbol, final JState newState) {
    transitions.put(symbol, newState);
  }
  public boolean isLegalTransition(final Symbol lookahead) {
    if (lookahead.equals(SpecialSymbols.$))
      return getItems().stream().anyMatch(i -> i.readyToReduce() && i.rule.lhs.equals(SpecialSymbols.augmentedStartSymbol));
    return transitions.containsKey(lookahead) || getItems().stream().anyMatch(item -> item.isLegalTransition(lookahead));
  }
  public boolean isLegalReduce(final Symbol lookahead) {
    return lookahead.isVerb() && getItems().stream().anyMatch(item -> item.isLegalReduce((Verb) lookahead));
  }
  public Set<Symbol> allLegalTransitions() {
    return transitions.keySet();
  }
  @Override public String toString() {
    return name + ":" + System.lineSeparator() + extendedToString();
    // return name;
  }
  @SuppressWarnings("boxing") public String extendedToString() {
    String $ = "";
    Set<SimpleEntry<DerivationRule, Integer>> noLookaheads = getItems().stream()
        .map(item -> new SimpleEntry<>(item.rule, item.dotIndex)).distinct().collect(Collectors.toSet());
    for (SimpleEntry<DerivationRule, Integer> item : noLookaheads) {
      List<JItem> matching = getItems().stream()
          .filter(i -> i.rule.equals(item.getKey()) && i.dotIndex == item.getValue().intValue()).collect(Collectors.toList());
      for (int i = 0; i < matching.size(); i++) {
        JItem match = matching.get(i);
        if (i == 0)
          $ += "[" + match.toString();
        else
          $ += "/" + match.lookahead.toString() + "," + match.labelToString() + "," + match.isNewLabelToString();
      }
      $ += "]" + System.lineSeparator();
    }
    return $;
  }
  public String compactToString() {
    String $ = "{";
    for (JItem item : getItems().stream()
        .filter(item -> (item.dotIndex != 0 || SpecialSymbols.augmentedStartSymbol == item.rule.lhs)).collect(Collectors.toList()))
      $ += item.toString() + ",";
    return $ + "}";
  }
  @Override public boolean equals(final Object obj) {
    if (obj.getClass() != JState.class)
      return false;
    JState s = (JState) obj;
    return bnf.equals(s.bnf)
        && s.getKernelItems().stream().allMatch(ki -> getKernelItems().stream().anyMatch(ki2 -> ki.strongEquals(ki2)));
  }
  /**
   * @param lookahead
   *          - the transition symbol
   * @return the index of the next state or -1 if the transition does not exist.
   * 
   */
  public JState goTo(final Symbol lookahead) {
    JState next = transitions.get(lookahead);
    if (next == null)
      throw new IllegalStateException("Algorithm fault. for some reason q.goto().goto() failed.");
    return next;
  }
  @Override public int hashCode() {
    return getItems().hashCode() + bnf.hashCode();
  }
  public Set<JItem> getItems() {
    return items;
  }
  private Set<JItem> getKernelItems() {
    return items.stream().filter(i -> i.kernel).collect(Collectors.toSet());
  }
  public boolean isInitial() {
    return index == 0;
  }
}