package org.spartan.fajita.revision.api.encoding;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.spartan.fajita.revision.api.Fajita;
import org.spartan.fajita.revision.ast.encoding.JamoosClassesRenderer;
import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.Symbol;

public class FajitaEncoder3 {
  Fajita fajita;

  public FajitaEncoder3(Fajita fajita) {
    this.fajita = fajita;
  }
  private Map<String, String> _encode() {
    Map<String, String> $ = new HashMap<>();
    String astTopClass = JamoosClassesRenderer.topClassName(fajita.bnf());
    for (NonTerminal start : fajita.startSymbols) {
      RLLPEncoder7 rllpe = new RLLPEncoder7(fajita, start, astTopClass);
      $.put(rllpe.topClassName + ".java", rllpe.topClass);
      for (Entry<String, String> e : rllpe.apiClasses.entrySet())
        $.put(e.getKey() + ".java", e.getValue());
    }
    for (Symbol nested : fajita.nestedParameters) {
      RLLPEncoder7 rllpe = new RLLPEncoder7(fajita, nested, astTopClass);
      $.put(rllpe.topClassName + ".java", rllpe.topClass);
      for (Entry<String, String> e : rllpe.apiClasses.entrySet())
        $.put(e.getKey() + ".java", e.getValue());
    }
    JamoosClassesRenderer jcr = JamoosClassesRenderer.render(fajita.ebnf(), fajita.packagePath);
    $.put(jcr.topClassName + ".java", jcr.topClass);
    $.put("~", fajita.apiName.toLowerCase());
    return $;
  }
  public static Map<String, String> encode(Fajita fajita) {
    return new FajitaEncoder3(fajita)._encode();
  }
}