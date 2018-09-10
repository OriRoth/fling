package roth.ori.fling.examples;

import static roth.ori.fling.junk.TestFinite.*;
import static roth.ori.fling.api.Fling.*;
import static roth.ori.fling.examples.TestFinite.NT.S;
import static roth.ori.fling.examples.TestFinite.Term.*;

import java.io.IOException;

import roth.ori.fling.api.Fling;
import roth.ori.fling.api.Fling.FlingBNF;
import roth.ori.fling.api.Main;
import roth.ori.fling.export.Grammar;
import roth.ori.fling.symbols.NonTerminal;
import roth.ori.fling.symbols.Terminal;

public class TestFinite extends Grammar {
  public static enum Term implements Terminal {
    m, n, o
  }

  public static enum NT implements NonTerminal {
    S
  }

  @Override public FlingBNF bnf() {
    return Fling.build(TestFinite.class, Term.class, NT.class, "TestFinite", Main.packagePath, Main.projectPath) //
        .start(S) //
        .derive(S).to(option(m), either(n, o)).orNone();
  }
  public static void main(String[] args) throws IOException {
    new TestFinite().generateGrammarFiles();
  }
  public static void testing() {
    n().$();
    m().n().$();
    o().$();
    m().o().$();
  }
}
