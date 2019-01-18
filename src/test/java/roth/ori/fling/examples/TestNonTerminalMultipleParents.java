package roth.ori.fling.examples;

import static roth.ori.fling.api.Fling.attribute;
import static roth.ori.fling.examples.TestNonTerminalMultipleParents.NT.A;
import static roth.ori.fling.examples.TestNonTerminalMultipleParents.NT.B;
import static roth.ori.fling.examples.TestNonTerminalMultipleParents.NT.C;
import static roth.ori.fling.examples.TestNonTerminalMultipleParents.NT.D;
import static roth.ori.fling.examples.TestNonTerminalMultipleParents.NT.S;
import static roth.ori.fling.examples.TestNonTerminalMultipleParents.Term.a;
import static roth.ori.fling.examples.TestNonTerminalMultipleParents.Term.b;
import static roth.ori.fling.junk.TestNonTerminalMultipleParents.*;

import java.io.IOException;

import roth.ori.fling.api.Fling;
import roth.ori.fling.api.Fling.FlingBNF;
import roth.ori.fling.api.Main;
import roth.ori.fling.export.Grammar;
import roth.ori.fling.symbols.Symbol;
import roth.ori.fling.symbols.Terminal;

public class TestNonTerminalMultipleParents extends Grammar {
  public static enum Term implements Terminal {
    a, b
  }

  public static enum NT implements Symbol {
    S, A, B, C, D
  }

  @Override public FlingBNF bnf() {
    return Fling.build(getClass(), Term.class, NT.class, "TestNonTerminalMultipleParents", Main.packagePath, Main.projectPath) //
        .start(S) //
        .derive(S).to(A, B) //
        .specialize(A).into(C, D) //
        .specialize(B).into(C, D) //
        .derive(C).to(attribute(a, String.class)) //
        .derive(D).to(attribute(b, String.class));
  }
  /**
   * @throws IOException
   */
  public static void main(String[] args) throws IOException {
    new TestNonTerminalMultipleParents().generateGrammarFiles();
    // testing();
  }
  public static void testing() {
    a("a1").b("b1").$();
    b("b2").a("a2").$();
  }
}
