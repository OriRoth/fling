package org.spartan.fajita.revision.symbols.extendibles;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

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
  @SuppressWarnings("unused") @Override public List<String> parseTypes(Function<Symbol, List<String>> operation,
      Function<Symbol, List<String>> forgivingOperation) {
    return Collections.singletonList(org.spartan.fajita.revision.export.Either.class.getTypeName());
  }
  @SuppressWarnings({ "rawtypes", "unused" }) @Override public List<Class> toClasses(Function<Symbol, Class> classSolution) {
    return Collections.singletonList(org.spartan.fajita.revision.export.Either.class);
  }
}
