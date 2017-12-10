package org.spartan.fajita.revision.export;

import java.io.IOException;

import org.spartan.fajita.revision.api.Fajita.FajitaBNF;
import org.spartan.fajita.revision.export.FajitaTestingAST.ExampleKind;
import org.spartan.fajita.revision.export.FajitaTestingAST.Test;
import org.spartan.fajita.revision.api.Main;

public abstract class Grammar {
  public abstract FajitaBNF bnf();
  @SuppressWarnings("static-method") public Test examples() {
    return new Test(new ExampleKind[] {});
  }
  public void generateGrammarFiles() throws IOException {
    Main.apiGenerator(bnf().go());
  }
  public void test() {
    // TODO Roth: complete
  }
}
