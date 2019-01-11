package roth.ori.fling.parser.rll;

import static roth.ori.fling.examples.Parenthesis.Term.c;
import static roth.ori.fling.examples.Parenthesis.Term.o;
import static roth.ori.fling.examples.TestAnB.Term.d;
import static roth.ori.fling.examples.TestAnBn.Term.a;
import static roth.ori.fling.examples.TestAnBn.Term.b;
import static roth.ori.fling.examples.TestAnBnCD.Term.a2;
import static roth.ori.fling.examples.TestAnBnCD.Term.b2;
import static roth.ori.fling.examples.TestAnBnCD.Term.c2;
import static roth.ori.fling.examples.TestAnBnCD.Term.d2;
import static roth.ori.fling.examples.TestAnBnCD.Term.e2;
import static roth.ori.fling.examples.TestAnBnCD.Term.f2;
import static roth.ori.fling.examples.TestAnCDBn.Term.a1;
import static roth.ori.fling.examples.TestAnCDBn.Term.b1;
import static roth.ori.fling.examples.TestAnCDBn.Term.c1;
import static roth.ori.fling.examples.TestAnCDBn.Term.d1;
import static roth.ori.fling.examples.TestAnCDBn.Term.e1;
import static roth.ori.fling.examples.TestJumps.Term.x;
import static roth.ori.fling.examples.TestJumps.Term.y;

import org.junit.Test;

import roth.ori.fling.examples.Parenthesis;
import roth.ori.fling.examples.TestAnB;
import roth.ori.fling.examples.TestAnBn;
import roth.ori.fling.examples.TestAnBnCD;
import roth.ori.fling.examples.TestAnCDBn;
import roth.ori.fling.examples.TestJumps;

@SuppressWarnings("static-method") public class RLLPTest {
  @Test public void jumps() {
    RLLPConcrete rllp = new RLLPConcrete(new TestJumps().bnf().bnf());
    rllp.consume( //
        x, y, y, x, y, y //
    );
    assert !rllp.rejected();
    assert rllp.accepted();
  }
  @Test public void anbn1() {
    RLLPConcrete rllp = new RLLPConcrete(new TestAnBn().bnf().bnf());
    rllp.consume( //
        a, a, b, b //
    );
    assert !rllp.rejected();
    assert rllp.accepted();
  }
  @Test public void anbn2() {
    RLLPConcrete rllp = new RLLPConcrete(new TestAnBn().bnf().bnf());
    rllp.consume( //
        a, a, a, a, a, a, b, b, b, b, b, b //
    );
    assert !rllp.rejected();
    assert rllp.accepted();
  }
  @Test public void anbn3() {
    RLLPConcrete rllp = new RLLPConcrete(new TestAnBn().bnf().bnf());
    rllp.consume( //
        a, a, a, a, a, a, b, b, b, b, b, a //
    );
    assert rllp.rejected();
  }
  @Test public void anb1() {
    RLLPConcrete rllp = new RLLPConcrete(new TestAnB().bnf().bnf());
    rllp.consume( //
        roth.ori.fling.examples.TestAnB.Term.c, roth.ori.fling.examples.TestAnB.Term.c, roth.ori.fling.examples.TestAnB.Term.c,
        roth.ori.fling.examples.TestAnB.Term.c, d //
    );
    assert !rllp.rejected();
    assert rllp.accepted();
  }
  @Test public void ancdbn() {
    RLLPConcrete rllp = new RLLPConcrete(new TestAnCDBn().bnf().bnf());
    rllp.consume( //
        a1, a1, a1, a1, a1, a1, b1, c1, d1, e1, b1, b1, b1, b1, b1 //
    );
    assert !rllp.rejected();
    assert rllp.accepted();
  }
  @Test public void anbncd1() {
    RLLPConcrete rllp = new RLLPConcrete(new TestAnBnCD().bnf().bnf());
    rllp.consume( //
        a2, b2 //
    );
    assert !rllp.rejected();
    assert rllp.accepted();
  }
  @Test public void anbncd2() {
    RLLPConcrete rllp = new RLLPConcrete(new TestAnBnCD().bnf().bnf());
    rllp.consume( //
        a2, c2, a2, a2, d2, c2, a2, d2, b2, e2, b2, b2, f2, e2, b2, f2 //
    );
    assert !rllp.rejected();
    assert rllp.accepted();
  }
  @Test public void parenthesis1() {
    RLLPConcrete rllp = new RLLPConcrete(new Parenthesis().bnf().bnf());
    rllp.consume( //
        o, o, o, c, c, o, c, c //
    );
    assert !rllp.rejected();
    assert rllp.accepted();
  }
  @Test public void parenthesis2() {
    RLLPConcrete rllp = new RLLPConcrete(new Parenthesis().bnf().bnf());
    rllp.consume( //
        o, o, o, c, c, c, c, o //
    );
    assert rllp.rejected();
  }
}
