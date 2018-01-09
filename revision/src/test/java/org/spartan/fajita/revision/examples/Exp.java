package org.spartan.fajita.revision.examples;

import static org.spartan.fajita.revision.examples.Exp.NT.S;
import static org.spartan.fajita.revision.examples.Exp.NT.X;
import static org.spartan.fajita.revision.examples.Exp.Term.a;
import static org.spartan.fajita.revision.examples.Exp.Term.b;
import static org.spartan.fajita.revision.examples.Exp.Term.c;
import static org.spartan.fajita.revision.examples.Exp.Term.d;

import java.io.IOException;

import org.spartan.fajita.revision.api.Fajita;
import org.spartan.fajita.revision.api.Fajita.FajitaBNF;
import org.spartan.fajita.revision.api.Main;
import org.spartan.fajita.revision.export.Grammar;
import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.Terminal;

public class Exp extends Grammar {
  public static enum Term implements Terminal {
    a, b, c, d
  }

  public static enum NT implements NonTerminal {
    S, X
  }

  @Override public FajitaBNF bnf() {
    return Fajita.build(Exp.class, Term.class, NT.class, "Exp", Main.packagePath, Main.projectPath) //
        .start(S) //
        .derive(S).to(X, S, X).or(d) //
        .derive(X).to(a, X, b).or(c);
  }
  public static void main(String[] args) throws IOException {
    new Exp().generateGrammarFiles();
  }
}
