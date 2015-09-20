package org.spartan.fajita.api.examples.balancedParenthesis;

import static org.spartan.fajita.api.examples.balancedParenthesis.BalancedParenthesis.NT.BALANCED;
import static org.spartan.fajita.api.examples.balancedParenthesis.BalancedParenthesis.NT.START;
import static org.spartan.fajita.api.examples.balancedParenthesis.BalancedParenthesis.Term.*;

import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.BNFBuilder;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;
import org.spartan.fajita.api.bnf.symbols.Type;
import org.spartan.fajita.api.parser.LRParser;

public class BalancedParenthesis {
  public static void expressionBuilder() {
    //
  }

  static enum Term implements Terminal {
    lp, rp, build;
    @Override public Type type() {
      return Type.VOID;
    }
  }

  static enum NT implements NonTerminal {
    START, BALANCED;
  }

  public static void buildBNF() {
    BNF bnf = new BNFBuilder(Term.class, NT.class) //
        .startConfig() //
        .setApiNameTo("Balanced Parenthesis") //
        .setStartSymbols(START) //
        .endConfig() //
        .derive(START).to(BALANCED).and(build) //
        /*     */.or().to(build) //
        .derive(BALANCED).to(lp).and(BALANCED).and(rp).and(BALANCED) //
        /*        */.or().to(lp).and(BALANCED).and(rp) //
        /*        */.or().to(lp).and(rp).and(BALANCED) //
        /*        */.or().to(lp).and(rp) //
        .finish();
    System.out.println(bnf);
    LRParser parser = new LRParser(bnf);
    System.out.println(parser.states);
    System.out.println(parser);
  }
  public static void main(final String[] args) {
    buildBNF();
  }
}
