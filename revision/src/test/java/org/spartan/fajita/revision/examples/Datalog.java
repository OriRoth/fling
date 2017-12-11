package org.spartan.fajita.revision.examples;

import static org.spartan.fajita.revision.api.Fajita.attribute;
import static org.spartan.fajita.revision.api.Fajita.either;
import static org.spartan.fajita.revision.api.Fajita.oneOrMore;
import static org.spartan.fajita.revision.examples.Datalog.NT.Clause;
import static org.spartan.fajita.revision.examples.Datalog.NT.Literal;
import static org.spartan.fajita.revision.examples.Datalog.NT.Program;
import static org.spartan.fajita.revision.examples.Datalog.NT.Rule;
import static org.spartan.fajita.revision.examples.Datalog.Term.body;
import static org.spartan.fajita.revision.examples.Datalog.Term.fact;
import static org.spartan.fajita.revision.examples.Datalog.Term.head;
import static org.spartan.fajita.revision.examples.Datalog.Term.literal;
import static org.spartan.fajita.revision.examples.Datalog.Term.name;
import static org.spartan.fajita.revision.examples.Datalog.Term.terms;
import static org.spartan.fajita.revision.export.testing.ExampleBody.call;
import static org.spartan.fajita.revision.export.testing.ExampleBody.toConclude;
import static org.spartan.fajita.revision.export.testing.FajitaTesting.example;

import java.io.IOException;

import org.spartan.fajita.revision.api.Fajita;
import org.spartan.fajita.revision.api.Fajita.FajitaBNF;
import org.spartan.fajita.revision.api.Main;
import org.spartan.fajita.revision.export.Grammar;
import org.spartan.fajita.revision.export.testing.FajitaTestingAST.Test;
import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.Terminal;
import org.spartan.fajita.revision.symbols.types.VarArgs;

public class Datalog extends Grammar {
  public static enum Term implements Terminal {
    head, body, fact, literal, name, terms
  }

  public static enum NT implements NonTerminal {
    Program, Rule, Literal, Clause
  }

  @Override public FajitaBNF bnf() {
    return Fajita.build(getClass(), Term.class, NT.class, "Datalog", Main.packagePath, Main.projectPath) //
        .start(Program) //
        .derive(Program).to(oneOrMore(Rule)) //
        .derive(Rule).to(either(attribute(fact, Literal), Clause)) //
        .derive(Clause).to(attribute(head, Literal), attribute(body, oneOrMore(attribute(literal, Literal)))) //
        .derive(Literal).to(attribute(name, String.class), attribute(terms, new VarArgs(String.class)));
  }
  @Override public Test examples() {
    return example( //
        call(fact).with(Literal)) //
            .example( //
                toConclude(Literal).call(name).with("parent").then(terms).with("John", "Bob"))
            .malexample( //
                call(fact).with("Fluent APIw have a bright future")) //
            .$();
  }
  public static void main(String[] args) throws IOException {
    new Datalog().generateGrammarFiles();
  }
}
