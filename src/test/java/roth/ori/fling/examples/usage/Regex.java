package roth.ori.fling.examples.usage;

import static roth.ori.fling.junk.RE.exactly;
import static roth.ori.fling.junk.Regex.re;
import static roth.ori.fling.junk.RE.anyDigit;

public class Regex {
  public static void main(String[] args) {
    re().noneOrMore(exactly("a").and().option(exactly("b"))).or().oneOrMore(anyDigit()).$();
  }
}
