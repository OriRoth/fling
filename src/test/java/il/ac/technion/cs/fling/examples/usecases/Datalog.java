package il.ac.technion.cs.fling.examples.usecases;

import static il.ac.technion.cs.fling.examples.generated.Datalog.fact;
import static il.ac.technion.cs.fling.examples.generated.Datalog.Term.l;
import static il.ac.technion.cs.fling.examples.generated.Datalog.Term.v;
import static il.ac.technion.cs.fling.examples.usecases.Datalog.DatalogPrinter.print;
import static java.lang.String.format;
import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import il.ac.technion.cs.fling.examples.LoopOverLanguageDefinitions;
import il.ac.technion.cs.fling.examples.generated.DatalogAST;
import il.ac.technion.cs.fling.examples.generated.DatalogAST.AdditionalClause;
import il.ac.technion.cs.fling.examples.generated.DatalogAST.Bodyless;
import il.ac.technion.cs.fling.examples.generated.DatalogAST.Fact;
import il.ac.technion.cs.fling.examples.generated.DatalogAST.Program;
import il.ac.technion.cs.fling.examples.generated.DatalogAST.Query;
import il.ac.technion.cs.fling.examples.generated.DatalogAST.RuleBody;
import il.ac.technion.cs.fling.examples.generated.DatalogAST.RuleHead;
import il.ac.technion.cs.fling.examples.generated.DatalogAST.Term;
import il.ac.technion.cs.fling.examples.generated.DatalogAST.Term1;
import il.ac.technion.cs.fling.examples.generated.DatalogAST.Term2;
import il.ac.technion.cs.fling.examples.generated.DatalogAST.WithBody;
import za.co.wstoop.jatalog.DatalogException;
import za.co.wstoop.jatalog.Expr;
import za.co.wstoop.jatalog.Jatalog;

/** This class demonstrates the use of automatically generated fluent API.
 * Needless to say, it cannot be compiled before this fluent API was generated.
 * To generate the respective fluent APIs, run
 * {@link LoopOverLanguageDefinitions}.
 * 
 * @author Ori Roth
 * @since April 2019 */
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

  /** Prints Datalog program.
   * 
   * @author Ori Roth */
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
      System.out.println(format("%s(%s).", fact.fact, String.join(",", fact.of)));
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
              .collect(joining(", "))));
    }
  }

  /** Runs Datalog program using {@link Jatalog}.
   * 
   * @author Ori Roth */
  public static class DatalogRunner extends DatalogAST.Visitor {
    final Jatalog j = new Jatalog();

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
              .collect(joining(", "))) //
          .map(s -> "{" + s + "}") //
          .collect(joining(", ")) + "]");
    }
  }
}
