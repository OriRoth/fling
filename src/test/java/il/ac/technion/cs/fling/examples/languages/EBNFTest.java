package il.ac.technion.cs.fling.examples.languages;
import org.junit.jupiter.api.Test;
import il.ac.technion.cs.fling.FancyEBNF;
import il.ac.technion.cs.fling.adapters.JavaGenerator;
import il.ac.technion.cs.fling.grammars.LL1;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Model;
import il.ac.technion.cs.fling.namers.NaiveNamer;
class EBNFTest {
  @Test void test() {
    il.ac.technion.cs.fling.EBNF bnf = new EBNF().BNF();
    NaiveNamer namer = new NaiveNamer("il.ac.technion.cs.fling.examples.generated", "AnBn");
    JavaGenerator j = new JavaGenerator(namer, "il.ac.technion.cs.fling.examples.generated", "AnBn");
    LL1 ll1 = new LL1(FancyEBNF.from(bnf), namer);
    auto dpda = ll1.buildAutomaton();
    Model m;
    j.go(m);
  }
}
