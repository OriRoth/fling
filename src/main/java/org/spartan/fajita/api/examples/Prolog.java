package org.spartan.fajita.api.examples;

import java.io.IOException;
import java.util.Map;

import org.spartan.fajita.api.Fajita;
import org.spartan.fajita.api.Main;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;

import static org.spartan.fajita.api.examples.Prolog.Term.*;
import static org.spartan.fajita.api.examples.Prolog.NT.*;

public class Prolog {
  private static final String apiName = "Prolog";

  static enum Term implements Terminal {
    head, body, fact, literal, functor, arguments, term, atom, numeric, variable, list, empty, emptyList, then, concat
  }

  static enum NT implements NonTerminal {
    CLAUSES, CLAUSE, BODY, TERMS, TERMS_OR_NONE, TERM, COMPOUND, LIST, LIST_CONTENTS, LIST_CONTENT, CONCATENATION
  }

  public static Map<String, String> buildBNF() {
    return Fajita.buildBNF(Term.class, NT.class) //
        .setApiName(apiName) //
        .start(CLAUSES) //
        .derive(CLAUSES).to(CLAUSE).and(CLAUSES).orNone() //
        .derive(CLAUSE) //
        /**/.to(fact, TERM) //
        /**/.or(head, TERM).and(BODY) //
        .derive(BODY).to(body, TERMS) //
        .derive(TERMS).to(TERM).and(TERMS_OR_NONE) //
        .derive(TERMS_OR_NONE).to(TERM).and(TERMS_OR_NONE).orNone() //
        .derive(TERM).to(COMPOUND).or(atom, String.class).or(variable, String.class).or(numeric, Double.class).or(LIST).or(empty) //
        .derive(COMPOUND).to(functor, String.class).and(arguments, TERMS) //
        .derive(LIST).to(empty).or(list, LIST_CONTENTS) //
        .derive(LIST_CONTENTS) //
        /**/.to(LIST_CONTENT).and(then).and(LIST_CONTENTS) //
        /**/.or(LIST_CONTENT).and(concat, CONCATENATION) //
        /**/.orNone() //
        .derive(LIST_CONTENT).to(emptyList).or(empty).or(variable, String.class) //
        .derive(CONCATENATION).to(variable, String.class).or(empty) //
        .go(Main.packagePath);
  }
  public static void main(String[] args) throws IOException {
    Main.apiGenerator(buildBNF());
  }
}
