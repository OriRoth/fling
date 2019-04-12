package fling.examples.programs;

import static fling.examples.generated.Datalog.fact;
import static fling.examples.generated.Datalog.Term.*;
import static fling.examples.programs.Datalog.DatalogPrinter.print;
import static java.lang.String.format;
import static java.util.stream.Collectors.joining;

import java.util.*;
import java.util.stream.Collectors;

import fling.examples.generated.DatalogAST;
import fling.examples.generated.DatalogAST.*;
import za.co.wstoop.jatalog.*;

public class Datalog {
  public static void main(final String[] args) {
    final Program program = fact("parent").of("john", "bob"). //
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
    @Override public void whileVisiting(final Fact fact) {
      print(fact);
    }
    @Override public void whileVisiting(final Query query) {
      print(query);
    }
    @Override public void whileVisiting(final Bodyless bodyless) {
      print(bodyless);
    }
    @Override public void whileVisiting(final RuleHead ruleHead) {
      System.out.print(format("%s(%s) :- ", ruleHead.infer, printTerms(ruleHead.of)));
    }
    @Override public void whileVisiting(final RuleBody ruleBody) {
      System.out.print(format("%s(%s)", ruleBody.firstClause.when, printTerms(ruleBody.firstClause.of)));
      ruleBody.additionalClause.stream() //
          .map(additionalClause -> format(", %s(%s)", //
              additionalClause.and, //
              printTerms(additionalClause.of))) //
          .forEach(System.out::print);
      System.out.println(".");
    }
    private static String printTerms(final Term[] terms) {
      return Arrays.stream(terms).map(DatalogPrinter::printTerm).collect(joining(","));
    }
    private static String printTerm(final Term term) {
      return term instanceof Term1 ? ((Term1) term).l : ((Term2) term).v;
    }
    public static void print(final Fact fact) {
      System.out.println(format("%s(%s).", fact.fact, Arrays.stream(fact.of).collect(joining(","))));
    }
    public static void print(final Query query) {
      System.out.println(format("%s(%s)?", query.query, printTerms(query.of)));
    }
    public static void print(final Bodyless bodyless) {
      System.out.println(format("%s(%s).", bodyless.always, printTerms(bodyless.of)));
    }
    public static void print(final WithBody withBody) {
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

    @Override public void whileVisiting(final Fact fact) throws DatalogException {
      j.fact(fact.fact, fact.of);
      print(fact);
    }
    @SuppressWarnings("unused") @Override public void whileVisiting(final Bodyless bodyless) throws DatalogException {
      // j.fact(Expr.expr(bodyless.always, toStrings(bodyless.of)));
      // print(bodyless);
    }
    @Override public void whileVisiting(final WithBody withBody) throws Exception {
      j.rule(Expr.expr(withBody.ruleHead.infer, toStrings(withBody.ruleHead.of)), //
          getExprRightHandSide(withBody));
      print(withBody);
    }
    @Override public void whileVisiting(final Query query) throws DatalogException {
      print(query);
      printResult(j.query(Expr.expr(query.query, toStrings(query.of))));
    }
    private static String[] toStrings(final Term[] terms) {
      return Arrays.stream(terms) //
          .map(term -> term instanceof Term1 ? //
              ((Term1) term).l : //
              ((Term2) term).v) //
          .toArray(String[]::new);
    }
    private static Expr[] getExprRightHandSide(final WithBody withBody) {
      final List<Expr> $ = new ArrayList<>();
      $.add(Expr.expr(withBody.ruleBody.firstClause.when, toStrings(withBody.ruleBody.firstClause.of)));
      for (final AdditionalClause a : withBody.ruleBody.additionalClause)
        $.add(Expr.expr(a.and, toStrings(a.of)));
      return $.toArray(new Expr[$.size()]);
    }
    private static void printResult(final Collection<Map<String, String>> result) {
      System.out.println("[" + result.stream() //
          .map(m -> m.entrySet().stream() //
              .map(e -> e.getKey() + "=" + e.getValue()) //
              .collect(Collectors.joining(", "))) //
          .map(s -> "{" + s + "}") //
          .collect(Collectors.joining(", ")) + "]");
    }
  }
}
