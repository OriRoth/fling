package org.spartan.fajita.revision.parser.rll;

import static org.spartan.fajita.revision.api.Fajita.attribute;
import static org.spartan.fajita.revision.examples.Datalog.NT.FactExpression;
import static org.spartan.fajita.revision.examples.Datalog.Term.by;
import static org.spartan.fajita.revision.examples.Datalog.Term.fact;
import static org.spartan.fajita.revision.examples.Datalog.Term.is;
import static org.spartan.fajita.revision.examples.Datalog.Term.rule;
import static org.spartan.fajita.revision.examples.malfunction.TestAnB.Term.c;
import static org.spartan.fajita.revision.examples.malfunction.TestAnB.Term.d;
import static org.spartan.fajita.revision.examples.malfunction.TestAnBn.Term.a;
import static org.spartan.fajita.revision.examples.malfunction.TestAnBn.Term.b;
import static org.spartan.fajita.revision.examples.malfunction.TestJumps.Term.x;
import static org.spartan.fajita.revision.examples.malfunction.TestJumps.Term.y;

import org.junit.Test;
import org.spartan.fajita.revision.examples.Datalog;
import org.spartan.fajita.revision.examples.malfunction.TestAnB;
import org.spartan.fajita.revision.examples.malfunction.TestAnBn;
import org.spartan.fajita.revision.examples.malfunction.TestJumps;
import org.spartan.fajita.revision.parser.ll.LLRecognizer;
import org.spartan.fajita.revision.symbols.types.VarArgs;

@SuppressWarnings("static-method") public class LLPTest {
  @Test public void datalog() {
    LLRecognizer llp = new LLRecognizer(new Datalog().bnf().bnf());
    assert llp.consume( //
        attribute(fact, FactExpression), //
        attribute(fact, FactExpression), //
        attribute(rule, FactExpression), //
        attribute(is, String.class), //
        attribute(by, new VarArgs(String.class)), //
        attribute(fact, FactExpression) //
    );
  }
  @Test public void jumps() {
    LLRecognizer llp = new LLRecognizer(new TestJumps().bnf().bnf());
    assert llp.consume( //
        x, y, x, y, y, x //
    );
  }
  @Test public void anbn1() {
    LLRecognizer llp = new LLRecognizer(new TestAnBn().bnf().bnf());
    assert llp.consume( //
        a, a, b, b //
    );
  }
  @Test public void anbn2() {
    LLRecognizer llp = new LLRecognizer(new TestAnBn().bnf().bnf());
    assert llp.consume( //
        a, a, a, a, a, a, b, b, b, b, b, b //
    );
  }
  @Test public void anbn3() {
    LLRecognizer llp = new LLRecognizer(new TestAnBn().bnf().bnf());
    assert !llp.consume( //
        a, a, a, a, a, a, b, b, b, b, b, a //
    );
  }
  @Test public void anb1() {
    LLRecognizer llp = new LLRecognizer(new TestAnB().bnf().bnf());
    assert llp.consume( //
        c, c, c, c, d //
    );
  }
}
