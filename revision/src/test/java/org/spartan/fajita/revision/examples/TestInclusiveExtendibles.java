package org.spartan.fajita.revision.examples;

import static org.spartan.fajita.revision.api.Fajita.attribute;
import static org.spartan.fajita.revision.api.Fajita.either;
import static org.spartan.fajita.revision.api.Fajita.noneOrMore;
import static org.spartan.fajita.revision.api.Fajita.oneOrMore;
import static org.spartan.fajita.revision.api.Fajita.option;
import static org.spartan.fajita.revision.examples.TestInclusiveExtendibles.NT.S;
import static org.spartan.fajita.revision.examples.TestInclusiveExtendibles.Term.a;
import static org.spartan.fajita.revision.examples.TestInclusiveExtendibles.Term.b;
import static org.spartan.fajita.revision.examples.TestInclusiveExtendibles.Term.c;
import static org.spartan.fajita.revision.examples.TestInclusiveExtendibles.Term.d;
import static org.spartan.fajita.revision.junk.TestInclusiveExtendibles.a;

import java.io.IOException;

import org.spartan.fajita.revision.api.Fajita;
import org.spartan.fajita.revision.api.Fajita.FajitaBNF;
import org.spartan.fajita.revision.api.Main;
import org.spartan.fajita.revision.export.Grammar;
import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.Terminal;

// TODO Roth: extend checks
public class TestInclusiveExtendibles extends Grammar {
  public static enum Term implements Terminal {
    a, b, c, d
  }

  public static enum NT implements NonTerminal {
    S
  }

  @Override public FajitaBNF bnf() {
    return Fajita.build(getClass(), Term.class, NT.class, "TestInclusiveExtendibles", Main.packagePath, Main.projectPath) //
        .start(S) //
        .derive(S).to( //
            oneOrMore(noneOrMore(attribute(a, String.class)), //
                either(attribute(b, Integer.class, String.class), attribute(c, String.class)), //
                option(attribute(d, Character.class))));
  }
  /**
   * @throws IOException
   */
  public static void main(String[] args) throws IOException {
    new TestInclusiveExtendibles().generateGrammarFiles();
    // testing();
  }
  @SuppressWarnings("boxing") public static void testing() {
    a("a11").a("a12").c("b1").b(0, "!").$();
  }
}
