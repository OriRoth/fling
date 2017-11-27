package org.spartan.fajita.api.examples;

import static org.spartan.fajita.api.Fajita.buildBNF;
import static org.spartan.fajita.api.bnf.BNFRenderer.builtin.ASCII;
import static org.spartan.fajita.api.examples.SimpleGrammar.NT.S;
import static org.spartan.fajita.api.examples.SimpleGrammar.Term.a;
import static org.spartan.fajita.api.examples.SimpleGrammar.Term.b;

import java.io.IOException;
import java.util.Map;

import static org.spartan.fajita.api.junk.Simplegrammar.*;

import org.spartan.fajita.api.Fajita.Deriver;
import org.spartan.fajita.api.Main;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;

public class SimpleGrammar {
  private static final String apiName = "SimpleGrammar";

  static enum Term implements Terminal {
    a, b, c
  }

  static enum NT implements NonTerminal {
    S
  }

  public static Deriver bnf() {
    return buildBNF(Term.class, NT.class) //
        .setApiName(apiName) //
        .start(S) //
        .derive(S).to(a).and(S).and(b);
  }
  public static Map<String, String> generate() {
    return bnf().go(Main.packagePath);
  }
  public static void main(String[] args) throws IOException {
    System.out.println(bnf().go().toString(ASCII));
    Main.apiGenerator(generate());
  }
  static void test() {
    a().b();
  }
}
