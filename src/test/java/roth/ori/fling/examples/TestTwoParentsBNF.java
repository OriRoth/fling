package roth.ori.fling.examples;

import static roth.ori.fling.examples.TestTwoParentsBNF.NT.A;
import static roth.ori.fling.examples.TestTwoParentsBNF.NT.B;
import static roth.ori.fling.examples.TestTwoParentsBNF.NT.C;
import static roth.ori.fling.examples.TestTwoParentsBNF.NT.D;
import static roth.ori.fling.examples.TestTwoParentsBNF.NT.S;
import static roth.ori.fling.examples.TestTwoParentsBNF.Term.a;
import static roth.ori.fling.examples.TestTwoParentsBNF.Term.b;

import java.io.IOException;

import roth.ori.fling.api.Fajita;
import roth.ori.fling.api.Fajita.FajitaBNF;
import roth.ori.fling.api.Main;
import roth.ori.fling.export.Grammar;
import roth.ori.fling.symbols.NonTerminal;
import roth.ori.fling.symbols.Terminal;

public class TestTwoParentsBNF extends Grammar {
  public static enum Term implements Terminal {
    a, b
  }

  public static enum NT implements NonTerminal {
    S, A, B, C, D
  }

  @Override public FajitaBNF bnf() {
    return Fajita.build(TestTwoParentsBNF.class, Term.class, NT.class, "TestTwoParentsBNF", Main.packagePath, Main.projectPath)
        .start(S) //
        .derive(S).to(A).and(B) //
        .derive(A).to(C).or(D) //
        .derive(B).to(C).or(D) //
        .derive(C).to(a) //
        .derive(D).to(b);
  }
  public static void main(String[] args) throws IOException {
    new TestTwoParentsBNF().generateGrammarFiles();
  }
}
