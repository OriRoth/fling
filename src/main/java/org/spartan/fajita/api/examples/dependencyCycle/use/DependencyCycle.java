package org.spartan.fajita.api.examples.dependencyCycle.use;

import static org.spartan.fajita.api.examples.dependencyCycle.use.DependencyCycle.NT.A;
import static org.spartan.fajita.api.examples.dependencyCycle.use.DependencyCycle.NT.B;
import static org.spartan.fajita.api.examples.dependencyCycle.use.DependencyCycle.Term.a;
import static org.spartan.fajita.api.examples.dependencyCycle.use.DependencyCycle.Term.d;
import static org.spartan.fajita.api.examples.dependencyCycle.use.DependencyCycle.Term.e;

import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.BNFBuilder;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;
import org.spartan.fajita.api.examples.dependencyCycle.States;
import org.spartan.fajita.api.examples.dependencyCycle.States.Q0;
import org.spartan.fajita.api.examples.dependencyCycle.States.Q1;
import org.spartan.fajita.api.examples.dependencyCycle.States.Q2;
import org.spartan.fajita.api.examples.dependencyCycle.States.Q3;
import org.spartan.fajita.api.examples.dependencyCycle.States.Q4;
import org.spartan.fajita.api.examples.dependencyCycle.States.Q5;
import org.spartan.fajita.api.examples.dependencyCycle.States.Q5Q4Q5;

public class DependencyCycle {
  @SuppressWarnings({ "hiding", "unused" }) public static void expressionBuilder() {
    Q0 q0 = new States.Q0();
    Q3<Q0, Q5Q4Q5> a = q0.a();
    Q5<Q1<Q0, ?>, Q4<Q2<Q0, ?>, Q5Q4Q5>> ae = a.e();
    Q4<Q2<Q0, ?>, Q5Q4Q5> aed = ae.d();
    Q5Q4Q5 aede = aed.e();
    Q4<Q2<Q0, ?>, Q5Q4Q5> aeded = aede.d();
  }

  static enum Term implements Terminal {
    a, d, e;
  }

  static enum NT implements NonTerminal {
    A, B;
  }

  public static BNF buildBNF() {
    BNF bnf = new BNFBuilder(Term.class, NT.class) //
        .start(A) //
        .derive(A).to(B).and(d).or(a) //
        .derive(B).to(A).and(e) //
        .finish();
    return bnf;
  }
}
