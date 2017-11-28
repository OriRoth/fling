package org.spartan.fajita.rllp;

import static org.spartan.fajita.api.EFajita.noneOrMore;
import static org.spartan.fajita.api.EFajita.oneOrMore;
import static org.spartan.fajita.rllp.RLLPTest.DatalogNT.*;
import static org.spartan.fajita.rllp.RLLPTest.DatalogTerm.*;
import static org.spartan.fajita.rllp.RLLPTest.SimpleNT.*;
import static org.spartan.fajita.rllp.RLLPTest.SimpleTerm.*;

import org.junit.Test;
import org.spartan.fajita.api.EFajita;
import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.BNFRenderer;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;
import org.spartan.fajita.api.rllp.RLLPConcrete;

@SuppressWarnings("static-method") public class RLLPTest {
  static enum DatalogTerm implements Terminal {
    head, body, fact, literal, name, terms
  }

  static enum DatalogNT implements NonTerminal {
    S, RULE, LITERAL, BODY
  }

  public static BNF datalog() {
    return EFajita.build(DatalogTerm.class, DatalogNT.class) //
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

  static enum SimpleTerm implements Terminal {
    a, b
  }

  static enum SimpleNT implements NonTerminal {
    S1
  }

  public static BNF simple() {
    return EFajita.build(SimpleTerm.class, SimpleNT.class) //
        .setApiName("Simple") //
        .start(S1) //
        .derive(S1).to(oneOrMore(a).separator(b)) //
        .go();
  }
  @Test public void a() {
    BNF bnf = datalog();
    // System.out.println(bnf.toString(BNFRenderer.builtin.ASCII));
    assert new RLLPConcrete(bnf).consume( //
        fact, name, terms, //
        fact, name, terms, //
        head, name, terms, body, //
        /**/literal, name, terms, //
        /**/literal, name, terms, //
        fact, name, terms //
    ).accepted();
  }
  @Test public void b() {
    BNF bnf = simple();
    System.out.println(bnf.toString(BNFRenderer.builtin.ASCII));
    new RLLPConcrete(bnf).consume( //
        a, b, a, b, a, b, a //
    );
  }
}
