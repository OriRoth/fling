package org.spartan.fajita.revision.symbols.extendibles;

import java.util.Map;
import java.util.Set;

import org.spartan.fajita.revision.symbols.Symbol;
import org.spartan.fajita.revision.symbols.Terminal;

public interface Extendible extends Symbol {
  boolean updateNullable(Set<Symbol> knownNullables);
  boolean updateFirstSet(Set<Symbol> nullables, Map<Symbol, Set<Terminal>> knownFirstSets);
}
