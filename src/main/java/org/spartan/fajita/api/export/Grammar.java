package org.spartan.fajita.api.export;

import java.io.IOException;

import org.spartan.fajita.api.EFajita.FajitaBNF;
import org.spartan.fajita.api.Main;

public abstract class Grammar {
  public abstract FajitaBNF bnf();
  // TODO Roth: manage project path
  public void generateGrammarFiles(String packagePath) throws IOException {
    Main.apiGenerator(bnf().go(packagePath));
  }
}
