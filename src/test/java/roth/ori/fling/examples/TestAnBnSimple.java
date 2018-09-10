package roth.ori.fling.examples;

import static roth.ori.fling.examples.TestAnBnSimple.NT.S;
import static roth.ori.fling.examples.TestAnBnSimple.Term.*;
import static roth.ori.fling.junk.TestAnBnSimple.*;

import java.io.IOException;

import roth.ori.fling.api.Fajita;
import roth.ori.fling.api.Fajita.FajitaBNF;
import roth.ori.fling.api.Main;
import roth.ori.fling.export.Grammar;
import roth.ori.fling.symbols.NonTerminal;
import roth.ori.fling.symbols.Terminal;

public class TestAnBnSimple extends Grammar {
  public static enum Term implements Terminal {
    g, h
  }

  public static enum NT implements NonTerminal {
    S
  }

  @Override public FajitaBNF bnf() {
    return Fajita.build(TestAnBnSimple.class, Term.class, NT.class, "TestAnBnSimple", Main.packagePath, Main.projectPath) //
        .start(S) //
        .derive(S).to(g, S, h).orNone();
  }
  public static void main(String[] args) throws IOException {
    new TestAnBnSimple().generateGrammarFiles();
  }
  public static void testing() {
    g().h().$();
    g().g().h().h().$();
    g().g().g().h().h().h().$();
    g().g().g().g().h().h().h().h().$();
  }
}
