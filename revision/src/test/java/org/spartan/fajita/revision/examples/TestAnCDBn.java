package org.spartan.fajita.revision.examples;

import static org.spartan.fajita.revision.api.Fajita.either;
import static org.spartan.fajita.revision.api.Fajita.noneOrMore;
import static org.spartan.fajita.revision.api.Fajita.option;
import static org.spartan.fajita.revision.examples.TestAnCDBn.NT.A;
import static org.spartan.fajita.revision.examples.TestAnCDBn.NT.B;
import static org.spartan.fajita.revision.examples.TestAnCDBn.NT.S;
import static org.spartan.fajita.revision.examples.TestAnCDBn.Term.a1;
import static org.spartan.fajita.revision.examples.TestAnCDBn.Term.b1;
import static org.spartan.fajita.revision.examples.TestAnCDBn.Term.c1;
import static org.spartan.fajita.revision.examples.TestAnCDBn.Term.d1;
import static org.spartan.fajita.revision.examples.TestAnCDBn.Term.e1;
import static org.spartan.fajita.revision.junk.TestAnCDBn.a1;

import java.io.IOException;

import org.spartan.fajita.revision.api.Fajita;
import org.spartan.fajita.revision.api.Fajita.FajitaBNF;
import org.spartan.fajita.revision.api.Main;
import org.spartan.fajita.revision.export.Grammar;
import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.Terminal;

public class TestAnCDBn extends Grammar {
  public static enum Term implements Terminal {
    a1, b1, c1, d1, e1
  }

  public static enum NT implements NonTerminal {
    S, A, B, C, D
  }

  @Override public FajitaBNF bnf() {
    return Fajita.build(TestAnCDBn.class, Term.class, NT.class, "TestAnCDBn", Main.packagePath, Main.projectPath).start(S) //
        .derive(S).to(A, S, noneOrMore(either(c1, d1), option(e1)), B).orNone() //
        .derive(A).to(a1) //
        .derive(B).to(b1);
  }
  public static void main(String[] args) throws IOException {
    new TestAnCDBn().generateGrammarFiles();
  }
  public static void testing() {
    a1().b1().$();
    a1().a1().b1().b1().$();
    a1().a1().a1().b1().b1().b1().$();
    a1().a1().a1().a1().b1().b1().b1().b1().$();
    a1().a1().a1().a1().d1().e1().b1().c1().b1().b1().b1().$();
  }
}
