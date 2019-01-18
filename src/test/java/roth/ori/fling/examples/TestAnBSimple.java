package roth.ori.fling.examples;

import static roth.ori.fling.examples.TestAnBSimple.NT.S;
import static roth.ori.fling.examples.TestAnBSimple.NT.S1;
import static roth.ori.fling.examples.TestAnBSimple.Term.*;
import static roth.ori.fling.junk.TestAnBSimple.*;

import java.io.IOException;

import roth.ori.fling.api.Fling;
import roth.ori.fling.api.Fling.FlingBNF;
import roth.ori.fling.api.Main;
import roth.ori.fling.export.Grammar;
import roth.ori.fling.symbols.Symbol;
import roth.ori.fling.symbols.Terminal;

public class TestAnBSimple extends Grammar {
  public static enum Term implements Terminal {
    e, f
  }

  public static enum NT implements Symbol {
    S, S1
  }

  @Override public FlingBNF bnf() {
    return Fling.build(TestAnBSimple.class, Term.class, NT.class, "TestAnBSimple", Main.packagePath, Main.projectPath).start(S) //
        .derive(S).to(S1, f) //
        .derive(S1).to(e, S1).orNone();
  }
  public static void main(String[] args) throws IOException {
    new TestAnBSimple().generateGrammarFiles();
  }
  public static void testing() {
    e().f().$();
    e().e().f().$();
    e().e().e().f().$();
    e().e().e().e().f().$();
  }
}
