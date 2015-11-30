package org.spartan.fajita.api.examples.bootstrap;

import static org.spartan.fajita.api.examples.bootstrap.BNFBootstrap.NT.*;
import static org.spartan.fajita.api.examples.bootstrap.BNFBootstrap.Term.*;

import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.BNFBuilder;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Symbol;
import org.spartan.fajita.api.bnf.symbols.Terminal;
import org.spartan.fajita.api.bnf.symbols.Type;

public class BNFBootstrap {
  public static void expressionBuilder() {
    // showASTs();
  }

  static enum Term implements Terminal {
    setApiName, derive, to, //
    and, toOneOf, or, //
    finish;
    @Override public String toString() {
      return methodSignatureString();
    }
  }

  static enum NT implements NonTerminal {
    S, RULE, RULE_TYPE, ABSTRACT_RULE, //
    NEXT_ABSTRACT, OR, NORMAL_RULE, NEXT_NORMAL, //
    AND, NEXT, NEXT_RULE;
  }

  // TODO: fix bootstrap.
  public static void buildBNF() {
    BNF b = new BNFBuilder(Term.class, NT.class) //
        .start(S) //
        //
        .derive(S).to(setApiName).and(RULE).and(NEXT) //
        .derive(RULE).to(derive).and(RULE_TYPE) //
        .derive(RULE_TYPE).to(ABSTRACT_RULE).or().to(NORMAL_RULE) //
        .derive(ABSTRACT_RULE).to(to) //
        /*             */.or().to(to).and(NEXT_ABSTRACT) //
        .derive(NEXT_ABSTRACT).to(OR) //
        /*             */.or().to(OR).and(NEXT_ABSTRACT) //
        .derive(OR).to(or).and(NEXT_ABSTRACT) //
        .derive(NORMAL_RULE).to(to) //
        /*           */.or().to(to).and(NEXT_NORMAL) //
        .derive(NEXT_NORMAL).to(AND) //
        /*            */.or().to(AND).and(NEXT_NORMAL) //
        .derive(AND).to(and).and(NEXT_NORMAL) //
        .derive(NEXT).to(NEXT_RULE).or().to(finish) //
        .derive(NEXT_RULE).to(RULE).and(NEXT) //
        .finish();
    System.out.println(b);
  }
}
