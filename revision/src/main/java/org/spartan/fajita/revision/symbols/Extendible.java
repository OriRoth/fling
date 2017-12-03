package org.spartan.fajita.revision.symbols;

import java.util.List;

public interface Extendible extends Symbol {
  boolean isNullable();
  List<Terminal> firstSet();
}
