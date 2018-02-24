package org.spartan.fajita.revision.export;

import java.io.IOException;

import org.spartan.fajita.revision.api.Fajita.FajitaBNF;
import org.spartan.fajita.revision.api.Main;

public abstract class Grammar {
  public abstract FajitaBNF bnf();
  public void generateGrammarFiles() throws IOException {
    Main.apiGenerator(bnf().go());
  }
}
