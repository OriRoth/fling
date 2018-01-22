package org.spartan.fajita.revision.programs;

import org.junit.Test;
import org.spartan.fajita.revision.examples.Datalog;
import org.spartan.fajita.revision.examples.usage.EBNF;
import org.spartan.fajita.revision.examples.usage.Exp;
import org.spartan.fajita.revision.examples.usage.Regex;
import org.spartan.fajita.revision.examples.TestAnB;
import org.spartan.fajita.revision.examples.TestAnBn;
import org.spartan.fajita.revision.examples.TestAnBnCD;
import org.spartan.fajita.revision.examples.TestAnCDBn;
import org.spartan.fajita.revision.examples.TestInclusiveExtendibles;
import org.spartan.fajita.revision.examples.TestJumps;
import org.spartan.fajita.revision.examples.TestNonTerminalMultipleParents;
import org.spartan.fajita.revision.examples.usage.datalog.Ancestor;
import org.spartan.fajita.revision.parser.rll.RLLPTest;

@SuppressWarnings("static-method") public class AllTest {
  @Test public void rllp() {
    RLLPTest t = new RLLPTest();
    t.anb1();
    t.anbn1();
    t.anbn2();
    t.anbn3();
    t.anbncd1();
    t.anbncd2();
    t.ancdbn();
    t.datalog();
    t.jumps();
    t.parenthesis1();
    t.parenthesis2();
  }
  @Test public void datalog() {
    new Datalog().test();
    Ancestor.program();
  }
  // TODO Roth: move all compilation tests to "usage" package
  @Test public void miscellaneous() {
    TestNonTerminalMultipleParents.testing();
    TestInclusiveExtendibles.testing();
    TestAnB.testing();
    TestAnBn.testing();
    TestJumps.testing();
    TestAnBnCD.testing();
    TestAnCDBn.testing();
    EBNF.main(null);
    Exp.main(null);
    Regex.main(null);
  }
}
