package org.spartan.fajita.revision.examples;

import static org.spartan.fajita.revision.examples.TestTwoParentsBNF.NT.A;
import static org.spartan.fajita.revision.examples.TestTwoParentsBNF.NT.B;
import static org.spartan.fajita.revision.examples.TestTwoParentsBNF.NT.C;
import static org.spartan.fajita.revision.examples.TestTwoParentsBNF.NT.D;
import static org.spartan.fajita.revision.examples.TestTwoParentsBNF.NT.S;
import static org.spartan.fajita.revision.examples.TestTwoParentsBNF.Term.a;
import static org.spartan.fajita.revision.examples.TestTwoParentsBNF.Term.b;

import java.io.IOException;

import org.spartan.fajita.revision.api.Fajita;
import org.spartan.fajita.revision.api.Fajita.FajitaBNF;
import org.spartan.fajita.revision.api.Main;
import org.spartan.fajita.revision.export.Grammar;
import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.Terminal;

public class TestTwoParentsBNF extends Grammar {
  public static enum Term implements Terminal {
    a, b
  }

  public static enum NT implements NonTerminal {
    S, A, B, C, D
  }

  @Override public FajitaBNF bnf() {
    return Fajita.build(TestTwoParentsBNF.class, Term.class, NT.class, "TestTwoParentsBNF", Main.packagePath, Main.projectPath)
        .start(S) //
        .derive(S).to(A).and(B) //
        .derive(A).to(C).or(D) //
        .derive(B).to(C).or(D) //
        .derive(C).to(a) //
        .derive(D).to(b);
  }
  public static void main(String[] args) throws IOException {
    new TestTwoParentsBNF().generateGrammarFiles();
  }
}
