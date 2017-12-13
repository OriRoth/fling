package org.spartan.fajita.revision.examples;

import static org.spartan.fajita.revision.examples.TestForceEpsilonMove.NT.*;
import static org.spartan.fajita.revision.examples.TestForceEpsilonMove.Term.*;

import java.io.IOException;

import org.spartan.fajita.revision.api.Fajita;
import org.spartan.fajita.revision.api.Fajita.FajitaBNF;
import org.spartan.fajita.revision.api.Main;
import org.spartan.fajita.revision.export.Grammar;
import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.Terminal;

public class TestForceEpsilonMove extends Grammar {
  public static enum Term implements Terminal {
    a, b, c, d
  }

  public static enum NT implements NonTerminal {
    S, SA, SB, AA, BB
  }

  @Override public FajitaBNF bnf() {
    return Fajita
        .build(TestForceEpsilonMove.class, Term.class, NT.class, "TestForceEpsilonMove", Main.packagePath, Main.projectPath)
        .start(S) //
        .derive(S).to(a, SA, a).or(a, SB) //
        .derive(SA).to(a, AA, a).or(b, AA) //
        .derive(AA).to(b, AA).or(c) //
        .derive(SB).to(a, SB).or(b, BB, b) //
        .derive(BB).to(b, BB, b).or(d);
  }
  public static void main(String[] args) throws IOException {
    new TestForceEpsilonMove().generateGrammarFiles();
  }
}
