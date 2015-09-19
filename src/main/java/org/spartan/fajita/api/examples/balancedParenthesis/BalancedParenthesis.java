package org.spartan.fajita.api.examples.balancedParenthesis;

import static org.spartan.fajita.api.bnf.symbols.Terminal.epsilon;
import static org.spartan.fajita.api.examples.balancedParenthesis.BalancedParenthesis.NT.*;
import static org.spartan.fajita.api.examples.balancedParenthesis.BalancedParenthesis.Term.*;

import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.BNFBuilder;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;
import org.spartan.fajita.api.bnf.symbols.Type;
import org.spartan.fajita.api.examples.balancedParenthesis.states.Q0;
import org.spartan.fajita.api.examples.balancedParenthesis.states.Q1;
import org.spartan.fajita.api.examples.balancedParenthesis.states.Q2;
import org.spartan.fajita.api.examples.balancedParenthesis.states.Q3;
import org.spartan.fajita.api.examples.balancedParenthesis.states.Q4;
import org.spartan.fajita.api.parser.LRParser;

public class BalancedParenthesis {
  public static void expressionBuilder() {
    Q4<Q0, Q3<Q0, Q2<Q0, Q1<Q0>>>> lp = new Q0().lp();
  }

  static enum Term implements Terminal {
    lp, rp, build;
    @Override public Type type() {
      return Type.VOID;
    }
  }

  static enum NT implements NonTerminal {
    START, BALANCED, EMPTY, NON_EMPTY;
  }

  public static void buildBNF() {
    BNF bnf = new BNFBuilder(Term.class, NT.class) //
        .startConfig() //
        .setApiNameTo("Balanced Parenthesis") //
        .setStartSymbols(START) //
        .endConfig() //
        .derive(START).to(BALANCED).and(build) //
        .derive(BALANCED).to(EMPTY).or(NON_EMPTY)//
        .derive(EMPTY).to(epsilon) //
        .derive(NON_EMPTY).to(lp).and(BALANCED).and(rp).and(BALANCED) //
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
