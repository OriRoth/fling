package org.spartan.fajita.api.examples.dependencyCycle.use;

import static org.spartan.fajita.api.examples.dependencyCycle.use.DependencyCycle.NT.*;
import static org.spartan.fajita.api.examples.dependencyCycle.use.DependencyCycle.Term.*;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Modifier;

import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.BNFBuilder;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;
import org.spartan.fajita.api.bnf.symbols.Type;
import org.spartan.fajita.api.examples.dependencyCycle.States;
import org.spartan.fajita.api.examples.dependencyCycle.States.Q0;
import org.spartan.fajita.api.examples.dependencyCycle.States.Q1;
import org.spartan.fajita.api.examples.dependencyCycle.States.Q2;
import org.spartan.fajita.api.examples.dependencyCycle.States.Q3;
import org.spartan.fajita.api.examples.dependencyCycle.States.Q4;
import org.spartan.fajita.api.examples.dependencyCycle.States.Q5;
import org.spartan.fajita.api.examples.dependencyCycle.States.Q5Q4Q5;
import org.spartan.fajita.api.generators.BaseStateSpec;
import org.spartan.fajita.api.generators.typeArguments.TypeArgumentManager;
import org.spartan.fajita.api.parser.LRParser;

import com.squareup.javapoet.TypeSpec;

public class DependencyCycle {
  public static void expressionBuilder() {
    Q0 q0 = new States.Q0();
    Q3<Q0, Q5Q4Q5> a = q0.a();
    Q5<Q1<Q0, ?>, Q4<Q2<Q0, ?>, Q5Q4Q5>> ae = a.e();
    Q4<Q2<Q0, ?>, Q5Q4Q5> aed = ae.d();
    Q5Q4Q5 aede = aed.e();
    Q4<Q2<Q0, ?>, Q5Q4Q5> aeded = aede.d();
  }

  static enum Term implements Terminal {
    a, d, e;
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
        .derive(A).to(B).and(d).or().to(a) //
        .derive(B).to(A).and(e) //
        .finish();
    System.out.println(bnf);
    LRParser parser = new LRParser(bnf);
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
