package org.spartan.fajita.revision.symbols.extendibles;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.spartan.fajita.revision.bnf.DerivationRule;
import org.spartan.fajita.revision.symbols.Symbol;
import org.spartan.fajita.revision.symbols.Terminal;

public interface Extendible extends Symbol {
  boolean updateNullable(Set<Symbol> knownNullables);
  boolean updateFirstSet(Set<Symbol> nullables, Map<Symbol, Set<Terminal>> knownFirstSets);
  List<String> parseTypes(Function<Symbol, List<String>> operation);
  List<Symbol> symbols();
  List<DerivationRule> rawSolution();
  List<?> fold(List<?> t);
  @SuppressWarnings("rawtypes") List<Object> conclude(List values, BiFunction<Symbol, List, List> solution,
      Function<Symbol, Class> classSolution);
  void fixSymbols(Function<List<Symbol>, List<Symbol>> fix);
}
