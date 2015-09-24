package org.spartan.fajita.api.examples.balancedParenthesis;

import static org.spartan.fajita.api.examples.balancedParenthesis.BalancedParenthesis.NT.BALANCED;
import static org.spartan.fajita.api.examples.balancedParenthesis.BalancedParenthesis.Term.lp;
import static org.spartan.fajita.api.examples.balancedParenthesis.BalancedParenthesis.Term.rp;

import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.BNFBuilder;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;
import org.spartan.fajita.api.bnf.symbols.Type;
import org.spartan.fajita.api.examples.balancedParenthesis.states.Q0;
import org.spartan.fajita.api.examples.balancedParenthesis.states.Q1;
import org.spartan.fajita.api.examples.balancedParenthesis.states.Q2;
import org.spartan.fajita.api.examples.balancedParenthesis.states.Q4;
import org.spartan.fajita.api.parser.LRParser;

public class BalancedParenthesis {
  @SuppressWarnings({ "rawtypes", "unused" }) public static void expressionBuilder() {
    Q0 q0 = new Q0();
    Q2<Q0, Q1> lp1 = q0.lp();
    Q2<Q2<Q0, ?>, Q4> lp2 = lp1.lp();
    Q2<Q2<Q2<Q0, ?>, ?>, Q4> lp3 = lp2.lp();
    Q2<Q2<Q2<Q2<Q0, ?>, ?>, ?>, Q4> lp4 = lp3.lp();
    Q2<Q2<Q2<Q2<Q2<Q0, ?>, ?>, ?>, ?>, Q4> lp5 = lp4.lp();
    Q2<Q2<Q2<Q2<Q2<Q2<Q0, ?>, ?>, ?>, ?>, ?>, Q4> lp6 = lp5.lp();
    Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q0, ?>, ?>, ?>, ?>, ?>, ?>, Q4> lp7 = lp6.lp();
    Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q0, ?>, ?>, ?>, ?>, ?>, ?>, ?>, Q4> lp8 = lp7.lp();
    Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q0, ?>, ?>, ?>, ?>, ?>, ?>, ?>, ?>, Q4> lp9 = lp8.lp();
    Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q0, ?>, ?>, ?>, ?>, ?>, ?>, ?>, ?>, ?>, Q4> lp10 = lp9.lp();
    Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q0, ?>, ?>, ?>, ?>, ?>, ?>, ?>, ?>, ?>, ?>, Q4> lp11 = lp10.lp();
    Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q0, ?>, ?>, ?>, ?>, ?>, ?>, ?>, ?>, ?>, ?>, ?>, Q4> lp12 = lp11.lp();
    Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q0, ?>, ?>, ?>, ?>, ?>, ?>, ?>, ?>, ?>, ?>, ?>, ?>, Q4> lp13 = lp12.lp();
    Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q0, ?>, ?>, ?>, ?>, ?>, ?>, ?>, ?>, ?>, ?>, ?>, ?>, ?>, Q4> lp14 = lp13.lp();
    Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q0, ?>, ?>, ?>, ?>, ?>, ?>, ?>, ?>, ?>, ?>, ?>, ?>, ?>, ?>, Q4> lp15 = lp14.lp();
    Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q0, ?>, ?>, ?>, ?>, ?>, ?>, ?>, ?>, ?>, ?>, ?>, ?>, ?>, ?>, ?>, Q4> lp16 = lp15
        .lp();
    Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q0, ?>, ?>, ?>, ?>, ?>, ?>, ?>, ?>, ?>, ?>, ?>, ?>, ?>, ?>, ?>, ?>, Q4> lp17 = lp16
        .lp();
    Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q0, ?>, ?>, ?>, ?>, ?>, ?>, ?>, ?>, ?>, ?>, ?>, ?>, ?>, ?>, ?>, ?>, ?>, Q4> lp18 = lp17
        .lp();
    Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q2<Q0, ?>, ?>, ?>, ?>, ?>, ?>, ?>, ?>, ?>, ?>, ?>, ?>, ?>, ?>, ?>, ?>, ?>, ?>, Q4> lp19 = lp18
        .lp();
  }

  static enum Term implements Terminal {
    lp, rp, build;
    @Override public Type type() {
      return Type.VOID;
    }
  }

  static enum NT implements NonTerminal {
    BALANCED;
  }

  public static void buildBNF() {
    BNF bnf = new BNFBuilder(Term.class, NT.class) //
        .startConfig() //
        .setApiNameTo("Balanced Parenthesis") //
        .setStartSymbols(BALANCED) //
        .endConfig() //
        .derive(BALANCED).to(lp).and(BALANCED).and(rp) //
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
