package fling.examples.languages;

import static fling.examples.languages.Datalog.V.AdditionalClause;
import static fling.examples.languages.Datalog.V.Bodyless;
import static fling.examples.languages.Datalog.V.Fact;
import static fling.examples.languages.Datalog.V.FirstClause;
import static fling.examples.languages.Datalog.V.Program;
import static fling.examples.languages.Datalog.V.Query;
import static fling.examples.languages.Datalog.V.Rule;
import static fling.examples.languages.Datalog.V.RuleBody;
import static fling.examples.languages.Datalog.V.RuleHead;
import static fling.examples.languages.Datalog.V.Statement;
import static fling.examples.languages.Datalog.V.Term;
import static fling.examples.languages.Datalog.V.WithBody;
import static fling.examples.languages.Datalog.Σ.always;
import static fling.examples.languages.Datalog.Σ.and;
import static fling.examples.languages.Datalog.Σ.fact;
import static fling.examples.languages.Datalog.Σ.infer;
import static fling.examples.languages.Datalog.Σ.l;
import static fling.examples.languages.Datalog.Σ.of;
import static fling.examples.languages.Datalog.Σ.query;
import static fling.examples.languages.Datalog.Σ.v;
import static fling.examples.languages.Datalog.Σ.when;
import static fling.grammars.BNF.bnf;
import static fling.internal.grammar.sententials.Notation.noneOrMore;
import static fling.internal.grammar.sententials.Notation.oneOrMore;

import fling.adapters.JavaMediator;
import fling.grammars.BNF;
import fling.internal.grammar.sententials.Terminal;
import fling.internal.grammar.sententials.Variable;

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
      "fling.examples.generated", "Datalog", Σ.class);
}
