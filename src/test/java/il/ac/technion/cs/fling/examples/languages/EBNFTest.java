package il.ac.technion.cs.fling.examples.languages;
import org.junit.jupiter.api.Test;
import il.ac.technion.cs.fling.FancyEBNF;
import il.ac.technion.cs.fling.adapters.JavaGenerator;
import il.ac.technion.cs.fling.grammars.LL1;
import il.ac.technion.cs.fling.internal.compiler.api.dom.PolynomialAPICompiler;
import il.ac.technion.cs.fling.internal.compiler.api.dom.ReliableAPICompiler;
class EBNFTest {
  @Test void test() {
    final il.ac.technion.cs.fling.EBNF bnf = new EBNF().BNF();
    final JavaGenerator j = new JavaGenerator(getClass().getPackageName() + ".__", getClass().getSimpleName());
    final FancyEBNF from = FancyEBNF.from(bnf);
    final LL1 ll1 = new LL1(from, j.namer);
    final var dpda = ll1.buildAutomaton(ll1.bnf.reduce());
    System.out.println(j.go(new PolynomialAPICompiler(dpda).go()));
    System.out.println(j.go(new ReliableAPICompiler(dpda).go()));
  }
}
