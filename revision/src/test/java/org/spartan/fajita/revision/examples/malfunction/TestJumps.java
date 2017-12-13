package org.spartan.fajita.revision.examples.malfunction;

import static org.spartan.fajita.revision.api.Fajita.oneOrMore;
import static org.spartan.fajita.revision.api.Fajita.option;
import static org.spartan.fajita.revision.examples.malfunction.TestJumps.NT.S;
import static org.spartan.fajita.revision.examples.malfunction.TestJumps.Term.x;
import static org.spartan.fajita.revision.examples.malfunction.TestJumps.Term.y;
import static org.spartan.fajita.revision.junk.TestJumps.a;

import java.io.IOException;

import org.spartan.fajita.revision.api.Fajita;
import org.spartan.fajita.revision.api.Fajita.FajitaBNF;
import org.spartan.fajita.revision.api.Main;
import org.spartan.fajita.revision.export.Grammar;
import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.Terminal;

public class TestJumps extends Grammar {
  public static enum Term implements Terminal {
    x, y
  }

  public static enum NT implements NonTerminal {
    S
  }

  @Override public FajitaBNF bnf() {
    return Fajita.build(getClass(), Term.class, NT.class, "TestJumps", Main.packagePath, Main.projectPath) //
        .start(S) //
        .derive(S).to(x, oneOrMore(y, option(x)));
  }
  /**
   * @throws IOException
   */
  public static void main(String[] args) throws IOException {
    new TestJumps().generateGrammarFiles();
    // testing();
  }
  public static void testing() {
    a().b().b().a().b().b().$();
  }
}
