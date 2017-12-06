package org.spartan.fajita.revision.symbols.extendibles;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.spartan.fajita.revision.parser.ell.Interpretation;
import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.Symbol;
import org.spartan.fajita.revision.symbols.Terminal;

import static java.util.stream.Collectors.toList;

public class OneOrMore extends BaseExtendible {
  private NonTerminal head2;

  public OneOrMore(List<Symbol> symbols) {
    super(symbols);
  }
  @Override protected void solve() {
    head = nonTerminal();
    head2 = nonTerminal();
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
  @Override public List<String> parseTypes(Function<Symbol, List<String>> operation) {
    List<String> $ = new LinkedList<>();
    for (Symbol s : symbols())
      for (String q : operation.apply(s))
        $.add(q + "[]");
    return $;
  }
  // TODO Roth: check whether some of this can be generalized
  @SuppressWarnings({ "rawtypes", "unchecked" }) @Override public List<?> fold(List<?> t) {
    assert isSolved && head != null && head2 != null && t.size() == 1;
    Object o = t.get(0);
    assert o instanceof Interpretation;
    Interpretation current = (Interpretation) o;
    assert head.equals(current.symbol) && current.value.size() > 0;
    List $ = new LinkedList<>();
    $.addAll(current.value.subList(0, current.value.size() - 1));
    Object l = current.value.get(current.value.size() - 1);
    assert l instanceof Interpretation;
    Interpretation li = (Interpretation) l;
    assert head2.equals(li.symbol);
    while (!li.value.isEmpty()) {
      $.addAll(li.value.subList(0, li.value.size() - 1));
      Object l2 = li.value.get(li.value.size() - 1);
      assert l2 instanceof Interpretation;
      li = (Interpretation) l2;
      assert head2.equals(li.symbol);
    }
    return $;
  }
  @SuppressWarnings({ "rawtypes", "unchecked" }) @Override public List<Object[]> conclude(List<?> values,
      BiFunction<Object, List<?>, Object> solution) {
    List<List> solved = new LinkedList<>();
    int currentSymbol = 0;
    for (Object o : values) {
      if (solved.size() < currentSymbol + 1)
        solved.add(new LinkedList<>());
      Interpretation i = (Interpretation) o;
      assert i.symbol.equals(symbols.get(currentSymbol));
      solved.get(currentSymbol).add(solution.apply(i.symbol, i.value));
      ++currentSymbol;
      if (currentSymbol == symbols.size())
        currentSymbol = 0;
    }
    return solved.stream().map(l -> l.toArray(new Object[l.size()])).collect(toList());
  }
}
