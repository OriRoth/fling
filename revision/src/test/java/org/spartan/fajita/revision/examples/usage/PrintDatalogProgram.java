package org.spartan.fajita.revision.examples.usage;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.spartan.fajita.revision.junk.DatalogAST.Clause;
import org.spartan.fajita.revision.junk.DatalogAST.Literal;
import org.spartan.fajita.revision.junk.DatalogAST.Program;
import org.spartan.fajita.revision.junk.DatalogAST.Rule;

public class PrintDatalogProgram {
  public static void main(String[] args) {
    System.out.println(print(Ancestor.program()));
  }
  private static String print(Program program) {
    StringBuilder $ = new StringBuilder();
    for (Rule r : program.program1)
      $.append(print(r)).append("\n");
    return $.toString();
  }
  private static String print(Rule r) {
    if (r.rule1.is(Literal.class))
      return print(r.rule1.get(Literal.class)) + ".";
    return print(r.rule1.get(Clause.class));
  }
  private static String print(Literal fact) {
    return fact.name + "(" + String.join(", ", fact.terms) + ")";
  }
  private static String print(Clause c) {
    StringBuilder $ = new StringBuilder();
    $.append(print(c.head)).append(" :- ");
    $.append(String.join(", ", Arrays.stream(c.body).map(x -> print(x)).collect(Collectors.toList())));
    return $.append(".").toString();
  }
}
