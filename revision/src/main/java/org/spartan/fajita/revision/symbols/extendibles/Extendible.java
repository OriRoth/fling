package org.spartan.fajita.revision.symbols.extendibles;

import java.util.List;

import org.spartan.fajita.revision.symbols.Symbol;
import org.spartan.fajita.revision.symbols.Terminal;

public interface Extendible extends Symbol {
  boolean isNullable();
  List<Terminal> firstSet();
}
