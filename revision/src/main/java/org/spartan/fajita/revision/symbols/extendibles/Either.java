package org.spartan.fajita.revision.symbols.extendibles;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.spartan.fajita.revision.parser.ell.Interpretation;
import org.spartan.fajita.revision.symbols.Symbol;
import org.spartan.fajita.revision.symbols.Terminal;

public class Either extends BaseExtendible {
  public Either(List<Symbol> symbols) {
    super(symbols);
  }
  @Override protected void solve() {
    head = nonTerminal();
    solvedSymbols = solve(symbols);
    for (Symbol s : solvedSymbols)
      addRule(head, Collections.singletonList(s));
  }
  @Override protected boolean isNullable(Set<Symbol> knownNullables) {
    return symbols.stream().allMatch(x -> knownNullables.contains(x));
  }
  @Override protected Set<Terminal> getFirstSet(@SuppressWarnings("unused") Set<Symbol> nullables,
      Map<Symbol, Set<Terminal>> knownFirstSets) {
    Set<Terminal> $ = new HashSet<>();
    for (Symbol s : symbols) {
      if (knownFirstSets.containsKey(s))
        $.addAll(knownFirstSets.get(s));
    }
    return $;
  }
  @Override public List<String> parseTypes(@SuppressWarnings("unused") Function<Symbol, List<String>> operation) {
    return Collections.singletonList(org.spartan.fajita.revision.export.Either.class.getTypeName());
  }
  @SuppressWarnings("unchecked") @Override public List<?> fold(List<?> t) {
    assert isSolved && head != null && t.size() == 1;
    Object o = t.get(0);
    assert o instanceof Interpretation;
    Interpretation current = (Interpretation) o;
    assert head.equals(current.symbol);
    return new LinkedList<>(current.value);
  }
  @SuppressWarnings({ "unused", "rawtypes", "unchecked" }) @Override public List<Object> conclude(List values,
      BiFunction<Symbol, List, List> solution, Function<Symbol, Class> classSolution) {
    assert values.size() == 1 && values.get(0) instanceof Interpretation;
    Interpretation i = (Interpretation) values.get(0);
    List is = solution.apply(i.symbol, i.value);
    // TODO Roth: deal with none/multiple Either values
    return Collections.singletonList(new org.spartan.fajita.revision.export.Either(
        is.isEmpty() ? null : is.size() == 1 ? is.get(0) : is.toArray(new Object[is.size()])));
  }
  @SuppressWarnings({ "rawtypes", "unused" }) @Override public List<Class> toClasses(Function<Symbol, Class> classSolution) {
    return Collections.singletonList(org.spartan.fajita.revision.export.Either.class);
  }
}
