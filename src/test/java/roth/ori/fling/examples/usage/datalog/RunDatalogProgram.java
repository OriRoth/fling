package roth.ori.fling.examples.usage.datalog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import roth.ori.fling.export.ASTVisitor;
import roth.ori.fling.junk.DatalogAST;
import roth.ori.fling.junk.DatalogAST.Fact;
import roth.ori.fling.junk.DatalogAST.Program;
import roth.ori.fling.junk.DatalogAST.Query;
import roth.ori.fling.junk.DatalogAST.Rule;
import za.co.wstoop.jatalog.DatalogException;
import za.co.wstoop.jatalog.Expr;
import za.co.wstoop.jatalog.Jatalog;

public class RunDatalogProgram {
  public static final String INPUT_PREFIX = "Jatalog:Fling$ ";
  public static final String OUTPUT_PREFIX = "                ---";

  static void print(Fact f) {
    System.out.println(INPUT_PREFIX + PrintDatalogProgram.print(f));
  }
  static void print(Rule r) {
    System.out.println(INPUT_PREFIX + PrintDatalogProgram.print(r));
  }
  static void print(Query q) {
    System.out.println(INPUT_PREFIX + PrintDatalogProgram.print(q));
  }
  static void print(Collection<Map<String, String>> jatalogResult) {
    List<Map<String, String>> $ = new ArrayList<>();
    for (Map<String, String> map : jatalogResult)
      $.add(new LinkedHashMap<>(map));
    System.out.println(OUTPUT_PREFIX + $);
  }
  public static void main(String[] args) {
    run(Ancestor.program());
  }
  @SuppressWarnings("unused") static ASTVisitor datalogRunner() {
    Jatalog j = new Jatalog();
    return new ASTVisitor(DatalogAST.class) {
      public boolean visit(Fact f) throws DatalogException {
        j.fact(f.fact, f.of);
        print(f);
        return true;
      }
      public boolean visit(Rule r) throws DatalogException {
        j.rule(Expr.expr(r.infer, r.of1), getExprRightHandSide(r));
        print(r);
        return true;
      }
      public boolean visit(Query q) throws DatalogException {
        print(q);
        print(j.query(Expr.expr(q.query, q.of)));
        return true;
      }
    };
  }
  public static void run(Program p) {
    datalogRunner().startVisit(p);
  }
  static Expr[] getExprRightHandSide(Rule r) {
    List<Expr> when = Arrays.stream(r.rule1).map(e -> Expr.expr(e.and, e.of)).collect(Collectors.toList());
    when.add(Expr.expr(r.when, r.of2));
    Expr[] whenx = when.toArray(new Expr[when.size()]);
    return whenx;
  }
}
