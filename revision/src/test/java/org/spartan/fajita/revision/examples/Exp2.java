package org.spartan.fajita.revision.examples;

import static org.spartan.fajita.revision.examples.Exp2.NT.*;
import static org.spartan.fajita.revision.examples.Exp2.Term.*;

import java.io.IOException;

import org.spartan.fajita.revision.api.Fajita;
import org.spartan.fajita.revision.api.Fajita.FajitaBNF;
import org.spartan.fajita.revision.api.Main;
import org.spartan.fajita.revision.export.Grammar;
import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.Terminal;

public class Exp2 extends Grammar {
  public static enum Term implements Terminal {
    a, b, c
  }

  public static enum NT implements NonTerminal {
    S
  }

  @Override public FajitaBNF bnf() {
    return Fajita.build(Exp2.class, Term.class, NT.class, "Exp2", Main.packagePath, Main.projectPath) //
        .start(S) //
        .derive(S).to(a, S, b, S, c).orNone() //
    ;
  }
  public static void main(String[] args) throws IOException {
    new Exp2().generateGrammarFiles();
  }
}
