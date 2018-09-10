package roth.ori.fling.examples;

import static roth.ori.fling.examples.TestAnBnCD.NT.A;
import static roth.ori.fling.examples.TestAnBnCD.NT.B;
import static roth.ori.fling.examples.TestAnBnCD.NT.C;
import static roth.ori.fling.examples.TestAnBnCD.NT.D;
import static roth.ori.fling.examples.TestAnBnCD.NT.E;
import static roth.ori.fling.examples.TestAnBnCD.NT.F;
import static roth.ori.fling.examples.TestAnBnCD.NT.S;
import static roth.ori.fling.examples.TestAnBnCD.Term.a2;
import static roth.ori.fling.examples.TestAnBnCD.Term.b2;
import static roth.ori.fling.examples.TestAnBnCD.Term.c2;
import static roth.ori.fling.examples.TestAnBnCD.Term.d2;
import static roth.ori.fling.examples.TestAnBnCD.Term.e2;
import static roth.ori.fling.examples.TestAnBnCD.Term.f2;
import static roth.ori.fling.junk.TestAnBnCD.*;

import java.io.IOException;

import roth.ori.fling.api.Fajita;
import roth.ori.fling.api.Fajita.FajitaBNF;
import roth.ori.fling.api.Main;
import roth.ori.fling.export.Grammar;
import roth.ori.fling.symbols.NonTerminal;
import roth.ori.fling.symbols.Terminal;

public class TestAnBnCD extends Grammar {
  public static enum Term implements Terminal {
    a2, b2, c2, d2, e2, f2
  }

  public static enum NT implements NonTerminal {
    S, A, B, C, D, E, F
  }

  @Override public FajitaBNF bnf() {
    return Fajita.build(TestAnBnCD.class, Term.class, NT.class, "TestAnBnCD", Main.packagePath, Main.projectPath).start(S) //
        .derive(S).to(A, S, B).orNone() //
        .derive(A).to(C, a2, D) //
        .derive(B).to(E, b2, F) //
        .derive(C).to(c2).orNone() //
        .derive(D).to(d2).orNone() //
        .derive(E).to(e2).orNone() //
        .derive(F).to(f2).orNone();
  }
  public static void main(String[] args) throws IOException {
    new TestAnBnCD().generateGrammarFiles();
  }
  public static void testing() {
    a2().b2().$();
    a2().a2().b2().b2().$();
    a2().a2().a2().b2().b2().b2().$();
    a2().a2().a2().a2().b2().b2().b2().b2().$();
    a2().a2().a2().c2().a2().d2().e2().b2().f2().b2().b2().b2().$();
  }
}
