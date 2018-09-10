package roth.ori.fling.programs;

import java.io.IOException;

import roth.ori.fling.examples.Datalog;
import roth.ori.fling.examples.EBNF;
import roth.ori.fling.examples.Exp;
import roth.ori.fling.examples.Exp2;
import roth.ori.fling.examples.FajitaTesting;
import roth.ori.fling.examples.Parenthesis;
import roth.ori.fling.examples.ParenthesisSimple;
import roth.ori.fling.examples.Regex;
import roth.ori.fling.examples.TestAnB;
import roth.ori.fling.examples.TestAnBSimple;
import roth.ori.fling.examples.TestAnBn;
import roth.ori.fling.examples.TestAnBnCD;
import roth.ori.fling.examples.TestAnBnSimple;
import roth.ori.fling.examples.TestAnCDBn;
import roth.ori.fling.examples.TestFinite;
import roth.ori.fling.examples.TestInclusiveExtendibles;
import roth.ori.fling.examples.TestJumps;
import roth.ori.fling.examples.TestNonTerminalMultipleParents;
import roth.ori.fling.examples.TestTwoParentsBNF;

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
    new FajitaTesting().generateGrammarFiles();
  }
}
