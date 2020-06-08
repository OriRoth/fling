package il.ac.technion.cs.fling.examples.languages.pattern;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

@SuppressWarnings("static-method") public class CharsTest {
  @Test public void test() {
    assertThat(Chars.EMPTY.size(), is(0));
  }

  @Test public void test1() {
    assertThat(Chars.of('a').size(), is(1));
  }

  @Test public void test2() {
    assertThat(Chars.from('a').to('z').size(), is(26));
  }

  @Test public void test3() {
    assertThat(Chars.ANY.size(), is(65536));
  }

  @Test public void test4() {
    assertThat(Chars.UPPER.size(), is(26));
  }

  @Test public void test5() {
    assertThat(Chars.DIGIT.size(), is(10));
  }

  @Test public void test6() {
    assertThat(Chars.XDIGIT.size(), is(22));
  }

  @Test public void test7() {
    assertThat(Chars.ASCII.size(), is(128));
  }
}
