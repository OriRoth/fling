package roth.ori.fling.examples.usage.datalog;

import java.util.Arrays;
import java.util.stream.Collectors;

import roth.ori.fling.junk.DatalogAST.AdditionalClause;
import roth.ori.fling.junk.DatalogAST.Bodyless;
import roth.ori.fling.junk.DatalogAST.Fact;
import roth.ori.fling.junk.DatalogAST.Program;
import roth.ori.fling.junk.DatalogAST.Query;
import roth.ori.fling.junk.DatalogAST.Rule;
import roth.ori.fling.junk.DatalogAST.Statement;
import roth.ori.fling.junk.DatalogAST.Term;
import roth.ori.fling.junk.DatalogAST.Term1;
import roth.ori.fling.junk.DatalogAST.Term2;
import roth.ori.fling.junk.DatalogAST.WithBody;

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
    return r instanceof WithBody ? print((WithBody) r) : print((Bodyless) r);
  }
  public static String print(Bodyless r) {
    return String.format("%s(%s).", r.always, print(r.of));
  }
  public static String print(Term t) {
    return t instanceof Term1 ? print((Term1) t) : print((Term2) t);
  }
  public static String print(Term1 t) {
    return t.l;
  }
  public static String print(Term2 t) {
    return t.v;
  }
  public static String print(WithBody r) {
    return String.format("%s(%s) :- %s(%s)%s.", r.rulehead.infer, print(r.rulehead.of), r.rulebody.firstclause.when,
        print(r.rulebody.firstclause.of),
        " & " + Arrays.stream(r.rulebody.rulebody1).map(PrintDatalogProgram::print).collect(Collectors.joining(" & ")));
  }
  public static String print(AdditionalClause ac) {
    return printAtom(ac.and, ac.of, "");
  }
  private static String print(Term[] ts) {
    return Arrays.stream(ts).map(PrintDatalogProgram::print).collect(Collectors.joining(","));
  }
  private static String printAtom(String header, Term[] terms, String suffix) {
    return String.format("%s(%s)%s", header, Arrays.stream(terms).map(PrintDatalogProgram::print).collect(Collectors.joining(",")),
        suffix);
  }
  private static String printAtom(String header, String[] entityNames, String suffix) {
    return String.format("%s(%s)%s", header, String.join(",", entityNames), suffix);
  }
}
