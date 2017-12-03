package org.spartan.fajita.ell;

import static org.spartan.fajita.api.EFajita.noneOrMore;
import static org.spartan.fajita.api.EFajita.oneOrMore;
import static org.spartan.fajita.ell.EBNFAnalyzerTest.DatalogNT.BODY;
import static org.spartan.fajita.ell.EBNFAnalyzerTest.DatalogNT.LITERAL;
import static org.spartan.fajita.ell.EBNFAnalyzerTest.DatalogNT.RULE;
import static org.spartan.fajita.ell.EBNFAnalyzerTest.DatalogNT.S;
import static org.spartan.fajita.ell.EBNFAnalyzerTest.DatalogTerm.$;
import static org.spartan.fajita.ell.EBNFAnalyzerTest.DatalogTerm.body;
import static org.spartan.fajita.ell.EBNFAnalyzerTest.DatalogTerm.fact;
import static org.spartan.fajita.ell.EBNFAnalyzerTest.DatalogTerm.head;
import static org.spartan.fajita.ell.EBNFAnalyzerTest.DatalogTerm.literal;
import static org.spartan.fajita.ell.EBNFAnalyzerTest.DatalogTerm.name;
import static org.spartan.fajita.ell.EBNFAnalyzerTest.DatalogTerm.terms;

import org.junit.Test;
import org.spartan.fajita.api.EFajita;
import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.EBNFAnalyzer;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;

@SuppressWarnings("static-method") public class EBNFAnalyzerTest {
  static enum DatalogTerm implements Terminal {
    head, body, fact, literal, name, terms, $
  }

  static enum DatalogNT implements NonTerminal {
    S, RULE, LITERAL, BODY
  }

  public static BNF datalog() {
    return EFajita.build(null, DatalogTerm.class, DatalogNT.class) //
        .setApiName("Datalog") //
        .start(S) //
        .derive(S).to(noneOrMore(RULE), $) //
        .derive(RULE) //
        /**/.to(fact, LITERAL) //
        /**/.or(head, LITERAL, BODY) //
        .derive(BODY).to(body, oneOrMore(literal, LITERAL)) //
        .derive(LITERAL).to(name, terms) //
        .go();
  }
  @Test public void a() {
    EBNFAnalyzer ana = new EBNFAnalyzer(datalog());
    assert !ana.isNullable(S);
    System.out.println(ana.firstSetOf(S));
    System.out.println(ana.firstSetOf(RULE));
  }
}
