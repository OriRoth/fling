package org.spartan.fajita.api.examples;

import static org.spartan.fajita.api.bnf.BNFRenderer.builtin.*;
import static org.spartan.fajita.api.examples.EFajitaExample.NT.*;
import static org.spartan.fajita.api.examples.EFajitaExample.Term.*;

import java.util.Map;

import static org.spartan.fajita.api.EFajita.*;
import org.spartan.fajita.api.EFajita.Deriver;
import org.spartan.fajita.api.Main;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;

public class EFajitaExample {
  private static final String apiName = "EFajitaExample";

  static enum Term implements Terminal {
    a, b, c, d
  }

  static enum NT implements NonTerminal {
    S, A, B, C, D, E
  }

  public static Deriver bnf() {
    return build(Term.class, NT.class) //
        .setApiName(apiName) //
        .start(S) //
        .derive(S).to(either(A, B), concat(A, B), C, D) //
        .derive(A).to(a).and(b, optional(attribute(c, E), d)) //
        .derive(B).to(b).or(c, attribute(d, A)) //
        .derive(C).to(oneOrMore(a, b).separator(optional(c))) //
        .derive(D).to(noneOrMore(a).separator(b).ifNone(c)) //
        .derive(E).to(optional(a));
  }
  public static Map<String, String> buildBNF() {
    return bnf().go(Main.packagePath);
  }
  public static void main(String[] args) {
//    System.out.println(bnf().go().toString(ASCII));
    System.out.println(bnf().go().toString(JAMOOS_CLASSES));
  }
}
