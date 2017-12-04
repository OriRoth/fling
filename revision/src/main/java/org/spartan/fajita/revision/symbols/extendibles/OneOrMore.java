package org.spartan.fajita.revision.symbols.extendibles;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.Symbol;
import org.spartan.fajita.revision.symbols.Terminal;

public class OneOrMore extends BaseExtendible {
  public OneOrMore(List<Symbol> symbols) {
    super(symbols);
  }
  @Override protected void solve() {
    head = nonTerminal();
    NonTerminal head2 = nonTerminal();
    symbols = solve(symbols);
    List<Symbol> rhs1 = new ArrayList<>(symbols);
    rhs1.add(head2);
    addRule(head, rhs1);
    List<Symbol> rhs2 = new ArrayList<>(symbols);
    rhs2.add(head2);
    addRule(head2, rhs2);
    addRule(head2, new LinkedList<>());
  }
  @Override public boolean isNullable(Set<Symbol> knownNullables) {
    return symbols.stream().allMatch(x -> knownNullables.contains(x));
  }
  @Override public Set<Terminal> getFirstSet(Set<Symbol> nullables, Map<Symbol, Set<Terminal>> knownFirstSets) {
    Set<Terminal> $ = new HashSet<>();
    for (Symbol s : symbols) {
      if (knownFirstSets.containsKey(s))
        $.addAll(knownFirstSets.get(s));
      if (!nullables.contains(s))
        break;
    }
    return $;
  }
}
