package roth.ori.fling.examples;

import static roth.ori.fling.examples.TestForceEpsilonMove.NT.*;
import static roth.ori.fling.examples.TestForceEpsilonMove.Term.*;

import java.io.IOException;

import roth.ori.fling.api.Fling;
import roth.ori.fling.api.Fling.FlingBNF;
import roth.ori.fling.api.Main;
import roth.ori.fling.export.Grammar;
import roth.ori.fling.symbols.Symbol;
import roth.ori.fling.symbols.Terminal;

public class TestForceEpsilonMove extends Grammar {
  public static enum Term implements Terminal {
    a, b, c, d
  }

  public static enum NT implements Symbol {
    S, SA, SB, AA, BB
  }

  @Override public FlingBNF bnf() {
    return Fling
        .build(TestForceEpsilonMove.class, Term.class, NT.class, "TestForceEpsilonMove", Main.packagePath, Main.projectPath)
        .start(S) //
        .derive(S).to(a, SA, a).or(a, SB) //
        .derive(SA).to(a, AA, a).or(b, AA) //
        .derive(AA).to(b, AA).or(c) //
        .derive(SB).to(a, SB).or(b, BB, b) //
        .derive(BB).to(b, BB, b).or(d);
  }
  public static void main(String[] args) throws IOException {
    new TestForceEpsilonMove().generateGrammarFiles();
  }
}
