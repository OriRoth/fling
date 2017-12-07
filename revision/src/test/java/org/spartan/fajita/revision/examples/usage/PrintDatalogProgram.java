package org.spartan.fajita.revision.examples.usage;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.spartan.fajita.revision.junk.DatalogAST.Literal;
import org.spartan.fajita.revision.junk.DatalogAST.Program;
import org.spartan.fajita.revision.junk.DatalogAST.Rule;
import org.spartan.fajita.revision.junk.DatalogAST.Rule1;
import org.spartan.fajita.revision.junk.DatalogAST.Rule2;

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
    if (r instanceof Rule1)
      return print((Rule1) r);
    return print((Rule2) r);
  }
  private static String print(Rule2 r) {
    return print(r.fact) + ".";
  }
  private static String print(Literal fact) {
    return fact.name + "(" + String.join(", ", fact.terms) + ")";
  }
  private static String print(Rule1 r) {
    StringBuilder $ = new StringBuilder();
    $.append(print(r.head)).append(" :- ");
    $.append(String.join(", ", Arrays.stream(r.body.body).map(x -> print(x)).collect(Collectors.toList())));
    return $.append(".").toString();
  }
}
