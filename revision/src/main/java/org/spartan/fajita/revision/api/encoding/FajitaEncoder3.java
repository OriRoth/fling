package org.spartan.fajita.revision.api.encoding;

import java.util.HashMap;
import java.util.Map;

import org.spartan.fajita.revision.api.Fajita;
import org.spartan.fajita.revision.ast.encoding.JamoosClassesRenderer;

public class FajitaEncoder3 {
  Fajita fajita;

  public FajitaEncoder3(Fajita fajita) {
    this.fajita = fajita;
  }
  private Map<String, String> _encode() {
    Map<String, String> $ = new HashMap<>();
    RLLPEncoder3 rllpe = new RLLPEncoder3(fajita);
    $.put(rllpe.topClassName + ".java", rllpe.topClass);
    JamoosClassesRenderer jcr = JamoosClassesRenderer.render(fajita.ebnf(), fajita.packagePath);
    $.put(jcr.topClassName + ".java", jcr.topClass);
    return $;
  }
  public static Map<String, String> encode(Fajita fajita) {
    return new FajitaEncoder3(fajita)._encode();
  }
}