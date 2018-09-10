package roth.ori.fling.api.encoding;

import java.util.HashMap;
import java.util.Map;

import roth.ori.fling.api.Fling;
import roth.ori.fling.ast.encoding.JamoosClassesRenderer;
import roth.ori.fling.symbols.NonTerminal;
import roth.ori.fling.symbols.Symbol;

public class FlingEncoder {
  Fling fling;

  public FlingEncoder(Fling fling) {
    this.fling = fling;
  }
  private Map<String, String> _encode() {
    Map<String, String> $ = new HashMap<>();
    String astTopClass = JamoosClassesRenderer.topClassName(fling.bnf());
    for (NonTerminal start : fling.startSymbols) {
      RLLPEncoder rllpe = new RLLPEncoder(fling, start, astTopClass);
      $.put(rllpe.topClassName + ".java", rllpe.topClass);
    }
    for (Symbol nested : fling.nestedParameters) {
      RLLPEncoder rllpe = new RLLPEncoder(fling, nested, astTopClass);
      $.put(rllpe.topClassName + ".java", rllpe.topClass);
    }
    JamoosClassesRenderer jcr = JamoosClassesRenderer.render(fling.ebnf(), fling.packagePath);
    $.put(jcr.topClassName + ".java", jcr.topClass);
    return $;
  }
  public static Map<String, String> encode(Fling fling) {
    return new FlingEncoder(fling)._encode();
  }
}