package il.ac.technion.cs.fling.examples.languages;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class TableMakerTest {

  @Test void test() {
    assertNotNull(new TableMaker().apiClass);
  }

}
