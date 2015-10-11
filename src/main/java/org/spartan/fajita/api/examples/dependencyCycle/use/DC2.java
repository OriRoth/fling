package org.spartan.fajita.api.examples.dependencyCycle.use;

import static org.spartan.fajita.api.examples.dependencyCycle.use.DC2.NT.*;
import static org.spartan.fajita.api.examples.dependencyCycle.use.DC2.Term.*;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Modifier;

import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.BNFBuilder;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;
import org.spartan.fajita.api.bnf.symbols.Type;
import org.spartan.fajita.api.examples.dependencyCycle.DC2States;
import org.spartan.fajita.api.examples.dependencyCycle.DC2States.Q0;
import org.spartan.fajita.api.examples.dependencyCycle.DC2States.Q1;
import org.spartan.fajita.api.examples.dependencyCycle.DC2States.Q2;
import org.spartan.fajita.api.examples.dependencyCycle.DC2States.Q3;
import org.spartan.fajita.api.examples.dependencyCycle.DC2States.Q3Q3;
import org.spartan.fajita.api.generators.BaseStateSpec;
import org.spartan.fajita.api.generators.typeArguments.TypeArgumentManager;
import org.spartan.fajita.api.parser.LRParser;

import com.squareup.javapoet.TypeSpec;

public class DC2 {
  public static void expressionBuilder() {
    Q0 q0 = new DC2States.Q0();
    Q2<Q0, Q3Q3<Q1<Q0, ?>>> a1 = q0.a();
    Q3Q3<Q1<Q0, ?>> a2 = a1.a();
    Q3<Q1<Q0, ?>, Q3Q3<Q1<Q0, ?>>> a3 = a2.a();
    Q3Q3<Q1<Q0, ?>> a4 = a3.a();
    int size = a4.$();
  }

  static enum Term implements Terminal {
    a;
    @Override public Type type() {
      return Type.VOID;
    }
  }

  static enum NT implements NonTerminal {
    A;
  }

  public static LRParser buildBNF() {
    BNF bnf = new BNFBuilder(Term.class, NT.class) //
        .startConfig() //
        .setApiNameTo("list of a's") //
        .setStartSymbols(A) //
        .endConfig() //
        .derive(A).to(A).and(a).or().to(a) //
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
