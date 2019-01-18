package roth.ori.fling.symbols.types;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import roth.ori.fling.symbols.GrammarElement;

public interface ParameterType {
  boolean accepts(Object arg);
  // TODO Roth: parameter "astPath" is a HACK
  @SuppressWarnings("rawtypes") List conclude(Object arg, BiFunction<GrammarElement, List, List> solution, String astPath);
  @SuppressWarnings("rawtypes") List<Class> toClasses(Function<GrammarElement, Class> classSolution);
  default String toParameterString() {
    return toString();
  }
}
