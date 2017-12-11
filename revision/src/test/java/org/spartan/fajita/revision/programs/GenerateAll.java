package org.spartan.fajita.revision.programs;

import java.io.IOException;

import org.spartan.fajita.revision.examples.Datalog;
import org.spartan.fajita.revision.examples.TestInclusiveExtendibles;
import org.spartan.fajita.revision.examples.TestNonTerminalMultipleParents;

public class GenerateAll {
  public static void main(String[] args) throws IOException {
    new Datalog().generateGrammarFiles();
    new TestInclusiveExtendibles().generateGrammarFiles();
    new TestNonTerminalMultipleParents().generateGrammarFiles();
  }
}
