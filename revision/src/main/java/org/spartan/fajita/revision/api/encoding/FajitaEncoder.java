package org.spartan.fajita.revision.api.encoding;

import java.util.HashMap;
import java.util.Map;

import org.spartan.fajita.revision.api.Fajita;
import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.Symbol;

public class FajitaEncoder {
  Fajita fajita;

  public FajitaEncoder(Fajita fajita) {
    this.fajita = fajita;
  }
  private Map<String, String> _encode() {
    Map<String, String> $ = new HashMap<>();
    for (NonTerminal start : fajita.startSymbols) {
      RLRAEncoder rllpe = new RLRAEncoder(fajita, start);
      $.put(rllpe.topClassName + ".java", rllpe.topClass);
    }
    for (Symbol nested : fajita.nestedParameters) {
      RLRAEncoder rllpe = new RLRAEncoder(fajita, nested);
      $.put(rllpe.topClassName + ".java", rllpe.topClass);
    }
    return $;
  }
  public static Map<String, String> encode(Fajita fajita) {
    return new FajitaEncoder(fajita)._encode();
  }
}