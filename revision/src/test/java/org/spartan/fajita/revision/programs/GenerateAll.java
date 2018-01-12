package org.spartan.fajita.revision.programs;

import java.io.IOException;

import org.spartan.fajita.revision.examples.Datalog;
import org.spartan.fajita.revision.examples.EBNF;
import org.spartan.fajita.revision.examples.Exp;
import org.spartan.fajita.revision.examples.Exp2;
import org.spartan.fajita.revision.examples.Parenthesis;
import org.spartan.fajita.revision.examples.ParenthesisSimple;
import org.spartan.fajita.revision.examples.Regex;
import org.spartan.fajita.revision.examples.TestAnB;
import org.spartan.fajita.revision.examples.TestAnBSimple;
import org.spartan.fajita.revision.examples.TestAnBn;
import org.spartan.fajita.revision.examples.TestAnBnCD;
import org.spartan.fajita.revision.examples.TestAnBnSimple;
import org.spartan.fajita.revision.examples.TestAnCDBn;
import org.spartan.fajita.revision.examples.TestFinite;
import org.spartan.fajita.revision.examples.TestInclusiveExtendibles;
import org.spartan.fajita.revision.examples.TestJumps;
import org.spartan.fajita.revision.examples.TestNonTerminalMultipleParents;
import org.spartan.fajita.revision.examples.TestTwoParentsBNF;

public class GenerateAll {
  public static void main(String[] args) throws IOException {
    new Datalog().generateGrammarFiles();
    new TestInclusiveExtendibles().generateGrammarFiles();
    new TestNonTerminalMultipleParents().generateGrammarFiles();
    new TestTwoParentsBNF().generateGrammarFiles();
    new TestAnBn().generateGrammarFiles();
    new TestAnBnSimple().generateGrammarFiles();
    new TestJumps().generateGrammarFiles();
    new TestAnB().generateGrammarFiles();
    new TestAnBSimple().generateGrammarFiles();
    new TestAnCDBn().generateGrammarFiles();
    new TestAnBnCD().generateGrammarFiles();
    new Parenthesis().generateGrammarFiles();
    new ParenthesisSimple().generateGrammarFiles();
    new TestFinite().generateGrammarFiles();
    new EBNF().generateGrammarFiles();
    new Exp().generateGrammarFiles();
    new Exp2().generateGrammarFiles();
    new Regex().generateGrammarFiles();
  }
}
