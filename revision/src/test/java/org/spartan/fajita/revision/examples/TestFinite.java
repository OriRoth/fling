package org.spartan.fajita.revision.examples;

import static org.spartan.fajita.revision.junk.TestFinite.*;
import static org.spartan.fajita.revision.api.Fajita.*;
import static org.spartan.fajita.revision.examples.TestFinite.NT.S;
import static org.spartan.fajita.revision.examples.TestFinite.Term.*;

import java.io.IOException;

import org.spartan.fajita.revision.api.Fajita;
import org.spartan.fajita.revision.api.Fajita.FajitaBNF;
import org.spartan.fajita.revision.api.Main;
import org.spartan.fajita.revision.export.Grammar;
import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.Terminal;

public class TestFinite extends Grammar {
  public static enum Term implements Terminal {
    m, n, o
  }

  public static enum NT implements NonTerminal {
    S
  }

  @Override public FajitaBNF bnf() {
    return Fajita.build(TestFinite.class, Term.class, NT.class, "TestFinite", Main.packagePath, Main.projectPath).start(S) //
        .derive(S).to(option(m), either(n, o)).orNone();
  }
  public static void main(String[] args) throws IOException {
    new TestFinite().generateGrammarFiles();
  }
  public static void testing() {
    n().$();
    m().n().$();
    o().$();
    m().o().$();
  }
}
