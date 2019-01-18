package roth.ori.fling.symbols.extendibles;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

import roth.ori.fling.bnf.DerivationRule;
import roth.ori.fling.symbols.NonTerminal;
import roth.ori.fling.symbols.GrammarElement;
import roth.ori.fling.symbols.Terminal;

public interface Extendible extends GrammarElement {
  boolean updateNullable(Set<GrammarElement> knownNullables);
  boolean updateFirstSet(Set<GrammarElement> nullables, Map<GrammarElement, Set<Terminal>> knownFirstSets);
  List<String> parseTypes(Function<GrammarElement, List<String>> operation, Function<GrammarElement, //
      List<String>> forgivingOperation);
  List<GrammarElement> symbols();
  List<DerivationRule> rawSolution();
  List<?> fold(List<?> t);
  @SuppressWarnings("rawtypes") List<Object> conclude(List values, BiFunction<GrammarElement, List, List> solution,
      Function<GrammarElement, Class> classSolution);
  void fixSymbols(Function<List<GrammarElement>, List<GrammarElement>> fix);
  @Override NonTerminal head();
}
