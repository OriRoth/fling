package org.spartan.fajita.revision.api.encoding;

import java.util.HashMap;
import java.util.Map;

import org.spartan.fajita.revision.api.Fajita;
import org.spartan.fajita.revision.ast.encoding.JamoosClassesRenderer;
import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.Symbol;

public class FajitaEncoder {
  Fajita fajita;

  public FajitaEncoder(Fajita fajita) {
    this.fajita = fajita;
  }
  private Map<String, String> _encode() {
    Map<String, String> $ = new HashMap<>();
    String astTopClass = JamoosClassesRenderer.topClassName(fajita.bnf());
    for (NonTerminal start : fajita.startSymbols) {
      RLLPEncoder rllpe = new RLLPEncoder(fajita, start, astTopClass);
      $.put(rllpe.topClassName + ".java", rllpe.topClass);
    }
    for (Symbol nested : fajita.nestedParameters) {
      RLLPEncoder rllpe = new RLLPEncoder(fajita, nested, astTopClass);
      $.put(rllpe.topClassName + ".java", rllpe.topClass);
    }
    JamoosClassesRenderer jcr = JamoosClassesRenderer.render(fajita.ebnf(), fajita.packagePath);
    $.put(jcr.topClassName + ".java", jcr.topClass);
    return $;
  }
  public static Map<String, String> encode(Fajita fajita) {
    return new FajitaEncoder(fajita)._encode();
  }
}