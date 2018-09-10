package roth.ori.fling.examples;

import static roth.ori.fling.api.Fajita.oneOrMore;
import static roth.ori.fling.api.Fajita.option;
import static roth.ori.fling.examples.TestJumps.NT.S;
import static roth.ori.fling.examples.TestJumps.Term.x;
import static roth.ori.fling.examples.TestJumps.Term.y;
import static roth.ori.fling.junk.TestJumps.x;

import java.io.IOException;

import roth.ori.fling.api.Fajita;
import roth.ori.fling.api.Fajita.FajitaBNF;
import roth.ori.fling.api.Main;
import roth.ori.fling.export.Grammar;
import roth.ori.fling.symbols.NonTerminal;
import roth.ori.fling.symbols.Terminal;

public class TestJumps extends Grammar {
  public static enum Term implements Terminal {
    x, y
  }

  public static enum NT implements NonTerminal {
    S
  }

  @Override public FajitaBNF bnf() {
    return Fajita.build(getClass(), Term.class, NT.class, "TestJumps", Main.packagePath, Main.projectPath) //
        .start(S) //
        .derive(S).to(x, oneOrMore(y, option(x)));
  }
  public static void main(String[] args) throws IOException {
    new TestJumps().generateGrammarFiles();
  }
  public static void testing() {
    x().y().y().x().y().x().$();
  }
}
