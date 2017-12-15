package org.spartan.fajita.revision.parser.rll;

import static org.spartan.fajita.revision.api.Fajita.attribute;
import static org.spartan.fajita.revision.examples.Datalog.NT.FactExpression;
import static org.spartan.fajita.revision.examples.Datalog.Term.by;
import static org.spartan.fajita.revision.examples.Datalog.Term.fact;
import static org.spartan.fajita.revision.examples.Datalog.Term.is;
import static org.spartan.fajita.revision.examples.Datalog.Term.rule;
import static org.spartan.fajita.revision.examples.malfunction.TestJumps.Term.x;
import static org.spartan.fajita.revision.examples.malfunction.TestJumps.Term.y;
import static org.spartan.fajita.revision.examples.malfunction.TestAnBn.Term.*;

import org.junit.Test;
import org.spartan.fajita.revision.examples.Datalog;
import org.spartan.fajita.revision.examples.malfunction.TestAnBn;
import org.spartan.fajita.revision.examples.malfunction.TestJumps;
import org.spartan.fajita.revision.symbols.types.VarArgs;

@SuppressWarnings("static-method") public class RLLPTest {
  @Test public void datalog() {
    RLLPConcrete2 rllp = new RLLPConcrete2(new Datalog().bnf().bnf());
    rllp.consume( //
        attribute(fact, FactExpression), //
        attribute(fact, FactExpression), //
        attribute(rule, FactExpression), //
        attribute(is, String.class), //
        attribute(by, new VarArgs(String.class)), //
        attribute(fact, FactExpression) //
    );
    assert !rllp.rejected();
  }
  @Test public void jumps() {
    RLLPConcrete2 rllp = new RLLPConcrete2(new TestJumps().bnf().bnf());
    rllp.consume( //
        x, y, x, y, y, x //
    );
    assert !rllp.rejected();
  }
  @Test public void anbn() {
    RLLPConcrete2 rllp = new RLLPConcrete2(new TestAnBn().bnf().bnf());
    rllp.consume( //
        a, a, a, a, a, b, b, b, b, b //
    );
    assert !rllp.rejected();
  }
}
