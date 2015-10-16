package org.spartan.fajita.api;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Modifier;

import org.spartan.fajita.api.examples.balancedParenthesis.BalancedParenthesis;
import org.spartan.fajita.api.generators.BaseStateSpec;
import org.spartan.fajita.api.generators.typeArguments.TypeArgumentManager;
import org.spartan.fajita.api.parser.LRParser;

import com.squareup.javapoet.TypeSpec;

public class Main {
  public static void main(final String[] args) {
    typeSpec(BalancedParenthesis.buildBNF());
  }
  private static void typeSpec(final LRParser parser) {
    TypeArgumentManager tam = new TypeArgumentManager(parser);
    System.out.println(new BaseStateSpec(tam).generate());
    final List<TypeSpec> types = new ArrayList<>();
    parser.getStates().forEach(s -> types
        .add(TypeSpec.classBuilder(s.name).addModifiers(Modifier.STATIC).addTypeVariables(tam.stateTypeArguments(s)).build()));
    for (TypeSpec typeSpec : types)
      System.out.println(typeSpec);
  }
}
