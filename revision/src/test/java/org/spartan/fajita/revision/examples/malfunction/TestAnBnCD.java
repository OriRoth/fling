package org.spartan.fajita.revision.examples.malfunction;

import static org.spartan.fajita.revision.examples.malfunction.TestAnBnCD.NT.A;
import static org.spartan.fajita.revision.examples.malfunction.TestAnBnCD.NT.A1;
import static org.spartan.fajita.revision.examples.malfunction.TestAnBnCD.NT.B;
import static org.spartan.fajita.revision.examples.malfunction.TestAnBnCD.NT.B1;
import static org.spartan.fajita.revision.examples.malfunction.TestAnBnCD.NT.C;
import static org.spartan.fajita.revision.examples.malfunction.TestAnBnCD.NT.D;
import static org.spartan.fajita.revision.examples.malfunction.TestAnBnCD.NT.E;
import static org.spartan.fajita.revision.examples.malfunction.TestAnBnCD.NT.F;
import static org.spartan.fajita.revision.examples.malfunction.TestAnBnCD.NT.S;
import static org.spartan.fajita.revision.examples.malfunction.TestAnBnCD.Term.a2;
import static org.spartan.fajita.revision.examples.malfunction.TestAnBnCD.Term.b2;
import static org.spartan.fajita.revision.examples.malfunction.TestAnBnCD.Term.c2;
import static org.spartan.fajita.revision.examples.malfunction.TestAnBnCD.Term.d2;
import static org.spartan.fajita.revision.examples.malfunction.TestAnBnCD.Term.e2;
import static org.spartan.fajita.revision.examples.malfunction.TestAnBnCD.Term.f2;

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
    S, A, B, C, D, E, F, A1, B1
  }

  @Override public FajitaBNF bnf() {
    return Fajita.build(TestAnBnCD.class, Term.class, NT.class, "TestAnBnCD", Main.packagePath, Main.projectPath).start(S) //
        .derive(S).to(A, S, B).orNone() //
        .derive(A).to(A1) //
        .derive(B).to(B1) //
        .derive(A1).to(C, a2, D) //
        .derive(B1).to(E, b2, F) //
        .derive(C).to(c2).orNone() //
        .derive(D).to(d2).orNone() //
        .derive(E).to(e2).orNone() //
        .derive(F).to(f2).orNone();
  }
  public static void main(String[] args) throws IOException {
    new TestAnBnCD().generateGrammarFiles();
  }
  public static void testing() {
    // a().b().$();
    // a().a().b().b().$();
    // a().a().a().b().b().b().$();
    // a().a().a().a().b().b().b().b().$();
  }
}
