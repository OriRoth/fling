package org.spartan.fajita.api.examples;

import static org.spartan.fajita.api.EFajita.oneOrMore;
import static org.spartan.fajita.api.examples.Simple.NT.S;
import static org.spartan.fajita.api.examples.Simple.Term.a;
import static org.spartan.fajita.api.examples.Simple.Term.b;

import java.io.IOException;
import java.util.Map;

import org.spartan.fajita.api.EFajita;
import org.spartan.fajita.api.EFajita.Deriver;
import org.spartan.fajita.api.Main;
import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;

import static org.spartan.fajita.api.junk.Simple.*;

@SuppressWarnings("unused") public class Simple {
  public static enum Term implements Terminal {
    a, b
  }

  public static enum NT implements NonTerminal {
    S
  }

  public static Deriver bnf() {
    return EFajita.build(Term.class, NT.class) //
        .setApiName("Simple") //
        .start(S) //
        .derive(S).to(oneOrMore(a).separator(b));
  }
  public static Map<String, String> buildBNF() {
    return bnf().go(Main.packagePath);
  }
  public static BNF simple() {
    return bnf().go();
  }
  public static void main(String[] args) throws IOException {
    // Main.apiGenerator(buildBNF());
    test();
  }
  static void test() {
    a() //
        .b() //
        .a() //
        .b() //
        .a() //
        .b() //
        .a();
  }
}
