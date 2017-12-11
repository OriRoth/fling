package org.spartan.fajita.revision.examples.usage;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.spartan.fajita.revision.junk.DatalogAST.*;

public class PrintDatalogProgram {
  public static void main(String[] args) {
    System.out.println(print(Ancestor.program()));
  }
  public static String print(Program program) {
    StringBuilder $ = new StringBuilder();
    for (Statement s : program.program1)
      $.append(print(s)).append("\n");
    return $.toString();
  }
  public static String print(Statement r) {
    if (r instanceof Fact)
      return print((Fact) r);
    if (r instanceof Rule)
      return print((Rule) r);
    return print((Query) r);
  }
  public static String print(Fact r) {
    return print(r.fact) + ".";
  }
  public static String print(Rule r) {
    StringBuilder $ = new StringBuilder();
    $.append(print(r.rule)).append(" :- ");
    $.append(r.is).append("(").append(String.join(", ", r.by)).append(")");
    if (r.rule1.length > 0)
      $.append(" ");
    $.append(String.join(", ", Arrays.stream(r.rule1).map(x -> print(x)).collect(Collectors.toList())));
    return $.append(".").toString();
  }
  public static String print(FactExpression e) {
    return e.that + "(" + String.join(", ", e.by) + ")";
  }
  public static String print(RuleExpression e) {
    return e.and + "(" + String.join(", ", e.by) + ")";
  }
  public static String print(Query r) {
    return r.query + "(" + String.join(", ", r.by) + ")?";
  }
}
