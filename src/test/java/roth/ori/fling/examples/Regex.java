package roth.ori.fling.examples;

import static roth.ori.fling.api.Fling.attribute;
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

import roth.ori.fling.api.Fling;
import roth.ori.fling.api.Fling.FlingBNF;
import roth.ori.fling.api.Main;
import roth.ori.fling.export.Grammar;
import roth.ori.fling.symbols.NonTerminal;
import roth.ori.fling.symbols.Terminal;

public class Regex extends Grammar {
  public static enum Term implements Terminal {
    re, exactly, and, or, option, noneOrMore, oneOrMore, either, anyChar, anyDigit
  }

  public static enum NT implements NonTerminal {
    Expression, RE, Tail
  }

  @Override public FlingBNF bnf() {
    return Fling.build(getClass(), Term.class, NT.class, "Regex", Main.packagePath, Main.projectPath) //
        .start(Expression) //
        .derive(Expression).to(re, RE) //
        .derive(RE).to(attribute(exactly, String.class), Tail) //
        /**/.or(attribute(option, RE), Tail) //
        /**/.or(attribute(noneOrMore, RE), Tail) //
        /**/.or(attribute(oneOrMore, RE), Tail) //
        /**/.or(attribute(either, RE, RE), Tail) //
        /**/.or(anyChar, Tail) //
        /**/.or(anyDigit, Tail) //
        .derive(Tail).to(and, RE).or(or, RE).orNone();
  }
  public static void main(String[] args) throws IOException {
    new Regex().generateGrammarFiles();
  }
}
