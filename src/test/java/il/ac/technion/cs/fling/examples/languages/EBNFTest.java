package il.ac.technion.cs.fling.examples.languages;
import org.junit.jupiter.api.Test;
import il.ac.technion.cs.fling.FancyEBNF;
import il.ac.technion.cs.fling.adapters.JavaGenerator;
import il.ac.technion.cs.fling.grammars.LL1;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Model;
import il.ac.technion.cs.fling.internal.compiler.api.dom.PolynomialAPICompiler;
import il.ac.technion.cs.fling.namers.NaiveLinker;
class EBNFTest {
  @Test void test() {
    final il.ac.technion.cs.fling.EBNF bnf = new EBNF().BNF();
    final NaiveLinker namer = new NaiveLinker("il.ac.technion.cs.fling.examples.generated", "AnBn");
    final JavaGenerator j = new JavaGenerator(namer, "il.ac.technion.cs.fling.examples.generated", "AnBn");
    FancyEBNF from = FancyEBNF.from(bnf);
    final LL1 ll1 = new LL1(from, namer);
    final var dpda = ll1.buildAutomaton(ll1.bnf.reduce());
    Model m = new PolynomialAPICompiler(dpda).go();
    String s = j.go(m);
    System.out.println(s);
  }
}
