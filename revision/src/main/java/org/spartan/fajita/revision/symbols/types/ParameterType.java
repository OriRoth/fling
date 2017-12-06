package org.spartan.fajita.revision.symbols.types;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.spartan.fajita.revision.symbols.Symbol;

public interface ParameterType {
  boolean accepts(Object arg);
  @SuppressWarnings("rawtypes") List conclude(Object arg, BiFunction<Symbol, List, List> solution);
  @SuppressWarnings("rawtypes") List<Class> toClasses(Function<Symbol, Class> classSolution);
}
