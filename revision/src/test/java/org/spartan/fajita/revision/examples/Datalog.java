package org.spartan.fajita.revision.examples;

import static org.spartan.fajita.revision.api.Fajita.attribute;
import static org.spartan.fajita.revision.api.Fajita.oneOrMore;
import static org.spartan.fajita.revision.examples.Datalog.NT.Fact;
import static org.spartan.fajita.revision.examples.Datalog.NT.Program;
import static org.spartan.fajita.revision.examples.Datalog.NT.Query;
import static org.spartan.fajita.revision.examples.Datalog.NT.Rule;
import static org.spartan.fajita.revision.examples.Datalog.NT.RuleExpression;
import static org.spartan.fajita.revision.examples.Datalog.NT.Statement;
import static org.spartan.fajita.revision.examples.Datalog.Term.by;
import static org.spartan.fajita.revision.examples.Datalog.Term.fact;
import static org.spartan.fajita.revision.examples.Datalog.Term.is;
import static org.spartan.fajita.revision.examples.Datalog.Term.query;
import static org.spartan.fajita.revision.examples.Datalog.Term.rule;

import java.io.IOException;

import org.spartan.fajita.revision.api.Fajita;
import org.spartan.fajita.revision.api.Fajita.FajitaBNF;
import org.spartan.fajita.revision.api.Main;
import org.spartan.fajita.revision.export.Grammar;
import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.Terminal;
import org.spartan.fajita.revision.symbols.types.VarArgs;

public class Datalog extends Grammar {
  public static enum Term implements Terminal {
    rule, is, fact, by, query
  }

  public static enum NT implements NonTerminal {
    Program, Statement, Rule, Query, Fact, RuleExpression
  }

  @Override public FajitaBNF bnf() {
    return Fajita.build(getClass(), Term.class, NT.class, "Datalog", Main.packagePath, Main.projectPath) //
        .start(Program) //
        .derive(Program).to(oneOrMore(Statement)) //
        .specialize(Statement).into(Rule, Query, Fact) //
        .derive(Fact).to(attribute(fact, String.class), attribute(by, new VarArgs(String.class))) //
        .derive(Rule).to( //
            attribute(rule, String.class), //
            attribute(by, new VarArgs(String.class)), //
            oneOrMore(RuleExpression)) //
        .derive(RuleExpression).to(attribute(is, String.class), attribute(by, new VarArgs(String.class))) //
        .derive(Query).to(attribute(query, String.class), attribute(by, new VarArgs(String.class))) //
    ;
  }
  public static void main(String[] args) throws IOException {
    new Datalog().generateGrammarFiles();
  }
}
