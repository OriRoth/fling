package roth.ori.fling.examples;

import static roth.ori.fling.examples.Exp2.NT.*;
import static roth.ori.fling.examples.Exp2.Term.*;

import java.io.IOException;

import roth.ori.fling.api.Fling;
import roth.ori.fling.api.Fling.FlingBNF;
import roth.ori.fling.api.Main;
import roth.ori.fling.export.Grammar;
import roth.ori.fling.symbols.Symbol;
import roth.ori.fling.symbols.Terminal;

public class Exp2 extends Grammar {
  public static enum Term implements Terminal {
    a, b, c
  }

  public static enum NT implements Symbol {
    S
  }

  @Override public FlingBNF bnf() {
    return Fling.build(Exp2.class, Term.class, NT.class, "Exp2", Main.packagePath, Main.projectPath) //
        .start(S) //
        .derive(S).to(a, S, b, S, c).orNone() //
    ;
  }
  public static void main(String[] args) throws IOException {
    new Exp2().generateGrammarFiles();
  }
}
