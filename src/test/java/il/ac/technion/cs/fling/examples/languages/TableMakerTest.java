package il.ac.technion.cs.fling.examples.languages;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
@SuppressWarnings("static-method") class TableMakerTest {
  @Test public void test() {
    assertNotNull(new TableMaker().apiClass);
  }
}
