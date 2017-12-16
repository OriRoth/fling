package org.spartan.fajita.revision.examples.malfunction;

import static org.spartan.fajita.revision.examples.malfunction.TestAnB.NT.A;
import static org.spartan.fajita.revision.examples.malfunction.TestAnB.NT.B;
import static org.spartan.fajita.revision.examples.malfunction.TestAnB.NT.S;
import static org.spartan.fajita.revision.examples.malfunction.TestAnB.NT.S1;
import static org.spartan.fajita.revision.examples.malfunction.TestAnB.Term.c;
import static org.spartan.fajita.revision.examples.malfunction.TestAnB.Term.d;

import java.io.IOException;

import org.spartan.fajita.revision.api.Fajita;
import org.spartan.fajita.revision.api.Fajita.FajitaBNF;
import org.spartan.fajita.revision.api.Main;
import org.spartan.fajita.revision.export.Grammar;
import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.Terminal;

public class TestAnB extends Grammar {
  public static enum Term implements Terminal {
    c, d
  }

  public static enum NT implements NonTerminal {
    S, S1, A, B
  }

  @Override public FajitaBNF bnf() {
    return Fajita.build(TestAnB.class, Term.class, NT.class, "TestAnB", Main.packagePath, Main.projectPath).start(S) //
        .derive(S).to(S1, B) //
        .derive(S1).to(A, S1) //
        .derive(A).to(c) //
        .derive(B).to(d);
  }
  public static void main(String[] args) throws IOException {
    new TestAnB().generateGrammarFiles();
  }
  public static void testing() {
    // c().d().$();
    // c().c().d().$();
    // c().c().c().d().$();
    // c().c().c().c().d().$();
  }
}
