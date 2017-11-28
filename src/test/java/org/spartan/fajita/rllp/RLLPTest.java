package org.spartan.fajita.rllp;

import static org.spartan.fajita.api.EFajita.noneOrMore;
import static org.spartan.fajita.api.EFajita.oneOrMore;
import static org.spartan.fajita.rllp.RLLPTest.NT.BODY;
import static org.spartan.fajita.rllp.RLLPTest.NT.LITERAL;
import static org.spartan.fajita.rllp.RLLPTest.NT.RULE;
import static org.spartan.fajita.rllp.RLLPTest.NT.S;
import static org.spartan.fajita.rllp.RLLPTest.Term.body;
import static org.spartan.fajita.rllp.RLLPTest.Term.fact;
import static org.spartan.fajita.rllp.RLLPTest.Term.head;
import static org.spartan.fajita.rllp.RLLPTest.Term.literal;
import static org.spartan.fajita.rllp.RLLPTest.Term.name;
import static org.spartan.fajita.rllp.RLLPTest.Term.terms;

import org.junit.Test;
import org.spartan.fajita.api.EFajita;
import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.BNFRenderer;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;
import org.spartan.fajita.api.rllp.RLLPConcrete;

public class RLLPTest {
  static enum Term implements Terminal {
    head, body, fact, literal, name, terms
  }

  static enum NT implements NonTerminal {
    S, RULE, LITERAL, BODY
  }

  public static BNF bnf() {
    return EFajita.build(Term.class, NT.class) //
        .setApiName("Datalog") //
        .start(S) //
        .derive(S).to(noneOrMore(RULE)) //
        .derive(RULE) //
        /**/.to(fact, LITERAL) //
        /**/.or(head, LITERAL, BODY) //
        .derive(BODY).to(body, oneOrMore(literal, LITERAL)) //
        .derive(LITERAL).to(name, terms) //
        .go();
  }

  RLLPConcrete $ = new RLLPConcrete(bnf());

  @Test public void a() {
    System.out.println(bnf().toString(BNFRenderer.builtin.ASCII));
    assert $.consume( //
        fact, name, terms, //
        fact, name, terms, //
        head, name, terms, body, //
        /**/literal, name, terms, //
        /**/literal, name, terms, //
        fact, name, terms //
    ).accepted();
  }
}
