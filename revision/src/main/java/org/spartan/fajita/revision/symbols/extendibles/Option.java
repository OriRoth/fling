package org.spartan.fajita.revision.symbols.extendibles;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.spartan.fajita.revision.parser.ell.ELLRecognizer;
import org.spartan.fajita.revision.symbols.Symbol;

import static java.util.stream.Collectors.toList;

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
  @Override public List<String> parseTypes(Function<Symbol, List<String>> operation) {
    return symbols.stream().map(s -> operation.apply(s)).reduce(new LinkedList<>(), (l1, l2) -> {
      l1.addAll(l2);
      return l1;
    }).stream().map(s -> Optional.class.getTypeName() + "<" + s + ">").collect(toList());
  }
  @Override public List<?> fold(List<?> t) {
    return t.isEmpty() ? ELLRecognizer.SKIP : super.fold(t);
  }
  @SuppressWarnings("rawtypes") @Override public List<Object> conclude(List values, BiFunction<Symbol, List, List> solution,
      Function<Symbol, Class> classSolution) {
    return values.isEmpty() ? new ArrayList<>() : super.conclude(values, solution, classSolution);
  }
  @SuppressWarnings({ "rawtypes", "unused" }) @Override public List<Class> toClasses(Function<Symbol, Class> classSolution) {
    return symbols.stream().map(s -> Optional.class).collect(toList());
  }
}
