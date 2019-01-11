package roth.ori.fling.examples;

import static roth.ori.fling.api.Fling.attribute;
import static roth.ori.fling.api.Fling.oneOrMore;
import static roth.ori.fling.api.Fling.noneOrMore;
import static roth.ori.fling.examples.Datalog.NT.Fact;
import static roth.ori.fling.examples.Datalog.NT.Program;
import static roth.ori.fling.examples.Datalog.NT.Query;
import static roth.ori.fling.examples.Datalog.NT.Rule;
import static roth.ori.fling.examples.Datalog.NT.RuleTail;
import static roth.ori.fling.examples.Datalog.NT.Statement;
import static roth.ori.fling.examples.Datalog.Term.and;
import static roth.ori.fling.examples.Datalog.Term.fact;
import static roth.ori.fling.examples.Datalog.Term.infer;
import static roth.ori.fling.examples.Datalog.Term.of;
import static roth.ori.fling.examples.Datalog.Term.query;
import static roth.ori.fling.examples.Datalog.Term.when;
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
    infer, fact, query, of, and, when
  }

  public enum NT implements NonTerminal {
    Program, Statement, Rule, Query, Fact, RuleTail
  }

  @Override public FlingBNF bnf() {
    return buildFlingBNF(Term.class, NT.class, "Datalog", PACKAGE_PATH, PROJECT_PATH) //
        .start(Program) //
        .derive(Program).to(oneOrMore(Statement)) //
        .specialize(Statement).into(Fact, Query, Rule) //
        .derive(Fact).to(attribute(fact, String.class), attribute(of, new VarArgs(String.class))) //
        .derive(Query).to(attribute(query, String.class), attribute(of, new VarArgs(String.class))) //
        .derive(Rule).to( //
            attribute(infer, String.class), //
            attribute(of, new VarArgs(String.class)), //
            attribute(when, String.class), //
            attribute(of, new VarArgs(String.class)), //
            noneOrMore(RuleTail)) //
        .derive(RuleTail).to( //
            attribute(and, String.class), //
            attribute(of, new VarArgs(String.class)));
  }
  @Override public Test examples() {
    return example(call(fact).with("true")) //
        .example( //
            call(query).with("parent").then(of).with("X", "Bob"))
        .malexample( //
            call(fact).with(/* nothing */)) //
        .$();
  }
  public static void main(String[] args) throws IOException {
    new Datalog().generateGrammarFiles();
  }
}
