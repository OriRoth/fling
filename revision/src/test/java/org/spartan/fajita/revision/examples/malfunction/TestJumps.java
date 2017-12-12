package org.spartan.fajita.revision.examples.malfunction;

import static org.spartan.fajita.revision.api.Fajita.oneOrMore;
import static org.spartan.fajita.revision.api.Fajita.option;
import static org.spartan.fajita.revision.examples.malfunction.TestJumps.NT.S;
import static org.spartan.fajita.revision.examples.malfunction.TestJumps.Term.a;
import static org.spartan.fajita.revision.examples.malfunction.TestJumps.Term.b;

import org.spartan.fajita.revision.api.Fajita;
import org.spartan.fajita.revision.api.Fajita.FajitaBNF;
import org.spartan.fajita.revision.api.Main;
import org.spartan.fajita.revision.export.Grammar;
import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.Terminal;

public class TestJumps extends Grammar {
  public static enum Term implements Terminal {
    a, b
  }

  public static enum NT implements NonTerminal {
    S
  }

  @Override public FajitaBNF bnf() {
    return Fajita.build(getClass(), Term.class, NT.class, "TestJumps", Main.packagePath, Main.projectPath) //
        .start(S) //
        .derive(S).to(a, oneOrMore(b, option(a)));
  }
}
