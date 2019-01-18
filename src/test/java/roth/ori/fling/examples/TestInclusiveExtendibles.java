package roth.ori.fling.examples;

import static roth.ori.fling.api.Fling.attribute;
import static roth.ori.fling.api.Fling.either;
import static roth.ori.fling.api.Fling.noneOrMore;
import static roth.ori.fling.api.Fling.oneOrMore;
import static roth.ori.fling.api.Fling.option;
import static roth.ori.fling.examples.TestInclusiveExtendibles.NT.S;
import static roth.ori.fling.examples.TestInclusiveExtendibles.Term.a;
import static roth.ori.fling.examples.TestInclusiveExtendibles.Term.b;
import static roth.ori.fling.examples.TestInclusiveExtendibles.Term.c;
import static roth.ori.fling.examples.TestInclusiveExtendibles.Term.d;
import static roth.ori.fling.junk.TestInclusiveExtendibles.a;

import java.io.IOException;

import roth.ori.fling.api.Fling;
import roth.ori.fling.api.Fling.FlingBNF;
import roth.ori.fling.api.Main;
import roth.ori.fling.export.Grammar;
import roth.ori.fling.symbols.Symbol;
import roth.ori.fling.symbols.Terminal;

// TODO Roth: extend checks
public class TestInclusiveExtendibles extends Grammar {
  public static enum Term implements Terminal {
    a, b, c, d
  }

  public static enum NT implements Symbol {
    S
  }

  @Override public FlingBNF bnf() {
    return Fling.build(getClass(), Term.class, NT.class, "TestInclusiveExtendibles", Main.packagePath, Main.projectPath) //
        .start(S) //
        .derive(S).to( //
            oneOrMore(noneOrMore(attribute(a, String.class)), //
                either(attribute(b, Integer.class, String.class), attribute(c, String.class)), //
                option(attribute(d, Character.class))));
  }
  /**
   * @throws IOException
   */
  public static void main(String[] args) throws IOException {
    new TestInclusiveExtendibles().generateGrammarFiles();
    // testing();
  }
  @SuppressWarnings("boxing") public static void testing() {
    a("a11").a("a12").c("b1").b(0, "!").$();
  }
}
