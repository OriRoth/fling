package roth.ori.fling.examples.usage;

import static roth.ori.fling.junk.EBNF.derive;
import static roth.ori.fling.junk.Literal.attribute;
import static roth.ori.fling.junk.Literal.either;
import static roth.ori.fling.junk.Literal.oneOrMore;
import static roth.ori.fling.junk.Literal.option;

import roth.ori.fling.symbols.Symbol;
import roth.ori.fling.symbols.Terminal;
import roth.ori.fling.symbols.types.VarArgs;

public class EBNF {
  public static void main(String[] args) {
    derive("EBNF").to(oneOrMore("Rule")) //
        .derive("Rule").to(attribute("derive", Symbol.class).and("To").and(option("ClauseTail"))) //
        .derive("To").to(attribute("to", "Literal")).or(attribute("to", String.class)) //
        .derive("ClauseTail").to(oneOrMore(either("and", "or").and("Literal"))) //
        .derive("Literal").to(attribute("oneOrMore", "Literal").and(option("ClauseTail"))) //
        /**/.or(attribute("oneOrMore", String.class).and(option("ClauseTail"))) //
        /**/.or(attribute("noneOrMore", "Literal").and(option("ClauseTail"))) //
        /**/.or(attribute("noneOrMore", String.class).and(option("ClauseTail"))) //
        /**/.or(attribute("option", "Literal").and(option("ClauseTail"))) //
        /**/.or(attribute("option", String.class).and(option("ClauseTail"))) //
        /**/.or(attribute("attribute", Terminal.class, new VarArgs(Object.class)).and(option("ClauseTail"))) //
        .$();
  }
}
