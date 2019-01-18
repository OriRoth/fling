package roth.ori.fling.examples;

import static roth.ori.fling.examples.Parenthesis.NT.C;
import static roth.ori.fling.examples.Parenthesis.NT.O;
import static roth.ori.fling.examples.Parenthesis.NT.S;
import static roth.ori.fling.examples.Parenthesis.Term.o;
import static roth.ori.fling.examples.Parenthesis.Term.c;
import static roth.ori.fling.junk.Parenthesis.o;

import java.io.IOException;

import roth.ori.fling.api.Fling;
import roth.ori.fling.api.Fling.FlingBNF;
import roth.ori.fling.api.Main;
import roth.ori.fling.export.Grammar;
import roth.ori.fling.symbols.Symbol;
import roth.ori.fling.symbols.Terminal;

public class Parenthesis extends Grammar {
  public static enum Term implements Terminal {
    o, c
  }

  public static enum NT implements Symbol {
    S, O, C
  }

  @Override public FlingBNF bnf() {
    return Fling.build(Parenthesis.class, Term.class, NT.class, "Parenthesis", Main.packagePath, Main.projectPath).start(S) //
        .derive(S).to(O, S, C, S).orNone() //
        .derive(O).to(o) //
        .derive(C).to(c);
  }
  public static void main(String[] args) throws IOException {
    new Parenthesis().generateGrammarFiles();
  }
  public static void testing() {
    o().c().$();
    o().o().c().o().c().c().$();
  }
}
