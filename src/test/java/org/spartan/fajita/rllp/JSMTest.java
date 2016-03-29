package org.spartan.fajita.rllp;

import static org.spartan.fajita.rllp.JSMTest.NT.*;
import static org.spartan.fajita.rllp.JSMTest.Term.c;
import static org.spartan.fajita.rllp.JSMTest.Term.d;

import java.util.Set;
import java.util.function.Predicate;

import org.junit.Before;
import org.junit.BeforeClass;
import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.BNFBuilder;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;
import org.spartan.fajita.api.bnf.symbols.Verb;
import org.spartan.fajita.api.rllp.Item;
import org.spartan.fajita.api.rllp.JSM;
import org.spartan.fajita.api.rllp.RLLP;

@SuppressWarnings("static-method") public class JSMTest {
  static enum Term implements Terminal {
    a, b, c, d;
  }

  static enum NT implements NonTerminal {
    S, A, B, C, D
  }

  private static BNF bnf;
  private static RLLP rllp;
  private  JSM jsm;
  @BeforeClass public static void init() {
    bnf = new BNFBuilder(Term.class, NT.class) //
        .start(S) //
        .derive(S).to(A).and(D) //
        .derive(A).to(B) //
        .derive(B).to(C) //
        .derive(C).to(c).orNone() //
        .derive(D).to(d) //
        .go();
    rllp = new RLLP(bnf);
  }
  @Before public void initJSM(){
    final Set<Verb> verbs = bnf.getVerbs();
    jsm = new JSM(verbs, rllp.jumpsTable);
  }
  
  public void testTrivial(){
    getAnyMatchingItem()
    jsm.push(i);
  }
  static Item getAnyMatchingItem(Predicate<Item> predicate) {
    return rllp.items.stream().filter(predicate).findAny().get();
  }
}
