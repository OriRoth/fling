package roth.ori.fling.symbols.extendibles;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

import roth.ori.fling.bnf.DerivationRule;
import roth.ori.fling.symbols.NonTerminal;
import roth.ori.fling.symbols.Symbol;
import roth.ori.fling.symbols.Terminal;

public interface Extendible extends Symbol {
  boolean updateNullable(Set<Symbol> knownNullables);
  boolean updateFirstSet(Set<Symbol> nullables, Map<Symbol, Set<Terminal>> knownFirstSets);
  List<String> parseTypes(Function<Symbol, List<String>> operation, Function<Symbol, //
      List<String>> forgivingOperation);
  List<Symbol> symbols();
  List<DerivationRule> rawSolution();
  List<?> fold(List<?> t);
  @SuppressWarnings("rawtypes") List<Object> conclude(List values, BiFunction<Symbol, List, List> solution,
      Function<Symbol, Class> classSolution);
  void fixSymbols(Function<List<Symbol>, List<Symbol>> fix);
  @Override NonTerminal head();
}
