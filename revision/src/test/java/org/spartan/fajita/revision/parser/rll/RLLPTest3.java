package org.spartan.fajita.revision.parser.rll;

import static org.spartan.fajita.revision.api.Fajita.attribute;
import static org.spartan.fajita.revision.examples.Datalog.Term.by;
import static org.spartan.fajita.revision.examples.Datalog.Term.fact;
import static org.spartan.fajita.revision.examples.Datalog.Term.is;
import static org.spartan.fajita.revision.examples.Datalog.Term.rule;
import static org.spartan.fajita.revision.examples.Parenthesis.Term.o;
import static org.spartan.fajita.revision.examples.TestAnB.Term.d;
import static org.spartan.fajita.revision.examples.TestAnBn.Term.a;
import static org.spartan.fajita.revision.examples.TestAnBn.Term.b;
import static org.spartan.fajita.revision.examples.TestAnBnCD.Term.a2;
import static org.spartan.fajita.revision.examples.TestAnBnCD.Term.b2;
import static org.spartan.fajita.revision.examples.TestAnBnCD.Term.c2;
import static org.spartan.fajita.revision.examples.TestAnBnCD.Term.d2;
import static org.spartan.fajita.revision.examples.TestAnBnCD.Term.e2;
import static org.spartan.fajita.revision.examples.TestAnBnCD.Term.f2;
import static org.spartan.fajita.revision.examples.TestAnCDBn.Term.a1;
import static org.spartan.fajita.revision.examples.TestAnCDBn.Term.b1;
import static org.spartan.fajita.revision.examples.TestAnCDBn.Term.c1;
import static org.spartan.fajita.revision.examples.TestAnCDBn.Term.d1;
import static org.spartan.fajita.revision.examples.TestAnCDBn.Term.e1;
import static org.spartan.fajita.revision.examples.TestJumps.Term.x;
import static org.spartan.fajita.revision.examples.TestJumps.Term.y;
import static org.spartan.fajita.revision.examples.Parenthesis.Term.c;

import org.junit.Test;
import org.spartan.fajita.revision.examples.Datalog;
import org.spartan.fajita.revision.examples.Parenthesis;
import org.spartan.fajita.revision.examples.TestAnB;
import org.spartan.fajita.revision.examples.TestAnBn;
import org.spartan.fajita.revision.examples.TestAnBnCD;
import org.spartan.fajita.revision.examples.TestAnCDBn;
import org.spartan.fajita.revision.examples.TestJumps;
import org.spartan.fajita.revision.symbols.types.VarArgs;

@SuppressWarnings("static-method") public class RLLPTest3 {
  @Test public void datalog() {
    RLLPConcrete3 rllp = new RLLPConcrete3(new Datalog().bnf().bnf());
    rllp.consume( //
        attribute(fact, String.class), //
        attribute(by, new VarArgs(String.class)), //
        attribute(rule, String.class), //
        attribute(by, new VarArgs(String.class)), //
        attribute(is, String.class), //
        attribute(by, new VarArgs(String.class)), //
        attribute(is, String.class), //
        attribute(by, new VarArgs(String.class)), //
        attribute(fact, String.class), //
        attribute(by, new VarArgs(String.class)) //
    );
    assert !rllp.rejected();
    assert rllp.accepted();
  }
  @Test public void jumps() {
    RLLPConcrete3 rllp = new RLLPConcrete3(new TestJumps().bnf().bnf());
    rllp.consume( //
        x, y, y, x, y, y //
    );
    assert !rllp.rejected();
    assert rllp.accepted();
  }
  @Test public void anbn1() {
    RLLPConcrete3 rllp = new RLLPConcrete3(new TestAnBn().bnf().bnf());
    rllp.consume( //
        a, a, b, b //
    );
    assert !rllp.rejected();
    assert rllp.accepted();
  }
  @Test public void anbn2() {
    RLLPConcrete3 rllp = new RLLPConcrete3(new TestAnBn().bnf().bnf());
    rllp.consume( //
        a, a, a, a, a, a, b, b, b, b, b, b //
    );
    assert !rllp.rejected();
    assert rllp.accepted();
  }
  @Test public void anbn3() {
    RLLPConcrete3 rllp = new RLLPConcrete3(new TestAnBn().bnf().bnf());
    rllp.consume( //
        a, a, a, a, a, a, b, b, b, b, b, a //
    );
    assert rllp.rejected();
  }
  @Test public void anb1() {
    RLLPConcrete3 rllp = new RLLPConcrete3(new TestAnB().bnf().bnf());
    rllp.consume( //
        org.spartan.fajita.revision.examples.TestAnB.Term.c,
        org.spartan.fajita.revision.examples.TestAnB.Term.c,
        org.spartan.fajita.revision.examples.TestAnB.Term.c,
        org.spartan.fajita.revision.examples.TestAnB.Term.c, d //
    );
    assert !rllp.rejected();
    assert rllp.accepted();
  }
  @Test public void ancdbn() {
    RLLPConcrete3 rllp = new RLLPConcrete3(new TestAnCDBn().bnf().bnf());
    rllp.consume( //
        a1, a1, a1, a1, a1, a1, b1, c1, d1, e1, b1, b1, b1, b1, b1 //
    );
    assert !rllp.rejected();
    assert rllp.accepted();
  }
  @Test public void anbncd1() {
    RLLPConcrete3 rllp = new RLLPConcrete3(new TestAnBnCD().bnf().bnf());
    rllp.consume( //
        a2, b2 //
    );
    assert !rllp.rejected();
    assert rllp.accepted();
  }
  @Test public void anbncd2() {
    RLLPConcrete3 rllp = new RLLPConcrete3(new TestAnBnCD().bnf().bnf());
    rllp.consume( //
        a2, c2, a2, a2, d2, c2, a2, d2, b2, e2, b2, b2, f2, e2, b2, f2 //
    );
    assert !rllp.rejected();
    assert rllp.accepted();
  }
  @Test public void parenthesis1() {
    RLLPConcrete3 rllp = new RLLPConcrete3(new Parenthesis().bnf().bnf());
    rllp.consume( //
        o, o, o, c, c, o, c, c //
    );
    assert !rllp.rejected();
    assert rllp.accepted();
  }
  @Test public void parenthesis2() {
    RLLPConcrete3 rllp = new RLLPConcrete3(new Parenthesis().bnf().bnf());
    rllp.consume( //
        o, o, o, c, c, c, c, o //
    );
    assert rllp.rejected();
  }
}
