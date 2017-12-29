package org.spartan.fajita.revision.examples;

import static org.spartan.fajita.revision.examples.TestAnBnCD.NT.A;
import static org.spartan.fajita.revision.examples.TestAnBnCD.NT.B;
import static org.spartan.fajita.revision.examples.TestAnBnCD.NT.C;
import static org.spartan.fajita.revision.examples.TestAnBnCD.NT.D;
import static org.spartan.fajita.revision.examples.TestAnBnCD.NT.E;
import static org.spartan.fajita.revision.examples.TestAnBnCD.NT.F;
import static org.spartan.fajita.revision.examples.TestAnBnCD.NT.S;
import static org.spartan.fajita.revision.examples.TestAnBnCD.Term.a2;
import static org.spartan.fajita.revision.examples.TestAnBnCD.Term.b2;
import static org.spartan.fajita.revision.examples.TestAnBnCD.Term.c2;
import static org.spartan.fajita.revision.examples.TestAnBnCD.Term.d2;
import static org.spartan.fajita.revision.examples.TestAnBnCD.Term.e2;
import static org.spartan.fajita.revision.examples.TestAnBnCD.Term.f2;
import static org.spartan.fajita.revision.junk.TestAnBnCD.*;

import java.io.IOException;

import org.spartan.fajita.revision.api.Fajita;
import org.spartan.fajita.revision.api.Fajita.FajitaBNF;
import org.spartan.fajita.revision.api.Main;
import org.spartan.fajita.revision.export.Grammar;
import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.Terminal;

public class TestAnBnCD extends Grammar {
  public static enum Term implements Terminal {
    a2, b2, c2, d2, e2, f2
  }

  public static enum NT implements NonTerminal {
    S, A, B, C, D, E, F
  }

  @Override public FajitaBNF bnf() {
    return Fajita.build(TestAnBnCD.class, Term.class, NT.class, "TestAnBnCD", Main.packagePath, Main.projectPath).start(S) //
        .derive(S).to(A, S, B).orNone() //
        .derive(A).to(C, a2, D) //
        .derive(B).to(E, b2, F) //
        .derive(C).to(c2).orNone() //
        .derive(D).to(d2).orNone() //
        .derive(E).to(e2).orNone() //
        .derive(F).to(f2).orNone();
  }
  public static void main(String[] args) throws IOException {
    new TestAnBnCD().generateGrammarFiles();
  }
  public static void testing() {
    a2().b2().$();
    a2().a2().b2().b2().$();
    a2().a2().a2().b2().b2().b2().$();
    a2().a2().a2().a2().b2().b2().b2().b2().$();
    a2().a2().a2().c2().a2().d2().e2().b2().f2().b2().b2().b2().$();
  }
}
