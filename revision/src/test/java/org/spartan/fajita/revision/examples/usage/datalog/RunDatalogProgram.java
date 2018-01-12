package org.spartan.fajita.revision.examples.usage.datalog;

import static org.spartan.fajita.revision.examples.usage.datalog.PrintDatalogProgram.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.spartan.fajita.revision.export.ASTVisitor;
import org.spartan.fajita.revision.junk.DatalogAST;
import org.spartan.fajita.revision.junk.DatalogAST.*;

import za.co.wstoop.jatalog.DatalogException;
import za.co.wstoop.jatalog.Expr;
import za.co.wstoop.jatalog.Jatalog;

public class RunDatalogProgram {
  public static final String INPUT_PREFIX = "Jatalog:Fajita$ ";
  public static final String OUTPUT_PREFIX = "                ---";

  public static void main(String[] args) {
    run(Ancestor.program());
  }
  @SuppressWarnings("unused") public static void run(Program p) {
    Jatalog j = new Jatalog();
    ASTVisitor visitor = new ASTVisitor(DatalogAST.class) {
      public boolean visit(Fact f) throws DatalogException {
        j.fact(f.fact, f.by);
        System.out.println(INPUT_PREFIX + print(f));
        return true;
      }
      public boolean visit(Rule r) throws DatalogException {
        List<Expr> $ = new LinkedList<>();
        $.addAll(Arrays.stream(r.rule1).map(e -> Expr.expr(e.is, e.by)).collect(Collectors.toList()));
        j.rule(Expr.expr(r.rule, r.by), $.toArray(new Expr[$.size()]));
        System.out.println(INPUT_PREFIX + print(r));
        return true;
      }
      public boolean visit(Query q) throws DatalogException {
        List<Map<String, String>> $ = new LinkedList<>();
        for (Map<String, String> x : j.query(Expr.expr(q.query, q.by)))
          $.add(new HashMap<>(x));
        System.out.println(INPUT_PREFIX + print(q) + "\n" + OUTPUT_PREFIX + $);
        return true;
      }
    };
    visitor.startVisit(p);
  }
}
