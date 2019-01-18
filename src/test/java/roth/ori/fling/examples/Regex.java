package roth.ori.fling.examples;

import static roth.ori.fling.examples.Regex.NT.Expression;
import static roth.ori.fling.examples.Regex.NT.RE;
import static roth.ori.fling.examples.Regex.NT.Tail;
import static roth.ori.fling.examples.Regex.Term.and;
import static roth.ori.fling.examples.Regex.Term.anyChar;
import static roth.ori.fling.examples.Regex.Term.anyDigit;
import static roth.ori.fling.examples.Regex.Term.either;
import static roth.ori.fling.examples.Regex.Term.exactly;
import static roth.ori.fling.examples.Regex.Term.noneOrMore;
import static roth.ori.fling.examples.Regex.Term.oneOrMore;
import static roth.ori.fling.examples.Regex.Term.option;
import static roth.ori.fling.examples.Regex.Term.or;
import static roth.ori.fling.examples.Regex.Term.re;

import java.io.IOException;

import roth.ori.fling.api.Fling.FlingBNF;
import roth.ori.fling.api.Main;
import roth.ori.fling.export.Grammar;
import roth.ori.fling.symbols.Symbol;
import roth.ori.fling.symbols.Terminal;

public class Regex extends Grammar {
  private static final String PACKAGE_PATH = Main.packagePath;
  private static final String PROJECT_PATH = Main.projectPath;

  public static enum Term implements Terminal {
    re, exactly, and, or, option, noneOrMore, oneOrMore, either, anyChar, anyDigit
  }

  public static enum NT implements Symbol {
    Expression, RE, Tail
  }

  @Override public FlingBNF bnf() {
    Class<String> String = String.class;
    return buildFlingBNF(Term.class, NT.class, "Regex", PACKAGE_PATH, PROJECT_PATH) //
        .start(Expression) //
        .derive(Expression).to(re, RE) //
        .derive(RE).to(exactly.with(String), Tail) //
        /**/.or(option.with(RE), Tail) //
        /**/.or(noneOrMore.with(RE), Tail) //
        /**/.or(oneOrMore.with(RE), Tail) //
        /**/.or(either.with(RE, RE), Tail) //
        /**/.or(anyChar, Tail) //
        /**/.or(anyDigit, Tail) //
        .derive(Tail).to(and, RE).or(or, RE).orNone();
  }
  public static void main(String[] args) throws IOException {
    new Regex().generateGrammarFiles();
  }
}
