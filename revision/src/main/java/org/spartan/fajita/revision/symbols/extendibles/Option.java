package org.spartan.fajita.revision.symbols.extendibles;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import org.spartan.fajita.revision.symbols.Symbol;

// TODO Roth: Deal with multiple symbols
public class Option extends Either {
  public Option(List<Symbol> symbols) {
    super(symbols);
  }
  @Override protected void solve() {
    super.solve();
    addRule(head, new ArrayList<>());
  }
  @Override protected boolean isNullable(@SuppressWarnings("unused") Set<Symbol> knownNullables) {
    return true;
  }
  @Override public List<String> parseTypes(@SuppressWarnings("unused") Function<Symbol, List<String>> operation,
      Function<Symbol, List<String>> forgivingOperation) {
    return symbols.stream().map(s -> forgivingOperation.apply(s)).reduce(new LinkedList<>(), (l1, l2) -> {
      l1.addAll(l2);
      return l1;
    }).stream().map(s -> Optional.class.getTypeName() + "<" + s + ">").collect(toList());
  }
  @SuppressWarnings({ "rawtypes", "unused" }) @Override public List<Class> toClasses(Function<Symbol, Class> classSolution) {
    return symbols.stream().map(s -> Optional.class).collect(toList());
  }
}
