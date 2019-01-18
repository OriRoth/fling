package roth.ori.fling.examples;

import static roth.ori.fling.api.Fling.noneOrMore;
import static roth.ori.fling.api.Fling.oneOrMore;
import static roth.ori.fling.examples.Datalog.Symbols.AdditionalClause;
import static roth.ori.fling.examples.Datalog.Symbols.Bodyless;
import static roth.ori.fling.examples.Datalog.Symbols.Fact;
import static roth.ori.fling.examples.Datalog.Symbols.FirstClause;
import static roth.ori.fling.examples.Datalog.Symbols.Program;
import static roth.ori.fling.examples.Datalog.Symbols.Query;
import static roth.ori.fling.examples.Datalog.Symbols.Rule;
import static roth.ori.fling.examples.Datalog.Symbols.RuleBody;
import static roth.ori.fling.examples.Datalog.Symbols.RuleHead;
import static roth.ori.fling.examples.Datalog.Symbols.Statement;
import static roth.ori.fling.examples.Datalog.Symbols.Term;
import static roth.ori.fling.examples.Datalog.Symbols.WithBody;
import static roth.ori.fling.examples.Datalog.Terminals.always;
import static roth.ori.fling.examples.Datalog.Terminals.and;
import static roth.ori.fling.examples.Datalog.Terminals.fact;
import static roth.ori.fling.examples.Datalog.Terminals.infer;
import static roth.ori.fling.examples.Datalog.Terminals.l;
import static roth.ori.fling.examples.Datalog.Terminals.of;
import static roth.ori.fling.examples.Datalog.Terminals.query;
import static roth.ori.fling.examples.Datalog.Terminals.v;
import static roth.ori.fling.examples.Datalog.Terminals.when;

import java.io.IOException;

import roth.ori.fling.api.Fling.FlingBNF;
import roth.ori.fling.api.Main;
import roth.ori.fling.export.Grammar;
import roth.ori.fling.symbols.Symbol;
import roth.ori.fling.symbols.Terminal;

public class Datalog extends Grammar {
  private static final String PACKAGE_PATH = Main.packagePath;
  private static final String PROJECT_PATH = Main.projectPath;

  public enum Terminals implements Terminal {
    infer, fact, query, of, and, when, always, v, l
  }

  public enum Symbols implements Symbol {
    Program, Statement, Rule, Query, Fact, Bodyless, WithBody, RuleHead, RuleBody, FirstClause, AdditionalClause, Term
  }

  @Override public FlingBNF bnf() {
    Class<String> String = String.class;
    return buildFlingBNF(Terminals.class, Symbols.class, "Datalog", PACKAGE_PATH, PROJECT_PATH). //
        start(Program). //
        derive(Program).to(oneOrMore(Statement)). //
        specialize(Statement).into(Fact, Query, Rule). //
        derive(Fact).to(fact.with(String), of.many(String)). //
        derive(Query).to(query.with(String), of.many(Term)). //
        specialize(Rule).into(Bodyless, WithBody). //
        derive(Bodyless).to(always.with(String), of.many(Term)). //
        derive(WithBody).to(RuleHead, RuleBody). //
        derive(RuleHead).to(infer.with(String), of.many(Term)). //
        derive(RuleBody).to(FirstClause, noneOrMore(AdditionalClause)). //
        derive(FirstClause).to(when.with(String), of.many(Term)). //
        derive(AdditionalClause).to(and.with(String), of.many(Term)). //
        derive(Term).to(l.with(String)).or(v.with(String));
  }
  // TODO Roth: recreate examples
  public static void main(String[] args) throws IOException {
    new Datalog().generateGrammarFiles();
  }
}
