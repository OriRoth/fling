package roth.ori.fling.examples;

import static roth.ori.fling.examples.TestAnBn.NT.A;
import static roth.ori.fling.examples.TestAnBn.NT.B;
import static roth.ori.fling.examples.TestAnBn.NT.S;
import static roth.ori.fling.examples.TestAnBn.Term.a;
import static roth.ori.fling.examples.TestAnBn.Term.b;
import static roth.ori.fling.junk.TestAnBn.a;

import java.io.IOException;

import roth.ori.fling.api.Fling;
import roth.ori.fling.api.Fling.FlingBNF;
import roth.ori.fling.api.Main;
import roth.ori.fling.export.Grammar;
import roth.ori.fling.symbols.Symbol;
import roth.ori.fling.symbols.Terminal;

public class TestAnBn extends Grammar {
  public static enum Term implements Terminal {
    a, b
  }

  public static enum NT implements Symbol {
    S, A, B
  }

  @Override public FlingBNF bnf() {
    return Fling.build(TestAnBn.class, Term.class, NT.class, "TestAnBn", Main.packagePath, Main.projectPath).start(S) //
        .derive(S).to(A, S, B).orNone() //
        .derive(A).to(a) //
        .derive(B).to(b);
  }
  public static void main(String[] args) throws IOException {
    new TestAnBn().generateGrammarFiles();
  }
  public static void testing() {
    a().b().$();
    a().a().b().b().$();
    a().a().a().b().b().b().$();
    a().a().a().a().b().b().b().b().$();
  }
}
