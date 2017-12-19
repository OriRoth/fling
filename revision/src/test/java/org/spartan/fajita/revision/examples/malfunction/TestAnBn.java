package org.spartan.fajita.revision.examples.malfunction;

import static org.spartan.fajita.revision.examples.malfunction.TestAnBn.NT.A;
import static org.spartan.fajita.revision.examples.malfunction.TestAnBn.NT.B;
import static org.spartan.fajita.revision.examples.malfunction.TestAnBn.NT.S;
import static org.spartan.fajita.revision.examples.malfunction.TestAnBn.Term.a;
import static org.spartan.fajita.revision.examples.malfunction.TestAnBn.Term.b;
import static org.spartan.fajita.revision.junk.TestAnBn.a;

import java.io.IOException;

import org.spartan.fajita.revision.api.Fajita;
import org.spartan.fajita.revision.api.Fajita.FajitaBNF;
import org.spartan.fajita.revision.api.Main;
import org.spartan.fajita.revision.export.Grammar;
import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.Terminal;

public class TestAnBn extends Grammar {
  public static enum Term implements Terminal {
    a, b
  }

  public static enum NT implements NonTerminal {
    S, A, B
  }

  @Override public FajitaBNF bnf() {
    return Fajita.build(TestAnBn.class, Term.class, NT.class, "TestAnBn", Main.packagePath, Main.projectPath).start(S) //
        .derive(S).to(A, S, B).orNone() //
        .derive(A).to(a) //
        .derive(B).to(b);
  }
  public static void main(String[] args) throws IOException {
    new TestAnBn().generateGrammarFiles();
    // testing();
  }
  public static void testing() {
    a().b().$();
    a().a().b().b().$();
    a().a().a().b().b().b().$();
    a().a().a().a().b().b().b().b().$();
  }
}
