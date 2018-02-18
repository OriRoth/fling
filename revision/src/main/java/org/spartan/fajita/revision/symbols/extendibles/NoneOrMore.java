package org.spartan.fajita.revision.symbols.extendibles;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.spartan.fajita.revision.symbols.Symbol;
import org.spartan.fajita.revision.symbols.Terminal;

public class NoneOrMore extends BaseExtendible {
  public NoneOrMore(List<Symbol> symbols) {
    super(symbols);
  }
  @Override protected void solve() {
    head = nonTerminal();
    solvedSymbols = solve(symbols);
    List<Symbol> rhs = new ArrayList<>(solvedSymbols);
    rhs.add(head);
    addRule(head, rhs);
    addRule(head, new LinkedList<>());
  }
  @Override public boolean isNullable(@SuppressWarnings("unused") Set<Symbol> knownNullables) {
    return true;
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
  @Override public List<String> parseTypes(Function<Symbol, List<String>> operation,
      @SuppressWarnings("unused") Function<Symbol, List<String>> forgivingOperation) {
    List<String> $ = new LinkedList<>();
    for (Symbol s : symbols)
      for (String q : operation.apply(s))
        $.add(q + "[]");
    return $;
  }
  @SuppressWarnings("rawtypes") @Override public List<Class> toClasses(Function<Symbol, Class> classSolution) {
    List<Class> $ = new LinkedList<>();
    for (Symbol s : symbols)
      $.addAll(s.toClasses(classSolution).stream().map(c -> Array.newInstance(c, 0).getClass()).collect(Collectors.toList()));
    return $;
  }
}
