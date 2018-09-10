package roth.ori.fling.examples;

import static roth.ori.fling.examples.ParenthesisSimple.NT.S;
import static roth.ori.fling.examples.ParenthesisSimple.Term.c;
import static roth.ori.fling.examples.ParenthesisSimple.Term.o;
import static roth.ori.fling.junk.ParenthesisSimple.o;

import java.io.IOException;

import roth.ori.fling.api.Fling;
import roth.ori.fling.api.Fling.FlingBNF;
import roth.ori.fling.api.Main;
import roth.ori.fling.export.Grammar;
import roth.ori.fling.symbols.NonTerminal;
import roth.ori.fling.symbols.Terminal;

public class ParenthesisSimple extends Grammar {
  public static enum Term implements Terminal {
    o, c
  }

  public static enum NT implements NonTerminal {
    S
  }

  @Override public FlingBNF bnf() {
    return Fling.build(ParenthesisSimple.class, Term.class, NT.class, "ParenthesisSimple", Main.packagePath, Main.projectPath)
        .start(S) //
        .derive(S).to(o, S, c, S).orNone();
  }
  public static void main(String[] args) throws IOException {
    new ParenthesisSimple().generateGrammarFiles();
  }
  public static void testing() {
    o().c().$();
    o().o().c().o().c().c().$();
  }
}
