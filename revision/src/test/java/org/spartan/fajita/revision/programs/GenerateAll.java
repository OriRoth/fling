package org.spartan.fajita.revision.programs;

import java.io.IOException;

import org.spartan.fajita.revision.examples.Datalog;
import org.spartan.fajita.revision.examples.TestInclusiveExtendibles;
import org.spartan.fajita.revision.examples.TestNonTerminalMultipleParents;
import org.spartan.fajita.revision.examples.TestTwoParentsBNF;
import org.spartan.fajita.revision.examples.malfunction.TestAnB;
import org.spartan.fajita.revision.examples.malfunction.TestAnBn;
import org.spartan.fajita.revision.examples.malfunction.TestAnCDBn;
import org.spartan.fajita.revision.examples.malfunction.TestJumps;

public class GenerateAll {
  public static void main(String[] args) throws IOException {
    new Datalog().generateGrammarFiles();
    new TestInclusiveExtendibles().generateGrammarFiles();
    new TestNonTerminalMultipleParents().generateGrammarFiles();
    new TestTwoParentsBNF().generateGrammarFiles();
    new TestAnBn().generateGrammarFiles();
    new TestJumps().generateGrammarFiles();
    new TestAnB().generateGrammarFiles();
    new TestAnCDBn().generateGrammarFiles();
  }
}
