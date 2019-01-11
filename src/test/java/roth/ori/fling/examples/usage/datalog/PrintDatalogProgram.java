package roth.ori.fling.examples.usage.datalog;

import java.util.Arrays;
import java.util.stream.Collectors;

import roth.ori.fling.junk.DatalogAST.Fact;
import roth.ori.fling.junk.DatalogAST.Program;
import roth.ori.fling.junk.DatalogAST.Query;
import roth.ori.fling.junk.DatalogAST.Rule;
import roth.ori.fling.junk.DatalogAST.FollowingAtom;
import roth.ori.fling.junk.DatalogAST.Statement;

public class PrintDatalogProgram {
  public static void main(String[] args) {
    System.out.println(print(Ancestor.program()));
  }
  public static String print(Program program) {
    return Arrays.stream(program.program1).map(PrintDatalogProgram::print).collect(Collectors.joining("\n"));
  }
  public static String print(Statement r) {
    if (r instanceof Fact)
      return print((Fact) r);
    if (r instanceof Rule)
      return print((Rule) r);
    return print((Query) r);
  }
  public static String print(Fact r) {
    return printAtom(r.fact, r.of, ".");
  }
  public static String print(Query q) {
    return printAtom(q.query, q.of, "?");
  }
  public static String print(Rule r) {
    return String.format("%s(%s) :- %s(%s)%s.", r.infer, String.join(",", r.of1), r.when, String.join(",", r.of2),
        r.rule1.length == 0 ? ""
            : " & " + Arrays.stream(r.rule1).map(PrintDatalogProgram::print).collect(Collectors.joining(" & ")));
  }
  public static String print(FollowingAtom a) {
    return printAtom(a.and, a.of, "");
  }
  private static String printAtom(String header, String[] literals, String suffix) {
    return String.format("%s(%s)%s", header, String.join(",", literals), suffix);
  }
}
