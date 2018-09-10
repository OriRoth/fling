package roth.ori.fling.examples;

import static roth.ori.fling.examples.TestAnB.NT.C;
import static roth.ori.fling.examples.TestAnB.NT.D;
import static roth.ori.fling.examples.TestAnB.NT.S;
import static roth.ori.fling.examples.TestAnB.NT.S1;
import static roth.ori.fling.examples.TestAnB.Term.c;
import static roth.ori.fling.examples.TestAnB.Term.d;
import static roth.ori.fling.junk.TestAnB.*;

import java.io.IOException;

import roth.ori.fling.api.Fajita;
import roth.ori.fling.api.Fajita.FajitaBNF;
import roth.ori.fling.api.Main;
import roth.ori.fling.export.Grammar;
import roth.ori.fling.symbols.NonTerminal;
import roth.ori.fling.symbols.Terminal;

public class TestAnB extends Grammar {
  public static enum Term implements Terminal {
    c, d
  }

  public static enum NT implements NonTerminal {
    S, S1, C, D
  }

  @Override public FajitaBNF bnf() {
    return Fajita.build(TestAnB.class, Term.class, NT.class, "TestAnB", Main.packagePath, Main.projectPath).start(S) //
        .derive(S).to(S1, D) //
        .derive(S1).to(C, S1).orNone() //
        .derive(C).to(c) //
        .derive(D).to(d);
  }
  public static void main(String[] args) throws IOException {
    new TestAnB().generateGrammarFiles();
  }
  public static void testing() {
    c().d().$();
    c().c().d().$();
    c().c().c().d().$();
    c().c().c().c().d().$();
  }
}
