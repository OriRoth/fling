package il.ac.technion.cs.fling.examples.languages;
import org.junit.jupiter.api.Test;
import il.ac.technion.cs.fling.FancyEBNF;
import il.ac.technion.cs.fling.adapters.JavaGenerator;
import il.ac.technion.cs.fling.grammars.LL1;
import il.ac.technion.cs.fling.internal.compiler.api.dom.PolynomialAPICompiler;
import il.ac.technion.cs.fling.internal.compiler.api.dom.ReliableAPICompiler;
import il.ac.technion.cs.fling.internal.grammar.Grammar;
class EBNFTest {
  @Test void test() {
    final il.ac.technion.cs.fling.EBNF bnf = new TAPI().BNF();
    final JavaGenerator j = new JavaGenerator(getClass().getPackageName() + ".__", getClass().getSimpleName());
    final FancyEBNF from = FancyEBNF.from(bnf);
    final Grammar g = new Grammar(from);
    final var dpda = LL1.buildAutomaton(g.bnf.clean());
    System.out.println(j.go(new PolynomialAPICompiler(dpda).go()));
    System.out.println(j.go(new ReliableAPICompiler(dpda).go()));
  }
}
