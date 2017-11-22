package org.spartan.fajita.api.examples;

import java.io.IOException;
import java.util.Map;

import org.spartan.fajita.api.Fajita;
import org.spartan.fajita.api.Main;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Symbol;
import org.spartan.fajita.api.bnf.symbols.Terminal;

import static org.spartan.fajita.api.examples.EBNF.Term.*;
import static org.spartan.fajita.api.examples.EBNF.NT.*;
import static org.spartan.fajita.api.examples.EBNF.TestTerm.*;
import static org.spartan.fajita.api.examples.EBNF.TestNT.*;
import static org.spartan.fajita.api.junk.Ebnf.*;
import static org.spartan.fajita.api.junk.ExpansionHead.*;
import static org.spartan.fajita.api.junk.ExpansionHeadExpanded.*;

public class EBNF {
  private static final String apiName = "EBNF";

  static enum Term implements Terminal {
    derive, to, and, or, option, repeat, orNone, term
  }

  static enum NT implements NonTerminal {
    RULES, RULE, EXPANSION, OPTION, REPEAT, EXPANSION_START, EXPANSION_HEAD, EXPANSION_BODY, EXPANSION_HEAD_EXPANDED
  }

  public static Map<String, String> buildBNF() {
    return Fajita.buildBNF(Term.class, NT.class) //
        .setApiName(apiName) //
        .start(RULES) //
        .derive(RULES).to(RULE).and(RULES).orNone() //
        .derive(RULE).to(derive, NonTerminal.class).and(EXPANSION_START).and(EXPANSION) //
        .derive(EXPANSION_START).to(to, Symbol.class).or(to, EXPANSION_HEAD) //
        .derive(EXPANSION_HEAD).to(OPTION).or(REPEAT).or(term, Symbol.class) //
        .derive(EXPANSION_HEAD_EXPANDED).to(EXPANSION_HEAD).and(EXPANSION) //
        .derive(OPTION).to(option, Symbol.class).or(option, EXPANSION_HEAD) //
        .derive(REPEAT).to(repeat, Symbol.class).or(repeat, EXPANSION_HEAD) //
        .derive(EXPANSION).to(EXPANSION_BODY).and(EXPANSION).orNone() //
        .derive(EXPANSION_BODY) //
        /**/.to(and, Symbol.class).or(and, EXPANSION_HEAD_EXPANDED) //
        /**/.or(or, Symbol.class).or(or, EXPANSION_HEAD_EXPANDED) //
        /**/.or(option, Symbol.class).or(option, EXPANSION_HEAD_EXPANDED) //
        /**/.or(repeat, Symbol.class).or(repeat, EXPANSION_HEAD_EXPANDED) //
        /**/.or(orNone) //
        .go(Main.packagePath);
  }
  public static void main(String[] args) throws IOException {
    Main.apiGenerator(buildBNF());
  }

  static enum TestTerm implements Terminal {
    a, b
  }

  static enum TestNT implements NonTerminal {
    S, A, B
  }

  static void test() {
    derive(S).to(option(repeat(A))).and(B) //
        .derive(A).to(a) //
        .derive(B).to(b).or(term(a).and(option(b))).orNone();
  }
}
