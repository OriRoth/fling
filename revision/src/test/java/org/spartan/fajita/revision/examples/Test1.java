package org.spartan.fajita.revision.examples;

import static org.spartan.fajita.revision.api.Fajita.attribute;
import static org.spartan.fajita.revision.examples.Test1.NT.*;
import static org.spartan.fajita.revision.examples.Test1.Term.*;
import static org.spartan.fajita.revision.junk.Test1.a;
import static org.spartan.fajita.revision.junk.A.b;

import java.io.IOException;

import org.spartan.fajita.revision.api.Fajita;
import org.spartan.fajita.revision.api.Fajita.FajitaBNF;
import org.spartan.fajita.revision.api.Main;
import org.spartan.fajita.revision.export.Grammar;
import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.Terminal;

public class Test1 extends Grammar {
  public static enum Term implements Terminal {
    a, b
  }

  public static enum NT implements NonTerminal {
    S, A
  }

  @Override public FajitaBNF bnf() {
    return Fajita.build(getClass(), Term.class, NT.class, "Test1", Main.packagePath, Main.projectPath) //
        .start(S) //
        .derive(S).to(attribute(a, A, A)) //
        .derive(A).to(attribute(b, int.class), attribute(b, int.class));
  }
  /**
   * @throws IOException
   */
  public static void main(String[] args) throws IOException {
    // System.out.println(bnf().go().toString(ASCII));
    // new Test1().generateGrammarFiles();
    test();
  }
  static void test() {
    System.out.println(a(b(1).b(2), b(3).b(4)));
  }
}
