package org.spartan.fajita.api.examples.dependencyCycle.use;

import static org.spartan.fajita.api.examples.dependencyCycle.use.DC2.NT.*;
import static org.spartan.fajita.api.examples.dependencyCycle.use.DC2.Term.*;

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
import org.spartan.fajita.api.parser.LRParser;

public class DC2 {
  @SuppressWarnings("unused") public static void expressionBuilder() {
    Q0 q0 = new DC2States.Q0();
    Q2<Q0, Q3Q3<Q1<Q0, ?>>> a1 = q0.a();
    Q3Q3<Q1<Q0, ?>> a2 = a1.a();
    Q3<Q1<Q0, ?>, Q3Q3<Q1<Q0, ?>>> a3 = a2.a();
    Q3Q3<Q1<Q0, ?>> a4 = a3.a();
    int size = a4.$();
  }

  static enum Term implements Terminal {
    a;
  }

  static enum NT implements NonTerminal {
    A;
  }

  public static LRParser buildBNF() {
    BNF bnf = new BNFBuilder(Term.class, NT.class) //
        
        
        .start(A) //
        
        .derive(A).to(A).and(a).or().to(a) //
        .finish();
    System.out.println(bnf);
    LRParser parser = new LRParser(bnf);
    System.out.println(parser);
    return parser;
  }
}
