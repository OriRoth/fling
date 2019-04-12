package fling.examples.languages;

import static fling.examples.languages.Datalog.V.*;
import static fling.examples.languages.Datalog.Σ.*;
import static fling.grammars.api.BNFAPI.bnf;
import static fling.internal.grammar.sententials.Notation.*;

import fling.adapters.JavaMediator;
import fling.grammars.BNF;
import fling.internal.grammar.sententials.*;

public class Datalog {
  public enum Σ implements Terminal {
    infer, fact, query, of, and, when, always, v, l
  }

  public enum V implements Variable {
    Program, Statement, Rule, Query, Fact, Bodyless, WithBody, //
    RuleHead, RuleBody, FirstClause, AdditionalClause, Term
  }

  private static final Class<String> String = String.class;
  public static final BNF bnf = bnf(). //
      start(Program). //
      derive(Program).to(oneOrMore(Statement)). //
      specialize(Statement).into(Fact, Rule, Query). //
      derive(Fact).to(fact.with(String), of.many(String)). //
      derive(Query).to(query.with(String), of.many(Term)). //
      specialize(Rule).into(Bodyless, WithBody). //
      derive(Bodyless).to(always.with(String), of.many(Term)). //
      derive(WithBody).to(RuleHead, RuleBody). //
      derive(RuleHead).to(infer.with(String), of.many(Term)). //
      derive(RuleBody).to(FirstClause, noneOrMore(AdditionalClause)). //
      derive(FirstClause).to(when.with(String), of.many(Term)). //
      derive(AdditionalClause).to(and.with(String), of.many(Term)). //
      derive(Term).to(l.with(String)). //
      derive(Term).to(v.with(String)). //
      build();
  public static final JavaMediator jm = new JavaMediator(bnf, //
      "fling.examples.generated", "Datalog", Σ.class);
}
