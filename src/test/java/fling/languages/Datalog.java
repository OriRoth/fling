package fling.languages;

import static fling.grammar.BNF.bnf;
import static fling.grammar.sententials.Notation.noneOrMore;
import static fling.grammar.sententials.Notation.oneOrMore;
import static fling.languages.Datalog.V.AdditionalClause;
import static fling.languages.Datalog.V.Bodyless;
import static fling.languages.Datalog.V.Fact;
import static fling.languages.Datalog.V.FirstClause;
import static fling.languages.Datalog.V.Program;
import static fling.languages.Datalog.V.Query;
import static fling.languages.Datalog.V.Rule;
import static fling.languages.Datalog.V.RuleBody;
import static fling.languages.Datalog.V.RuleHead;
import static fling.languages.Datalog.V.Statement;
import static fling.languages.Datalog.V.Term;
import static fling.languages.Datalog.V.WithBody;
import static fling.languages.Datalog.Σ.always;
import static fling.languages.Datalog.Σ.and;
import static fling.languages.Datalog.Σ.fact;
import static fling.languages.Datalog.Σ.infer;
import static fling.languages.Datalog.Σ.l;
import static fling.languages.Datalog.Σ.of;
import static fling.languages.Datalog.Σ.query;
import static fling.languages.Datalog.Σ.v;
import static fling.languages.Datalog.Σ.when;

import fling.adapters.JavaMediator;
import fling.grammar.BNF;
import fling.grammar.sententials.Terminal;
import fling.grammar.sententials.Variable;

public class Datalog {
  public enum Σ implements Terminal {
    infer, fact, query, of, and, when, always, v, l
  }

  public enum V implements Variable {
    Program, Statement, Rule, Query, Fact, Bodyless, WithBody, //
    RuleHead, RuleBody, FirstClause, AdditionalClause, Term
  }

  private static final Class<String> String = String.class;
  public static final BNF bnf = bnf(V.class). //
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
      "fling.generated", "Datalog", Σ.class);
}
