package il.ac.technion.cs.fling.examples.languages.pattern;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

@SuppressWarnings({ "static-method" }) public class CharsTest {
  @Test public void test() {
    assertThat(Chars.EMPTY.size()).isEqualTo(0);
  }

  @Test public void test1() {
    assertThat(Chars.of('a').size()).isEqualTo(1);
  }

  @Test public void test2() {
    assertThat(Chars.from('a').to('z').size()).isEqualTo(26);
  }

  @Test public void test3() {
    // TODO Auto-generated method stub
    assertThat(Chars.ANY.size()).isEqualTo(65536);
  }

  @Test public void test4() {
    assertThat(Chars.UPPER.size()).isEqualTo(26);
  }

  @Test public void test5() {
    assertThat(Chars.DIGIT.size()).isEqualTo(10);
  }

  @Test public void test6() {
    assertThat(Chars.XDIGIT.size()).isEqualTo(22);
  }

  @Test public void test7() {
    assertThat(Chars.ASCII.size()).isEqualTo(128);
  }
}
