package org.spartan.fajita.revision.examples.usage;

import static org.spartan.fajita.revision.junk.RE.exactly;
import static org.spartan.fajita.revision.junk.Regex.re;
import static org.spartan.fajita.revision.junk.RE.anyDigit;

public class Regex {
  public static void main(String[] args) {
    re().noneOrMore(exactly("a").and().option(exactly("b"))).or().oneOrMore(anyDigit()).$();
  }
}
