package org.spartan.fajita.revision.symbols.types;

import java.util.List;
import java.util.function.BiFunction;

public interface ParameterType {
  boolean accepts(Object arg);
  @SuppressWarnings("rawtypes") Object conclude(Object arg, BiFunction<Object, List, Object> solution);
}
