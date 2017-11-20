package org.spartan.fajita.api.examples;

import java.io.IOException;
import java.util.Map;

import org.spartan.fajita.api.Fajita;
import org.spartan.fajita.api.Main;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;

import static org.spartan.fajita.api.examples.PrologDomains.Term.*;
import static org.spartan.fajita.api.examples.PrologDomains.NT.*;

public class PrologDomains {
  private static final String apiName = "PrologDomains";

  static enum Term implements Terminal {
    domain, name, is, functor, list, or, and
  }

  static enum NT implements NonTerminal {
    DOMAINS, DOMAIN, DEFINITIONS, DEFINITIONS_OR_NONE, DEFINITION, TERMS, TERMS_OR_NONE, TERM
  }

  public static Map<String, String> buildBNF() {
    return Fajita.buildBNF(Term.class, NT.class) //
        .setApiName(apiName) //
        .start(DOMAINS) //
        .derive(DOMAINS).to(DOMAIN).and(DOMAINS).orNone() //
        .derive(DOMAIN).to(name, String.class).and(is).and(DEFINITIONS) //
        .derive(DEFINITIONS).to(DEFINITION).and(DEFINITIONS_OR_NONE) //
        .derive(DEFINITIONS_OR_NONE).to(or).and(DEFINITION).and(DEFINITIONS_OR_NONE).orNone() //
        .derive(DEFINITION).to(list, String.class).or(name, String.class).or(functor, String.class).and(TERMS) //
        .derive(TERMS).to(TERM).and(TERMS_OR_NONE) //
        .derive(TERMS_OR_NONE).to(and).and(TERM).and(TERMS_OR_NONE).orNone() //
        .derive(TERM).to(DEFINITION) //
        .go(Main.packagePath);
  }
  public static void main(String[] args) throws IOException {
    Main.apiGenerator(buildBNF());
  }
}
