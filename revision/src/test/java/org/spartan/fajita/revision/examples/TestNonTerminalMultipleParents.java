package org.spartan.fajita.revision.examples;

import static org.spartan.fajita.revision.api.Fajita.attribute;
import static org.spartan.fajita.revision.examples.TestNonTerminalMultipleParents.NT.A;
import static org.spartan.fajita.revision.examples.TestNonTerminalMultipleParents.NT.B;
import static org.spartan.fajita.revision.examples.TestNonTerminalMultipleParents.NT.C;
import static org.spartan.fajita.revision.examples.TestNonTerminalMultipleParents.NT.D;
import static org.spartan.fajita.revision.examples.TestNonTerminalMultipleParents.NT.S;
import static org.spartan.fajita.revision.examples.TestNonTerminalMultipleParents.Term.a;
import static org.spartan.fajita.revision.examples.TestNonTerminalMultipleParents.Term.b;

import java.io.IOException;

import org.spartan.fajita.revision.api.Fajita;
import org.spartan.fajita.revision.api.Fajita.FajitaBNF;
import org.spartan.fajita.revision.api.Main;
import org.spartan.fajita.revision.export.Grammar;
import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.Terminal;

public class TestNonTerminalMultipleParents extends Grammar {
  public static enum Term implements Terminal {
    a, b
  }

  public static enum NT implements NonTerminal {
    S, A, B, C, D
  }

  @Override public FajitaBNF bnf() {
    return Fajita.build(getClass(), Term.class, NT.class, "TestNonTerminalMultipleParents", Main.packagePath, Main.projectPath) //
        .start(S) //
        .derive(S).to(A, B) //
        .specialize(A).into(C, D) //
        .specialize(B).into(C, D) //
        .derive(C).to(attribute(a, String.class)) //
        .derive(D).to(attribute(b, String.class));
  }
  /**
   * @throws IOException
   */
  public static void main(String[] args) throws IOException {
    new TestNonTerminalMultipleParents().generateGrammarFiles();
    // test();
  }
  public static void test() {
  }
}
