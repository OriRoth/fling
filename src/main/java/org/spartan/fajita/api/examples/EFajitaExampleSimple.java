package org.spartan.fajita.api.examples;

import static org.spartan.fajita.api.bnf.BNFRenderer.builtin.*;
import static org.spartan.fajita.api.examples.EFajitaExampleSimple.NT.*;
import static org.spartan.fajita.api.examples.EFajitaExampleSimple.Term.*;
import static org.spartan.fajita.api.bnf.symbols.Predefined.*;

import java.util.Map;

import static org.spartan.fajita.api.EFajita.*;
import org.spartan.fajita.api.EFajita.Deriver;
import org.spartan.fajita.api.Main;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;

public class EFajitaExampleSimple {
  private static final String apiName = "EFajitaExampleSimple";

  static enum Term implements Terminal {
    a
  }

  static enum NT implements NonTerminal {
    S
  }

  public static Deriver bnf() {
    return build(Term.class, NT.class) //
        .setApiName(apiName) //
        .start(S) //
        .derive(S).to(either(Text, attribute(a, S)));
  }
  public static Map<String, String> buildBNF() {
    return bnf().go(Main.packagePath);
  }
  public static void main(String[] args) {
    System.out.println(bnf().go().toString(ASCII));
    System.out.println(bnf().go().toString(JAMOOS_CLASSES));
    System.out.println(bnf().go().toString(JAMOOS_EITHER));
  }
}
