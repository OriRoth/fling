package org.spartan.fajita.revision.examples;

import static org.spartan.fajita.revision.api.Fajita.attribute;
import static org.spartan.fajita.revision.api.Fajita.oneOrMore;
import static org.spartan.fajita.revision.examples.Datalog.NT.Body;
import static org.spartan.fajita.revision.examples.Datalog.NT.Program;
import static org.spartan.fajita.revision.examples.Datalog.NT.Literal;
import static org.spartan.fajita.revision.examples.Datalog.NT.Rule;
import static org.spartan.fajita.revision.examples.Datalog.Term.body;
import static org.spartan.fajita.revision.examples.Datalog.Term.fact;
import static org.spartan.fajita.revision.examples.Datalog.Term.head;
import static org.spartan.fajita.revision.examples.Datalog.Term.literal;
import static org.spartan.fajita.revision.examples.Datalog.Term.name;
import static org.spartan.fajita.revision.examples.Datalog.Term.terms;

import java.io.IOException;

import org.spartan.fajita.revision.api.Fajita;
import org.spartan.fajita.revision.api.Fajita.FajitaBNF;
import org.spartan.fajita.revision.api.Main;
import org.spartan.fajita.revision.export.Grammar;
import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.Terminal;
import org.spartan.fajita.revision.symbols.types.VarArgs;

// TODO Roth add OR
public class Datalog extends Grammar {
  public static enum Term implements Terminal {
    head, body, fact, literal, name, terms
  }

  public static enum NT implements NonTerminal {
    Program, Rule, Literal, Body
  }

  @Override public FajitaBNF bnf() {
    return Fajita.build(getClass(), Term.class, NT.class, "Datalog", Main.packagePath, Main.projectPath) //
        .start(Program) //
        .derive(Program).to(oneOrMore(Rule)) //
        .derive(Rule) //
        /**/.to(attribute(fact, Literal)) //
        /**/.or(attribute(head, Literal)).and(Body) //
        .derive(Body).to(attribute(body, oneOrMore(attribute(literal, Literal))))
        //
        .derive(Literal).to(attribute(name, String.class), attribute(terms, new VarArgs(String.class)));
  }
  public static void main(String[] args) throws IOException {
    new Datalog().generateGrammarFiles();
  }
}
