package fling.languages;

import static fling.generated.Datalog.fact;
import static fling.generated.Datalog.Term.l;
import static fling.generated.Datalog.Term.v;
import static fling.grammar.BNF.bnf;
import static fling.grammar.sententials.Notation.noneOrMore;
import static fling.grammar.sententials.Notation.oneOrMore;
import static fling.languages.Datalog.DatalogPrinter.print;
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
import static java.lang.String.format;
import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import fling.adapters.JavaMediator;
import fling.generated.DatalogAST;
import fling.generated.DatalogAST.AdditionalClause;
import fling.generated.DatalogAST.Bodyless;
import fling.generated.DatalogAST.Fact;
import fling.generated.DatalogAST.Program;
import fling.generated.DatalogAST.Query;
import fling.generated.DatalogAST.RuleBody;
import fling.generated.DatalogAST.RuleHead;
import fling.generated.DatalogAST.Term;
import fling.generated.DatalogAST.Term1;
import fling.generated.DatalogAST.Term2;
import fling.generated.DatalogAST.WithBody;
import fling.grammar.BNF;
import fling.grammar.sententials.Terminal;
import fling.grammar.sententials.Variable;
import za.co.wstoop.jatalog.DatalogException;
import za.co.wstoop.jatalog.Expr;
import za.co.wstoop.jatalog.Jatalog;

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

  public static void main(String[] args) {
    Program program = fact("parent").of("john", "bob"). //
        fact("parent").of("bob", "donald"). //
        always("ancestor").of(l("adam"), v("X")). //
        infer("ancestor").of(v("A"), v("B")). //
        when("parent").of(v("A"), v("B")). //
        infer("ancestor").of(v("A"), v("B")). //
        when("parent").of(v("A"), v("C")). //
        and("ancestor").of(v("C"), v("B")). //
        query("ancestor").of(l("john"), v("X")). //
        $();
    new DatalogRunner().visit(program);
  }

  public static class DatalogPrinter extends DatalogAST.Visitor {
    @Override public void whileVisiting(Fact fact) {
      print(fact);
    }
    @Override public void whileVisiting(Query query) {
      print(query);
    }
    @Override public void whileVisiting(Bodyless bodyless) {
      print(bodyless);
    }
    @Override public void whileVisiting(RuleHead ruleHead) {
      System.out.print(format("%s(%s) :- ", ruleHead.infer, printTerms(ruleHead.of)));
    }
    @Override public void whileVisiting(RuleBody ruleBody) {
      System.out.print(format("%s(%s)", ruleBody.firstClause.when, printTerms(ruleBody.firstClause.of)));
      ruleBody.additionalClause.stream() //
          .map(additionalClause -> format(", %s(%s)", //
              additionalClause.and, //
              printTerms(additionalClause.of))) //
          .forEach(System.out::print);
      System.out.println(".");
    }
    private static String printTerms(Term[] terms) {
      return Arrays.stream(terms).map(DatalogPrinter::printTerm).collect(joining(","));
    }
    private static String printTerm(Term term) {
      return term instanceof Term1 ? ((Term1) term).l : ((Term2) term).v;
    }
    public static void print(Fact fact) {
      System.out.println(format("%s(%s).", fact.fact, Arrays.stream(fact.of).collect(joining(","))));
    }
    public static void print(Query query) {
      System.out.println(format("%s(%s)?", query.query, printTerms(query.of)));
    }
    public static void print(Bodyless bodyless) {
      System.out.println(format("%s(%s).", bodyless.always, printTerms(bodyless.of)));
    }
    public static void print(WithBody withBody) {
      System.out.println(format("%s(%s) :- %s(%s)%s%s.", //
          withBody.ruleHead.infer, //
          printTerms(withBody.ruleHead.of), //
          withBody.ruleBody.firstClause.when, //
          printTerms(withBody.ruleBody.firstClause.of), //
          withBody.ruleBody.additionalClause.isEmpty() ? "" : ", ", //
          withBody.ruleBody.additionalClause.stream() //
              .map(a -> format("%s(%s)", a.and, printTerms(a.of))) //
              .collect(Collectors.joining(", "))));
    }
  }

  public static class DatalogRunner extends DatalogAST.Visitor {
    Jatalog j = new Jatalog();

    @Override public void whileVisiting(Fact fact) throws DatalogException {
      j.fact(fact.fact, fact.of);
      print(fact);
    }
    @SuppressWarnings("unused") @Override public void whileVisiting(Bodyless bodyless) throws DatalogException {
      // j.fact(Expr.expr(bodyless.always, toStrings(bodyless.of)));
      // print(bodyless);
    }
    @Override public void whileVisiting(WithBody withBody) throws Exception {
      j.rule(Expr.expr(withBody.ruleHead.infer, toStrings(withBody.ruleHead.of)), //
          getExprRightHandSide(withBody));
      print(withBody);
    }
    @Override public void whileVisiting(Query query) throws DatalogException {
      print(query);
      printResult(j.query(Expr.expr(query.query, toStrings(query.of))));
    }
    private static String[] toStrings(Term[] terms) {
      return Arrays.stream(terms) //
          .map(term -> term instanceof Term1 ? //
              ((Term1) term).l : //
              ((Term2) term).v) //
          .toArray(String[]::new);
    }
    private static Expr[] getExprRightHandSide(WithBody withBody) {
      List<Expr> $ = new ArrayList<>();
      $.add(Expr.expr(withBody.ruleBody.firstClause.when, toStrings(withBody.ruleBody.firstClause.of)));
      for (AdditionalClause a : withBody.ruleBody.additionalClause)
        $.add(Expr.expr(a.and, toStrings(a.of)));
      return $.toArray(new Expr[$.size()]);
    }
    private static void printResult(Collection<Map<String, String>> result) {
      System.out.println("[" + result.stream() //
          .map(m -> m.entrySet().stream() //
              .map(e -> e.getKey() + "=" + e.getValue()) //
              .collect(Collectors.joining(", "))) //
          .map(s -> "{" + s + "}") //
          .collect(Collectors.joining(", ")) + "]");
    }
  }
}
