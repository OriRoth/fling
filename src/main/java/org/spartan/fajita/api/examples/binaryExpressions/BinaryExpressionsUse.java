package org.spartan.fajita.api.examples.binaryExpressions;

import static org.spartan.fajita.api.examples.binaryExpressions.BinaryExpressionsUse.NT.AND;
import static org.spartan.fajita.api.examples.binaryExpressions.BinaryExpressionsUse.NT.EXPRESSION;
import static org.spartan.fajita.api.examples.binaryExpressions.BinaryExpressionsUse.NT.LITERAL;
import static org.spartan.fajita.api.examples.binaryExpressions.BinaryExpressionsUse.NT.NOT;
import static org.spartan.fajita.api.examples.binaryExpressions.BinaryExpressionsUse.NT.OR;
import static org.spartan.fajita.api.examples.binaryExpressions.BinaryExpressionsUse.NT.S;
import static org.spartan.fajita.api.examples.binaryExpressions.BinaryExpressionsUse.Term.and;
import static org.spartan.fajita.api.examples.binaryExpressions.BinaryExpressionsUse.Term.bool;
import static org.spartan.fajita.api.examples.binaryExpressions.BinaryExpressionsUse.Term.not;
import static org.spartan.fajita.api.examples.binaryExpressions.BinaryExpressionsUse.Term.or;

import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.BNFBuilder;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;

public class BinaryExpressionsUse {
//  public static void expressionBuilder() {
//    // top down
//    Compound e0 = bool(true);
//    Literal e1 = bool(true).or().bool(false);
//    Literal e2 = bool(true).or().bool(false).and().bool(false);
//    Literal e3 = not().not().bool(true);
//    // bottom up
//    Compound e4 = not(bool(true));
//    Compound e5 = or(bool(true), bool(false));
//    Compound e6 = and(bool(false), bool(true));
//    Compound e7 = or(or(bool(true), bool(false)), and(bool(true), bool(false)));
//    showASTs(e0, e1, e2, e3, e4, e5, e6, e7);
//  }

  enum Term implements Terminal {
    bool, and, or, not;
  }

  static enum NT implements NonTerminal {
    S, LITERAL, EXPRESSION, AND, OR, NOT;
  }

  public static void buildBNF() {
    // define the rules
    BNF b = new BNFBuilder(Term.class, NT.class) //
        //
        .start(S) //
        //
        .derive(S).to(EXPRESSION) //
        .derive(EXPRESSION).to(OR).or().to(AND).or().to(LITERAL).or().to(NOT) //
        .derive(LITERAL).to(bool)//
        .derive(OR).to(EXPRESSION).and(or).and(EXPRESSION)//
        .derive(AND).to(EXPRESSION).and(and).and(EXPRESSION)//
        .derive(NOT).to(not).and(EXPRESSION)//
        .finish();
    System.out.println(b);
  }
}
