package org.spartan.fajita.api.examples;

import static org.spartan.fajita.api.EFajita.attribute;
import static org.spartan.fajita.api.EFajita.build;
import static org.spartan.fajita.api.EFajita.noneOrMore;
import static org.spartan.fajita.api.bnf.BNFRenderer.builtin.ASCII;
import static org.spartan.fajita.api.bnf.BNFRenderer.builtin.JAMOOS_CLASSES;
import static org.spartan.fajita.api.bnf.BNFRenderer.builtin.JAMOOS_EITHER;
import static org.spartan.fajita.api.examples.EFajitaExampleSimple.NT.S;
import static org.spartan.fajita.api.examples.EFajitaExampleSimple.Term.a;
import static org.spartan.fajita.api.examples.EFajitaExampleSimple.Term.b;
import static org.spartan.fajita.api.examples.EFajitaExampleSimple.Term.c;
import static org.spartan.fajita.api.examples.EFajitaExampleSimple.Term.d;
import static org.spartan.fajita.api.examples.EFajitaExampleSimple.Term.e;
import static org.spartan.fajita.api.examples.EFajitaExampleSimple.Term.f;

import java.util.Map;

import org.spartan.fajita.api.EFajita.FajitaBNF;
import org.spartan.fajita.api.Main;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;

public class EFajitaExampleSimple {
  private static final String apiName = "EFajitaExampleSimple";

  static enum Term implements Terminal {
    a, b, c, d, e, f
  }

  static enum NT implements NonTerminal {
    S
  }

  public static FajitaBNF bnf() {
    return build(Term.class, NT.class) //
        .setApiName(apiName) //
        .start(S) //
        .derive(S).to(noneOrMore(attribute(a, String.class), attribute(b, String.class)) //
            .separator(attribute(c, String.class), attribute(d, String.class)) //
            .ifNone(attribute(e, String.class), attribute(f, String.class)) //
    );
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
