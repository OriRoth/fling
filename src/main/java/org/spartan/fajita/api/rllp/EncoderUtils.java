package org.spartan.fajita.api.rllp;

import java.util.HashMap;
import java.util.Map;

import org.spartan.fajita.api.Fajita;
import org.spartan.fajita.api.bnf.symbols.Verb;

import com.squareup.javapoet.ClassName;

public class EncoderUtils {
  public static final String finalReturnType = "ASTNode";
  public static final String error = "ParseError";
  public static final String utilClass = "Utils";
  private final Map<Item, String> itemNames = new HashMap<>();
  private Fajita fajita;

  public EncoderUtils(Fajita fajita) {
    this.fajita = fajita;
  }
  private String utilsPath() {
    return fajita.getPackagePath() + "." + utilClass;
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
  // private static String randomHexString() {
  // if (rnd == null)
  // rnd = new Random(System.currentTimeMillis());
  // return Integer.toHexString(rnd.nextInt(5000));
  // }
  private static String hexifyObject(Object o) {
    return Integer.toHexString(Math.abs(o.hashCode() % 1000));
  }
  public ClassName returnTypeOf$() {
    return ClassName.get(utilsPath(), finalReturnType);
  }
  public static ClassName errorType() {
    return ClassName.get("", error);
  }
}
