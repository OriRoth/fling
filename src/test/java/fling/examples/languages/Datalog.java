package fling.examples.languages;

import static fling.examples.languages.Datalog.V.*;
import static fling.examples.languages.Datalog.Σ.*;
import static fling.grammars.api.BNFAPI.bnf;
import static fling.internal.grammar.sententials.Notation.*;

import fling.*;
import fling.BNF;
import fling.adapters.JavaMediator;

/**
 * Datalog language.
 * 
 * @author Yossi Gil
 */
public class Datalog {
  /** Set of terminals, i.e., method names of generated fluent API. */
  public enum Σ implements Terminal {
    infer, fact, query, of, and, when, always, v, l
  }

  /**
   * Set of non-terminals, i.e., abstract concepts of fluent API; these names
   * will be translated into names of classes of abstract syntax tree that Fling
   * generates, i.e., this AST will have class {@link Program} which will have a
   * list of {@link Statement}, etc.
   */
  public enum V implements Variable {
    Program, Statement, Rule, Query, Fact, Bodyless, WithBody, //
    RuleHead, RuleBody, FirstClause, AdditionalClause, Term
  }

  /**
   * Short name of {@link String}.class, used to specify the type of parameters
   * to fluent API methods in grammar specification.
   */
  private static final Class<String> S = String.class;
  /**
   * Datalog's grammar in Backus-Naur form.
   */
  public static final BNF bnf = bnf(). //
      start(Program). // This is the start symbol
      derive(Program).to(oneOrMore(Statement)). // Program ::= Statement*
      specialize(Statement).into(Fact, Rule, Query).
      /* Defines the rule Statement ::= Fact |Rule | Query, but also defines
       * that classes {@link Fact}, {@link Rule} and {@link Query} extend class
       * {@link Statement} */
      derive(Fact).to(fact.with(S), of.many(S)). // Fact ::= fact(S*) of(S*)
      derive(Query).to(query.with(S), of.many(Term)). //
      specialize(Rule).into(Bodyless, WithBody). //
      derive(Bodyless).to(always.with(S), of.many(Term)). //
      derive(WithBody).to(RuleHead, RuleBody). //
      derive(RuleHead).to(infer.with(S), of.many(Term)). //
      derive(RuleBody).to(FirstClause, noneOrMore(AdditionalClause)). //
      derive(FirstClause).to(when.with(S), of.many(Term)). //
      derive(AdditionalClause).to(and.with(S), of.many(Term)). //
      derive(Term).to(l.with(S)).or(v.with(S)). //
      build();
  /**
   * The {@link JavaMediator} responsible of compiling the Java Datalog API/AST
   * types/AST run-time compiler.
   */
  public static final JavaMediator jm = new JavaMediator(bnf, //
      "fling.examples.generated", "Datalog", Σ.class);
}
