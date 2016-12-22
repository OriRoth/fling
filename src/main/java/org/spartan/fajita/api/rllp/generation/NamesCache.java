package org.spartan.fajita.api.rllp.generation;

import java.util.HashMap;
import java.util.Map;

import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Verb;
import org.spartan.fajita.api.rllp.Item;
import org.spartan.fajita.api.rllp.JSM;

public class NamesCache {
  public static final String errorTypeName = "ERROR";
  public static String fajitaApiName;
  private final Map<Item, String> itemNames = new HashMap<>();
  private final BNF bnf;

  public NamesCache(BNF bnf) {
    this.bnf = bnf;
  }
  public String getItemName(Item i) {
    if (!itemNames.containsKey(i)) {
      long similar_lhs = itemNames.keySet().stream()
          .filter(i2 -> i2.rule.lhs == i.rule.lhs && i2.rule.getChildren().size() > 0 && i2.rule != i.rule).count();
      final String $ = i.rule.lhs + "_" + i.dotIndex + (similar_lhs > 0 ? "_n" + similar_lhs : "");
      itemNames.put(i, $);
    }
    return itemNames.get(i);
  }
  public String getRecursiveTypeName(JSM jsm) {
    return getItemName(jsm.peek()) + "_rec_";
  }
  public static String verbTypeName(final Verb verb) {
    return verb.name();
  }
//  private static String randomHexString() {
//    if (rnd == null)
//      rnd = new Random(System.currentTimeMillis());
//    return Integer.toHexString(rnd.nextInt(5000));
//  }
//  private static String hexifyObject(Object o) {
//    return Integer.toHexString(Math.abs(o.hashCode() % 1000));
//  }
  public static String returnTypeOf$(NonTerminal startNT) {
    return getApiName(startNT) + "_$_";
  }
  public String returnTypeOf$() {
    return returnTypeOf$(bnf.getStartSymbols().get(0));
  }
  public static String getApiName(NonTerminal startNT) {
    return fajitaApiName + "__" + startNT.name() + "__";
  }
}
