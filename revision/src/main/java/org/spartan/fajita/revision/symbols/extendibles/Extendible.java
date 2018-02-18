package org.spartan.fajita.revision.symbols.extendibles;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.spartan.fajita.revision.bnf.DerivationRule;
import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.Symbol;
import org.spartan.fajita.revision.symbols.Terminal;

public interface Extendible extends Symbol {
  boolean updateNullable(Set<Symbol> knownNullables);
  boolean updateFirstSet(Set<Symbol> nullables, Map<Symbol, Set<Terminal>> knownFirstSets);
  List<String> parseTypes(Function<Symbol, List<String>> operation, Function<Symbol, //
      List<String>> forgivingOperation);
  List<Symbol> symbols();
  List<DerivationRule> rawSolution();
  void fixSymbols(Function<List<Symbol>, List<Symbol>> fix);
  @Override NonTerminal head();
}
