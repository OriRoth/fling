package org.spartan.fajita.revision.examples;

import static org.spartan.fajita.revision.api.Fajita.attribute;
import static org.spartan.fajita.revision.api.Fajita.oneOrMore;
import static org.spartan.fajita.revision.examples.TestInclusiveExtendibles.NT.S;
import static org.spartan.fajita.revision.examples.TestInclusiveExtendibles.Term.a;
import static org.spartan.fajita.revision.examples.TestInclusiveExtendibles.Term.b;

import static org.spartan.fajita.revision.junk.TestInclusiveExtendibles.a;

import java.io.IOException;

import org.spartan.fajita.revision.api.Fajita;
import org.spartan.fajita.revision.api.Fajita.FajitaBNF;
import org.spartan.fajita.revision.api.Main;
import org.spartan.fajita.revision.export.Grammar;
import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.Terminal;

public class TestInclusiveExtendibles extends Grammar {
  public static enum Term implements Terminal {
    a, b
  }

  public static enum NT implements NonTerminal {
    S
  }

  @Override public FajitaBNF bnf() {
    return Fajita.build(getClass(), Term.class, NT.class, "TestInclusiveExtendibles", Main.packagePath, Main.projectPath) //
        .start(S) //
        .derive(S).to(oneOrMore(oneOrMore(attribute(a, String.class)), attribute(b, String.class)));
  }
  /**
   * @throws IOException
   */
  public static void main(String[] args) throws IOException {
    // new TestInclusiveExtendibles().generateGrammarFiles();
    test();
  }
  public static void test() {
    a("a11").a("a12").b("b1").a("a21").a("a22").b("b2").$();
  }
}
