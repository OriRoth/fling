package org.spartan.fajita.revision.parser.rll;

import static org.spartan.fajita.revision.api.Fajita.attribute;
import static org.spartan.fajita.revision.examples.Datalog.NT.FactExpression;
import static org.spartan.fajita.revision.examples.Datalog.Term.by;
import static org.spartan.fajita.revision.examples.Datalog.Term.fact;
import static org.spartan.fajita.revision.examples.Datalog.Term.is;
import static org.spartan.fajita.revision.examples.Datalog.Term.rule;
import static org.spartan.fajita.revision.examples.malfunction.TestJumps.Term.a;
import static org.spartan.fajita.revision.examples.malfunction.TestJumps.Term.b;

import org.junit.Test;
import org.spartan.fajita.revision.examples.Datalog;
import org.spartan.fajita.revision.examples.malfunction.TestJumps;
import org.spartan.fajita.revision.symbols.types.VarArgs;

@SuppressWarnings("static-method") public class RLLPTest {
  @Test public void datalog() {
    RLLPConcrete rllp = new RLLPConcrete(new Datalog().bnf().bnf());
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
  @Test public void simpleTest() {
    RLLPConcrete rllp = new RLLPConcrete(new TestJumps().bnf().bnf());
    rllp.consume( //
        a, b, a, b, b, a //
    );
    assert !rllp.rejected();
  }
}
