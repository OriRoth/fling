package org.spartan.fajita.api.examples;

import static org.spartan.fajita.api.EFajita.build;
import static org.spartan.fajita.api.examples.Simple.NT.S;
import static org.spartan.fajita.api.examples.Simple.NT.B;
import static org.spartan.fajita.api.examples.Simple.Term.a;
import static org.spartan.fajita.api.examples.Simple.Term.b;

import java.io.IOException;
import java.util.Map;

import org.spartan.fajita.api.EFajita.Deriver;
import org.spartan.fajita.api.Main;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;

import static org.spartan.fajita.api.junk.Simple.a;

public class Simple {
  private static final String apiName = "Simple";

  static enum Term implements Terminal {
    a, b
  }

  static enum NT implements NonTerminal {
    S, B
  }

  public static Deriver bnf() {
    return build(Term.class, NT.class) //
        .setApiName(apiName) //
        .start(S) //
        .derive(S).to(a, B, S) //
        .derive(B).to(b, B).orNone();
  }
  public static Map<String, String> buildBNF() {
    return bnf().go(Main.packagePath);
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
        .b() //
        .a() //
        .b() //
        .b() //
        .b() //
        .a() //
        .b();
  }
}
