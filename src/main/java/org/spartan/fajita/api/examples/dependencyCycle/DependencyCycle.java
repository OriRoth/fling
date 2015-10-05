package org.spartan.fajita.api.examples.dependencyCycle;

import static org.spartan.fajita.api.examples.dependencyCycle.DependencyCycle.NT.*;
import static org.spartan.fajita.api.examples.dependencyCycle.DependencyCycle.Term.*;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Modifier;

import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.BNFBuilder;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;
import org.spartan.fajita.api.bnf.symbols.Type;
import org.spartan.fajita.api.generators.BaseStateSpec;
import org.spartan.fajita.api.generators.typeArguments.TypeArgumentManager;
import org.spartan.fajita.api.parser.LRParser;

import com.squareup.javapoet.TypeSpec;

public class DependencyCycle {
  public static void expressionBuilder() {
    //
  }

  static enum Term implements Terminal {
    b, c, d, e;
    @Override public Type type() {
      return Type.VOID;
    }
  }

  static enum NT implements NonTerminal {
    A, B;
  }

  public static LRParser buildBNF() {
    BNF bnf = new BNFBuilder(Term.class, NT.class) //
        .startConfig() //
        .setApiNameTo("Balanced Parenthesis") //
        .setStartSymbols(A) //
        .endConfig() //
        .derive(A).to(B).and(d) //
        .derive(B).to(A).and(e) //
        .finish();
    System.out.println(bnf);
    LRParser parser = new LRParser(bnf);
    System.out.println(parser.states);
    System.out.println(parser);
    return parser;
  }
  public static void apiGeneration(final LRParser parser) {
    // ApiGenerator apiGenerator = new ApiGenerator(parser);
    System.out.println(new BaseStateSpec(new TypeArgumentManager(parser)).generate());
  }
  public static void main(final String[] args) {
    LRParser parser = buildBNF();
    TypeArgumentManager tam = new TypeArgumentManager(parser);
    System.out.println(new BaseStateSpec(tam).generate());
    final List<TypeSpec> $ = new ArrayList<>();
    parser.states.forEach(s -> $.add(TypeSpec.classBuilder("Q" + s.stateIndex).addModifiers(Modifier.STATIC)
        .addTypeVariables(tam.stateTypeArguments(s)).build()));
    for (TypeSpec typeSpec : $)
      System.out.println(typeSpec);
  }
}
