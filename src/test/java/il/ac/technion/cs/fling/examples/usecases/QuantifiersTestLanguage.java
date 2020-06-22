package il.ac.technion.cs.fling.examples.usecases;
import static il.ac.technion.cs.fling.examples.generated.QuantifiersTestLanguage.a;
import il.ac.technion.cs.fling.examples.generated.QuantifiersTestLanguageAST.X;
import il.ac.technion.cs.fling.examples.generated.QuantifiersTestLanguageAST.Y1;
public class QuantifiersTestLanguage {
  public static void main(final String[] args) {
    final X x1 = a(1).$();
    assert x1.a.size() == 1;
    assert x1.a.get(0) == 1;
    assert x1.b.isEmpty();
    assert !x1.y.isPresent();
    assert !x1.e.isPresent();
    final X x2 = a(1).a(2).b(3).b(4).c(5).e(6).$();
    assert x2.a.size() == 2;
    assert x2.a.get(0) == 1;
    assert x2.a.get(1) == 2;
    assert x2.b.size() == 2;
    assert x2.b.get(0) == 3;
    assert x2.b.get(1) == 4;
    assert x2.y.isPresent();
    assert x2.y.get() instanceof Y1;
    assert ((Y1) x2.y.get()).c == 5;
    assert x2.e.isPresent();
    assert x2.e.get() == 6;
  }
}
