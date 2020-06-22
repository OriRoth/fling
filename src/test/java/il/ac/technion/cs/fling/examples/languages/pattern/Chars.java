package il.ac.technion.cs.fling.examples.languages.pattern;
interface Pattern {
  /*
   * X? X, once or not at all X* X, zero or more times X+ X, one or more times
   * X{n} X, exactly n times X{n,} X, at least n times X{n,m} X, at least n but
   * not more than m times
   *
   */
  static Pattern maybe(@SuppressWarnings("unused") final Pattern p) {
    return null;
  }
  class x implements Pattern {
    static {
      new x();
    }
  }
}
public interface Chars {
  boolean includes(char c);
  default int size() {
    int $ = 0;
    for (int c = Character.MIN_VALUE; c <= Character.MAX_VALUE; ++c)
      if (includes((char) c))
        ++$;
    return $;
  }
  interface To {
    Chars to(char c);
  }
  static To from(final char from) {
    return to -> (Chars) c -> c >= from && c <= to;
  }
  default Chars not() {
    return c -> !includes(c);
  }
  default Chars or(final Chars other) {
    return c -> includes(c) || other.includes(c);
  }
  default Chars and(final Chars other) {
    return c -> includes(c) && other.includes(c);
  }
  default Chars xor(final Chars other) {
    return c -> includes(c) ^ other.includes(c);
  }
  default Chars except(final Chars other) {
    return c -> includes(c) && !other.includes(c);
  }
  default Chars or(final char singleton) {
    return c -> includes(c) || c == singleton;
  }
  /** No character */
  Chars EMPTY = c -> false;
  /** . Any character */
  Chars ANY = EMPTY.not();
  /** \d A digit: [0-9] aka \p{Digit} a decimal digit: [0-9] */
  Chars DIGIT = from('0').to('9');
  /** \D A non-digit: [^0-9] */
  Chars NON_DIGIT = DIGIT.not();
  /** \s//\p{Space} a space character: [ \t\n\x0B\f\r] */
  Chars SPACE = of(' ', '\t', '\n', '\u000B', '\f', '\r');
  /** \S A non-whitespace character: [^\s] */
  Chars NOT_SPACE = SPACE.not();
  /** \p{Lower} A lower-case alphabetic character: [a-z] */
  Chars LOWER = from('a').to('z');
  /** \p{Upper} An upper-case alphabetic character:[A-Z] */
  Chars UPPER = from('A').to('Z');
  /** \p{Alpha} An alphabetic character:[\p{Lower}\p{Upper}] */
  Chars ALPHA = LOWER.or(UPPER);
  /** \w A word character: [a-zA-Z_0-9] */
  Chars WORD = ALPHA.or(DIGIT).or('_');
  /** \W A non-word character: [^\w] */
  Chars NOT_WORD = WORD.not();
  /** \p{ASCII} All ASCII:[\x00-\x7F] */
  Chars ASCII = from('\u0000').to('\u007F');
  /** \p{Alnum} An alphanumeric character:[\p{Alpha}\p{Digit}] */
  Chars ALNUM = ALPHA.or(DIGIT);
  /** \p{Punct} Punctuation: One of !"#$%&'()*+,-./:;<=>?@[\]^_`{|}~ */
  Chars PUNCT = of("!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~");
  Chars PUNCT1 = of('!', '"', '#', '$', '%', '&', '\'', '(', ')', '*', '+', '-', '.', '/', ';', '<', '=', '>', '?', '@',
      '[', '\\', ']', '^', '_', '`', '{', '|', '}');
  /** \p{Graph} A visible character: [\p{Alnum}\p{Punct}] */
  Chars GRAPH = ALNUM.or(PUNCT);
  /** \p{Print} A printable character: [\p{Graph}\x20] */
  Chars PRINT = GRAPH.or(of('\u0020'));
  /** \p{Blank} A space or a tab: [ \t] */
  Chars BLANK = of(' ', '\t');
  /** \p{Cntrl} A control character: [\x00-\x1F\x7F] */
  Chars CNTRL = from('\u0000').to('\u001F').or('\u007F');
  /** \p{XDigit} A hexadecimal digit: [0-9a-fA-F] */
  Chars XDIGIT = DIGIT.or(from('a').to('f')).or(from('A').to('F'));
  static Chars of(final char... cs) {
    Chars $ = EMPTY;
    for (final char c : cs)
      $ = $.or(c);
    return $;
  }
  static Chars of(final String... ss) {
    Chars $ = EMPTY;
    for (final String s : ss)
      for (final char c : s.toCharArray())
        $ = $.or(c);
    return $;
  }
}
