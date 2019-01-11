package roth.ori.fling.examples;

import static roth.ori.fling.api.Fling.attribute;
import static roth.ori.fling.api.Fling.noneOrMore;
import static roth.ori.fling.api.Fling.oneOrMore;
import static roth.ori.fling.examples.Datalog.DatalogNonTerminals.Fact;
import static roth.ori.fling.examples.Datalog.DatalogNonTerminals.FollowingAtom;
import static roth.ori.fling.examples.Datalog.DatalogNonTerminals.Program;
import static roth.ori.fling.examples.Datalog.DatalogNonTerminals.Query;
import static roth.ori.fling.examples.Datalog.DatalogNonTerminals.Rule;
import static roth.ori.fling.examples.Datalog.DatalogNonTerminals.Statement;
import static roth.ori.fling.examples.Datalog.DatalogTerminals.and;
import static roth.ori.fling.examples.Datalog.DatalogTerminals.fact;
import static roth.ori.fling.examples.Datalog.DatalogTerminals.infer;
import static roth.ori.fling.examples.Datalog.DatalogTerminals.of;
import static roth.ori.fling.examples.Datalog.DatalogTerminals.query;
import static roth.ori.fling.examples.Datalog.DatalogTerminals.when;
import static roth.ori.fling.export.testing.ExampleBody.call;
import static roth.ori.fling.export.testing.FlingTesting.example;
import static roth.ori.fling.symbols.types.VarArgs.varargs;

import java.io.IOException;
import java.util.Arrays;

import roth.ori.fling.api.Fling.FlingBNF;
import roth.ori.fling.api.Main;
import roth.ori.fling.export.Grammar;
import roth.ori.fling.export.testing.FlingTestingAST.Test;
import roth.ori.fling.symbols.NonTerminal;
import roth.ori.fling.symbols.Terminal;

public class Datalog extends Grammar {
  private static final String PACKAGE_PATH = Main.packagePath;
  private static final String PROJECT_PATH = Main.projectPath;

  public enum DatalogTerminals implements Terminal {
    infer, fact, query, of, and, when
  }

  public enum DatalogNonTerminals implements NonTerminal {
    Program, Statement, Rule, Query, Fact, FollowingAtom
  }

  public interface Term {
    String entityName();
  }
  public interface Literal extends Term {}
  public interface Variable extends Term {}

  public static Literal l(String l) {
    return new Literal() {
      @Override public String entityName() {
        return l;
      }
    };
  }
  public static Variable v(String v) {
    return new Variable() {
      @Override public String entityName() {
        return v;
      }
    };
  }
  public static String[] entityNames(Term[] terms) {
    return Arrays.stream(terms).map(Term::entityName).toArray(String[]::new);
  }
  @Override public FlingBNF bnf() {
    return buildFlingBNF(DatalogTerminals.class, DatalogNonTerminals.class, "Datalog", PACKAGE_PATH, PROJECT_PATH) //
        .start(Program) //
        .derive(Program).to(oneOrMore(Statement)) //
        .specialize(Statement).into(Fact, Query, Rule) //
        .derive(Fact).to(attribute(fact, String.class), attribute(of, varargs(String.class))) //
        .derive(Query).to(attribute(query, String.class), attribute(of, varargs(Term.class))) //
        .derive(Rule).to( //
            attribute(infer, String.class), //
            attribute(of, varargs(Term.class)), //
            attribute(when, String.class), //
            attribute(of, varargs(Term.class)), //
            noneOrMore(FollowingAtom)) //
        .derive(FollowingAtom).to( //
            attribute(and, String.class), //
            attribute(of, varargs(Term.class)));
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
