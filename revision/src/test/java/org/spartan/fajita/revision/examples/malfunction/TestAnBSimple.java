package org.spartan.fajita.revision.examples.malfunction;

import static org.spartan.fajita.revision.examples.malfunction.TestAnBSimple.NT.S;
import static org.spartan.fajita.revision.examples.malfunction.TestAnBSimple.NT.S1;
import static org.spartan.fajita.revision.examples.malfunction.TestAnBSimple.Term.*;
import static org.spartan.fajita.revision.junk.TestAnB.*;

import java.io.IOException;

import org.spartan.fajita.revision.api.Fajita;
import org.spartan.fajita.revision.api.Fajita.FajitaBNF;
import org.spartan.fajita.revision.api.Main;
import org.spartan.fajita.revision.export.Grammar;
import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.Terminal;

public class TestAnBSimple extends Grammar {
  public static enum Term implements Terminal {
    e, f
  }

  public static enum NT implements NonTerminal {
    S, S1
  }

  @Override public FajitaBNF bnf() {
    return Fajita.build(TestAnBSimple.class, Term.class, NT.class, "TestAnBSimple", Main.packagePath, Main.projectPath).start(S) //
        .derive(S).to(S1, f) //
        .derive(S1).to(e, S1);
  }
  public static void main(String[] args) throws IOException {
    new TestAnBSimple().generateGrammarFiles();
  }
  public static void testing() {
    c().d().$();
    c().c().d().$();
    c().c().c().d().$();
    c().c().c().c().d().$();
  }
}
