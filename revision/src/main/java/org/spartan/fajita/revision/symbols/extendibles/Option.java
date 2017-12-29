package org.spartan.fajita.revision.symbols.extendibles;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.spartan.fajita.revision.parser.ell.Interpretation;
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
  @Override public List<String> parseTypes(Function<Symbol, List<String>> operation) {
    return symbols.stream().map(s -> operation.apply(s)).reduce(new LinkedList<>(), (l1, l2) -> {
      l1.addAll(l2);
      return l1;
    }).stream().map(s -> Optional.class.getTypeName() + "<" + s + ">").collect(toList());
  }
  @Override public List<?> fold(List<?> t) {
    return t.isEmpty() ? new ArrayList<>() : super.fold(t);
  }
  @SuppressWarnings({ "rawtypes", "unused", "unchecked" }) @Override public List<Object> conclude(List values,
      BiFunction<Symbol, List, List> solution, Function<Symbol, Class> classSolution) {
    if (values.isEmpty())
      return symbols.stream().filter(s -> !s.isVerb() || s.asVerb().type.length > 0) //
          .map(s -> Optional.empty()).collect(toList());
    assert values.size() == 1 && values.get(0) instanceof Interpretation;
    Interpretation i = (Interpretation) values.get(0);
    List is = solution.apply(i.symbol, i.value);
    return (List<Object>) is.stream().map(o -> Optional.ofNullable(o)).collect(toList());
  }
  @SuppressWarnings({ "rawtypes", "unused" }) @Override public List<Class> toClasses(Function<Symbol, Class> classSolution) {
    return symbols.stream().map(s -> Optional.class).collect(toList());
  }
}
