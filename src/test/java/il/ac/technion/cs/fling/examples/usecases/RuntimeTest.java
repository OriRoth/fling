package il.ac.technion.cs.fling.examples.usecases;
import org.junit.Test;
public class RuntimeTest {
  @SuppressWarnings("static-method") @Test public void testAll() {
    Datalog.main(null);
    HTMLTable.main(null);
    SimpleArithmeticUseCase.main(null);
    SubFigure.main(null);
    TableMaker.main(null);
    TaggedBalancedParentheses.main(null);
    QuantifiersTestLanguage.main(null);
  }
}
