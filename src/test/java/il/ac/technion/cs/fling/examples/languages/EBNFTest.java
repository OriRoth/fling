package il.ac.technion.cs.fling.examples.languages;
import org.junit.jupiter.api.Test;
import il.ac.technion.cs.fling.FancyEBNF;
import il.ac.technion.cs.fling.adapters.JavaGenerator;
import il.ac.technion.cs.fling.grammars.LL1;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Model;
import il.ac.technion.cs.fling.namers.NaiveLinker;
class EBNFTest {
  @Test void test() {
    final il.ac.technion.cs.fling.EBNF bnf = new EBNF().BNF();
    final NaiveLinker namer = new NaiveLinker("il.ac.technion.cs.fling.examples.generated", "AnBn");
    final JavaGenerator j = new JavaGenerator(namer, "il.ac.technion.cs.fling.examples.generated", "AnBn");
    final LL1 ll1 = new LL1(FancyEBNF.from(bnf), namer);
    final auto dpda = ll1.buildAutomaton();
    Model m;
    j.go(m);
  }
}
