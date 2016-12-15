package org.spartan.fajita.api.rllp.generation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.symbols.Verb;
import org.spartan.fajita.api.bnf.symbols.type.NestedType;
import org.spartan.fajita.api.rllp.Item;
import org.spartan.fajita.api.rllp.JSM;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeVariableName;

public class Utilities {
  private static Random rnd;
  public static final TypeVariableName errorClass = TypeVariableName.get("ERROR");

  public static TypeVariableName verbTypeName(final Verb verb) {
    return TypeVariableName.get(verb.name());
  }
  public static ClassName itemClassName(final Item i) {
    final String suffix = Integer.toHexString(Math.abs(i.rule.hashCode()) % 1000);
    final String itemTypeName = i.rule.lhs + "_" + i.dotIndex + "_" + suffix;
    return ClassName.get("", itemTypeName);
  }
  public static TypeName parameterizedType(final ClassName type, Iterable<? extends TypeName> params) {
    List<TypeName> l = new ArrayList<>();
    for (TypeName param : params)
      l.add(param);
    if (l.isEmpty())
      return type;
    return ParameterizedTypeName.get(type, l.toArray(new TypeName[] {}));
  }
  public static String getNestedTypeName(NestedType type, BNF bnf) {
    return bnf.getApiName() + "_" + type.nested.name();
  }
  public static TypeName parameterizedType(final String typename, Iterable<? extends TypeName> params) {
    return parameterizedType(ClassName.get("", typename), params);
  }
  public static String randomHexString() {
    if (rnd == null)
      rnd = new Random(System.currentTimeMillis());
    return Integer.toHexString(rnd.nextInt(5000));
  }
  public static String recursiveTypeName(JSM jsm) {
    return itemClassName(jsm.peek()).simpleName() + randomHexString() + "_rec_";
  }
}
