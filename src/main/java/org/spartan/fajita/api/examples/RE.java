package org.spartan.fajita.api.examples;

import static org.spartan.fajita.api.examples.RE.NT.*;
import static org.spartan.fajita.api.examples.RE.Term.*;

import java.io.IOException;

import org.spartan.fajita.api.Fajita;
import org.spartan.fajita.api.Main;
import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;

public class RE {
  static enum Term implements Terminal {
    range, to, oneOf, chars
  }


  static enum NT implements NonTerminal {
    RANGE, RANGES, ONE_OF
    , START
  }

  public static BNF buildBNF() {
    BNF b = Fajita.buildBNF(Term.class, NT.class) //
        .setApiName("Regex") //
        .start(START) //
        .derive(START).to(ONE_OF) //
        .derive(ONE_OF).to(oneOf, RANGE, RANGES) //
        .go();
    return b;
  }
  public static void main(String[] args) throws IOException {
    Main.apiGenerator(buildBNF());
  }
  public static void expressionBuilder() {
    // compiles
    // does not compile
  }
}
