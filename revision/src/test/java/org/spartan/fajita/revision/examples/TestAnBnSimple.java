package org.spartan.fajita.revision.examples;

import static org.spartan.fajita.revision.examples.TestAnBnSimple.NT.S;
import static org.spartan.fajita.revision.examples.TestAnBnSimple.Term.*;
import static org.spartan.fajita.revision.junk.TestAnBnSimple.*;

import java.io.IOException;

import org.spartan.fajita.revision.api.Fajita;
import org.spartan.fajita.revision.api.Fajita.FajitaBNF;
import org.spartan.fajita.revision.api.Main;
import org.spartan.fajita.revision.export.Grammar;
import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.Terminal;

public class TestAnBnSimple extends Grammar {
  public static enum Term implements Terminal {
    g, h
  }

  public static enum NT implements NonTerminal {
    S
  }

  @Override public FajitaBNF bnf() {
    return Fajita.build(TestAnBnSimple.class, Term.class, NT.class, "TestAnBnSimple", Main.packagePath, Main.projectPath).start(S) //
        .derive(S).to(g, S, h).orNone();
  }
  public static void main(String[] args) throws IOException {
    new TestAnBnSimple().generateGrammarFiles();
  }
  public static void testing() {
    g().h().$();
    g().g().h().h().$();
    g().g().g().h().h().h().$();
    g().g().g().g().h().h().h().h().$();
  }
}
