package roth.ori.fling.examples;

import static roth.ori.fling.api.Fling.either;
import static roth.ori.fling.api.Fling.noneOrMore;
import static roth.ori.fling.api.Fling.option;
import static roth.ori.fling.examples.TestAnCDBn.NT.A;
import static roth.ori.fling.examples.TestAnCDBn.NT.B;
import static roth.ori.fling.examples.TestAnCDBn.NT.S;
import static roth.ori.fling.examples.TestAnCDBn.Term.a1;
import static roth.ori.fling.examples.TestAnCDBn.Term.b1;
import static roth.ori.fling.examples.TestAnCDBn.Term.c1;
import static roth.ori.fling.examples.TestAnCDBn.Term.d1;
import static roth.ori.fling.examples.TestAnCDBn.Term.e1;
import static roth.ori.fling.junk.TestAnCDBn.a1;

import java.io.IOException;

import roth.ori.fling.api.Fling;
import roth.ori.fling.api.Fling.FlingBNF;
import roth.ori.fling.api.Main;
import roth.ori.fling.export.Grammar;
import roth.ori.fling.symbols.Symbol;
import roth.ori.fling.symbols.Terminal;

public class TestAnCDBn extends Grammar {
  public static enum Term implements Terminal {
    a1, b1, c1, d1, e1
  }

  public static enum NT implements Symbol {
    S, A, B, C, D
  }

  @Override public FlingBNF bnf() {
    return Fling.build(TestAnCDBn.class, Term.class, NT.class, "TestAnCDBn", Main.packagePath, Main.projectPath).start(S) //
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
