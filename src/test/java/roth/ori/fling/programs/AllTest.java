package roth.ori.fling.programs;

import org.junit.Test;
import roth.ori.fling.examples.Datalog;
import roth.ori.fling.examples.usage.EBNF;
import roth.ori.fling.examples.usage.Exp;
import roth.ori.fling.examples.usage.Regex;
import roth.ori.fling.examples.TestAnB;
import roth.ori.fling.examples.TestAnBn;
import roth.ori.fling.examples.TestAnBnCD;
import roth.ori.fling.examples.TestAnCDBn;
import roth.ori.fling.examples.TestInclusiveExtendibles;
import roth.ori.fling.examples.TestJumps;
import roth.ori.fling.examples.TestNonTerminalMultipleParents;
import roth.ori.fling.examples.usage.datalog.Ancestor;
import roth.ori.fling.parser.rll.RLLPTest;

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
