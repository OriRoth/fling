package org.spartan.fajita.api.rllp;

import java.util.HashMap;
import java.util.Map;

import org.spartan.fajita.api.ast.ASTNode;
import org.spartan.fajita.api.bnf.symbols.Verb;

import com.squareup.javapoet.ClassName;

public class EEncoderUtils {
  public static final String error = "ParseError";
  private final Map<Item, String> itemNames = new HashMap<>();

  private static String utilsPath() {
    return ASTNode.class.getPackage().getName();
  }
  public String getItemName(Item i) {
    if (!itemNames.containsKey(i)) {
      long similar_lhs = itemNames.keySet().stream()
          .filter(i2 -> i2.rule.lhs == i.rule.lhs && i2.rule.size() > 0 && i2.rule != i.rule).count();
      final String $ = i.rule.lhs + "_" + i.dotIndex + (similar_lhs > 0 ? "_n" + similar_lhs : "");
      itemNames.put(i, $);
    }
    return itemNames.get(i);
  }
  public String getRecursiveTypeName(JSM jsm) {
    return getItemName(jsm.peek()) + "_rec_" + hexifyObject(jsm.getS0());
  }
  public static String verbTypeName(final Verb verb) {
    return verb.name();
  }
  private static String hexifyObject(Object o) {
    return Integer.toHexString(Math.abs(o.hashCode() % 1000));
  }
  public static ClassName returnTypeOf$() {
    return ClassName.get(utilsPath(), ASTNode.class.getSimpleName());
  }
  public static ClassName errorType() {
    return ClassName.get("", error);
  }
}
