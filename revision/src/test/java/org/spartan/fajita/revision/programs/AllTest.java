package org.spartan.fajita.revision.programs;

import org.junit.Test;
import org.spartan.fajita.revision.examples.Datalog;
import org.spartan.fajita.revision.examples.TestInclusiveExtendibles;
import org.spartan.fajita.revision.examples.usage.Ancestor;
import org.spartan.fajita.revision.examples.TestNonTerminalMultipleParents;
import org.spartan.fajita.revision.examples.malfunction.TestAnB;
import org.spartan.fajita.revision.examples.malfunction.TestAnBn;

@SuppressWarnings("static-method") public class AllTest {
  @Test public void datalog() {
    new Datalog().test();
    Ancestor.program();
  }
  @Test public void miscellaneous() {
    TestNonTerminalMultipleParents.testing();
    TestInclusiveExtendibles.testing();
    TestAnB.testing();
    TestAnBn.testing();
  }
}
