package roth.ori.fling.examples;

import static roth.ori.fling.examples.EBNF.NT.AndOr;
import static roth.ori.fling.examples.EBNF.NT.ClauseTail;
import static roth.ori.fling.examples.EBNF.NT.EBNF;
import static roth.ori.fling.examples.EBNF.NT.Literal;
import static roth.ori.fling.examples.EBNF.NT.Rule;
import static roth.ori.fling.examples.EBNF.NT.To;
import static roth.ori.fling.examples.EBNF.Term.and;
import static roth.ori.fling.examples.EBNF.Term.attribute;
import static roth.ori.fling.examples.EBNF.Term.derive;
import static roth.ori.fling.examples.EBNF.Term.either;
import static roth.ori.fling.examples.EBNF.Term.noneOrMore;
import static roth.ori.fling.examples.EBNF.Term.oneOrMore;
import static roth.ori.fling.examples.EBNF.Term.option;
import static roth.ori.fling.examples.EBNF.Term.or;
import static roth.ori.fling.examples.EBNF.Term.to;

import java.io.IOException;

import roth.ori.fling.api.Fling;
import roth.ori.fling.api.Fling.FlingBNF;
import roth.ori.fling.api.Main;
import roth.ori.fling.export.Grammar;
import roth.ori.fling.symbols.NonTerminal;
import roth.ori.fling.symbols.Terminal;
import roth.ori.fling.symbols.types.VarArgs;

public class EBNF extends Grammar {
  public static enum Term implements Terminal {
    derive, to, and, or, orNone, option, noneOrMore, oneOrMore, either, attribute
  }

  public static enum NT implements NonTerminal {
    EBNF, Rule, ClauseTail, Literal, To, AndOr
  }

  @Override public FlingBNF bnf() {
    return Fling.build(getClass(), Term.class, NT.class, "EBNF", Main.packagePath, Main.projectPath) //
        .start(EBNF) //
        .derive(EBNF).to(Fling.oneOrMore(Rule)) //
        .derive(Rule).to(Fling.attribute(derive, String.class), To, Fling.option(ClauseTail)) //
        .derive(To).to(Fling.attribute(to, Literal)) //
        /**/.or(Fling.attribute(to, String.class)) //
        .derive(ClauseTail).to(Fling.noneOrMore(AndOr)) //
        .derive(AndOr).to(Fling.either(Fling.attribute(and, Literal), Fling.attribute(or, Literal))) //
        /**/.or(Fling.either(Fling.attribute(and, String.class), Fling.attribute(or, String.class))) //
        .derive(Literal).to(Fling.attribute(oneOrMore, Literal), Fling.option(ClauseTail)) //
        /**/.or(Fling.attribute(oneOrMore, String.class), Fling.option(ClauseTail)) //
        /**/.or(Fling.attribute(noneOrMore, Literal), Fling.option(ClauseTail)) //
        /**/.or(Fling.attribute(noneOrMore, String.class), Fling.option(ClauseTail)) //
        /**/.or(Fling.attribute(option, Literal), Fling.option(ClauseTail)) //
        /**/.or(Fling.attribute(option, String.class), Fling.option(ClauseTail)) //
        /**/.or(Fling.attribute(either, Literal, Literal), Fling.option(ClauseTail)) //
        /**/.or(Fling.attribute(either, Literal, String.class), Fling.option(ClauseTail)) //
        /**/.or(Fling.attribute(either, String.class, Literal), Fling.option(ClauseTail)) //
        /**/.or(Fling.attribute(either, String.class, String.class), Fling.option(ClauseTail)) //
        /**/.or(Fling.attribute(attribute, String.class, new VarArgs(Object.class)), Fling.option(ClauseTail)) //
    ;
  }
  public static void main(String[] args) throws IOException {
    new EBNF().generateGrammarFiles();
  }
}
