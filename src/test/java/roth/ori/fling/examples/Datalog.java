package roth.ori.fling.examples;

import static roth.ori.fling.api.Fling.attribute;
import static roth.ori.fling.api.Fling.oneOrMore;
import static roth.ori.fling.examples.Datalog.NT.Fact;
import static roth.ori.fling.examples.Datalog.NT.Program;
import static roth.ori.fling.examples.Datalog.NT.Query;
import static roth.ori.fling.examples.Datalog.NT.Rule;
import static roth.ori.fling.examples.Datalog.NT.RuleExpression;
import static roth.ori.fling.examples.Datalog.NT.Statement;
import static roth.ori.fling.examples.Datalog.Term.by;
import static roth.ori.fling.examples.Datalog.Term.fact;
import static roth.ori.fling.examples.Datalog.Term.is;
import static roth.ori.fling.examples.Datalog.Term.query;
import static roth.ori.fling.examples.Datalog.Term.rule;
import static roth.ori.fling.export.testing.ExampleBody.call;
import static roth.ori.fling.export.testing.FlingTesting.example;

import java.io.IOException;

import roth.ori.fling.api.Fling.FlingBNF;
import roth.ori.fling.api.Main;
import roth.ori.fling.export.Grammar;
import roth.ori.fling.export.testing.FlingTestingAST.Test;
import roth.ori.fling.symbols.NonTerminal;
import roth.ori.fling.symbols.Terminal;
import roth.ori.fling.symbols.types.VarArgs;

public class Datalog extends Grammar {
  private static final String PACKAGE_PATH = Main.packagePath;
  private static final String PROJECT_PATH = Main.projectPath;
  
  public enum Term implements Terminal {
    rule, is, fact, by, query
  }

  public enum NT implements NonTerminal {
    Program, Statement, Rule, Query, Fact, RuleExpression
  }

  @Override public FlingBNF bnf() {
    return buildFlingBNF(Term.class, NT.class, "Datalog", PACKAGE_PATH, PROJECT_PATH) //
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
  @Override public Test examples() {
    return example( //
        call(fact).with("true")) //
            .example( //
                call(query).with("parent").then(by).with("X", "Bob"))
            .malexample( //
                call(fact).with(/* nothing */)) //
            .$();
  }
  public static void main(String[] args) throws IOException {
    new Datalog().generateGrammarFiles();
  }
}
