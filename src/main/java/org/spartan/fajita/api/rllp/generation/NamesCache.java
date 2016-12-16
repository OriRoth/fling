package org.spartan.fajita.api.rllp.generation;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Verb;
import org.spartan.fajita.api.rllp.Item;
import org.spartan.fajita.api.rllp.JSM;

public class NamesCache {
  public static final String errorTypeName = "ERROR";
  public static String fajitaApiName;
  private final Map<Item, String> itemsName = new HashMap<>();
  private final List<SimpleEntry<JSM, String>> recursiveTypesName = new ArrayList<>();
  private final Map<Verb, String> verbNames = new HashMap<>();
  private static Random rnd;
  private final BNF bnf;

  public NamesCache(BNF bnf) {
    this.bnf = bnf;
  }
  public String getItemName(Item i) {
    if (!itemsName.containsKey(i)) {
      final String $ = i.rule.lhs + "_" + i.dotIndex + "_" + Integer.toHexString(Math.abs(i.rule.hashCode()) % 1000);
      itemsName.put(i, $);
    }
    return itemsName.get(i);
  }
  public String getRecursiveTypeName(JSM jsm) {
    if (!recursiveTypesName.stream().anyMatch(entry -> entry.getKey().equals(jsm))) {
      final Item i = jsm.peek();
      final String name = i.rule.lhs + "_" + i.dotIndex + "_" + hexifyObject(i.rule) + randomHexString() + "__rec__";
      recursiveTypesName.add(new SimpleEntry<>(jsm, name));
    }
    return recursiveTypesName.stream().filter(e -> e.getKey().equals(jsm)).findAny().get().getValue();
  }
  public String verbTypeName(final Verb verb) {
    if (!verbNames.containsKey(verb))
      verbNames.put(verb, verb.name());
    return verbNames.get(verb);
  }
  private static String randomHexString() {
    if (rnd == null)
      rnd = new Random(System.currentTimeMillis());
    return Integer.toHexString(rnd.nextInt(5000));
  }
  private static String hexifyObject(Object o) {
    return Integer.toHexString(Math.abs(o.hashCode() % 1000));
  }
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
