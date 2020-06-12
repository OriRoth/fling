package il.ac.technion.cs.fling.examples.languages.pattern;

interface Pattern {
  /*
   * X? X, once or not at all X* X, zero or more times X+ X, one or more times
   * X{n} X, exactly n times X{n,} X, at least n times X{n,m} X, at least n but
   * not more than m times
   * 
   */
  static Pattern maybe(@SuppressWarnings("unused") Pattern p) {
    return null;
  }

  class x implements Pattern {
    static {
      @SuppressWarnings("unused") x x = new x();
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

  static To from(char from) {
    return to -> (Chars) c -> c >= from && c <= to;
  }

  default Chars not() {
    return c -> !Chars.this.includes(c);
  }

  default Chars or(Chars other) {
    return c -> Chars.this.includes(c) || other.includes(c);
  }

  default Chars and(Chars other) {
    return c -> Chars.this.includes(c) && other.includes(c);
  }

  default Chars xor(Chars other) {
    return c -> Chars.this.includes(c) ^ other.includes(c);
  }

  default Chars except(Chars other) {
    return c -> Chars.this.includes(c) && !other.includes(c);
  }

  default Chars or(char singleton) {
    return c -> Chars.this.includes(c) || c == singleton;
  }

  /** No character */
  Chars EMPTY = c -> false;
  /** . Any character */
  Chars ANY = EMPTY.not();
  /** \d A digit: [0-9] aka \p{Digit} a decimal digit: [0-9] */
  Chars DIGIT = Chars.from('0').to('9');
  /** \D A non-digit: [^0-9] */
  Chars NON_DIGIT = DIGIT.not();
  /** \s//\p{Space} a space character: [ \t\n\x0B\f\r] */
  Chars SPACE = Chars.of(' ', '\t', '\n', '\u000B', '\f', '\r');
  /** \S A non-whitespace character: [^\s] */
  Chars NOT_SPACE = SPACE.not();
  /** \p{Lower} A lower-case alphabetic character: [a-z] */
  Chars LOWER = Chars.from('a').to('z');
  /** \p{Upper} An upper-case alphabetic character:[A-Z] */
  Chars UPPER = Chars.from('A').to('Z');
  /** \p{Alpha} An alphabetic character:[\p{Lower}\p{Upper}] */
  Chars ALPHA = LOWER.or(UPPER);
  /** \w A word character: [a-zA-Z_0-9] */
  Chars WORD = ALPHA.or(DIGIT).or('_');
  /** \W A non-word character: [^\w] */
  Chars NOT_WORD = WORD.not();
  /** \p{ASCII} All ASCII:[\x00-\x7F] */
  Chars ASCII = Chars.from('\u0000').to('\u007F');
  /** \p{Alnum} An alphanumeric character:[\p{Alpha}\p{Digit}] */
  Chars ALNUM = ALPHA.or(DIGIT);
  /** \p{Punct} Punctuation: One of !"#$%&'()*+,-./:;<=>?@[\]^_`{|}~ */
  Chars PUNCT = Chars.of("!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~");
  Chars PUNCT1 = Chars.of('!', '"', '#', '$', '%', '&', '\'', '(', ')', '*', '+', '-', '.', '/', ';', '<',
      '=', '>', '?', '@', '[', '\\', ']', '^', '_', '`', '{', '|', '}');
  /** \p{Graph} A visible character: [\p{Alnum}\p{Punct}] */
  Chars GRAPH = ALNUM.or(PUNCT);
  /** \p{Print} A printable character: [\p{Graph}\x20] */
  Chars PRINT = GRAPH.or(Chars.of('\u0020'));
  /** \p{Blank} A space or a tab: [ \t] */
  Chars BLANK = Chars.of(' ', '\t');
  /** \p{Cntrl} A control character: [\x00-\x1F\x7F] */
  Chars CNTRL = Chars.from('\u0000').to('\u001F').or('\u007F');
  /** \p{XDigit} A hexadecimal digit: [0-9a-fA-F] */
  Chars XDIGIT = DIGIT.or(Chars.from('a').to('f')).or(Chars.from('A').to('F'));

  static Chars of(char... cs) {
    Chars $ = Chars.EMPTY;
    for (char c : cs)
      $ = $.or(c);
    return $;
  }

  static Chars of(String... ss) {
    Chars $ = Chars.EMPTY;
    for (String s : ss)
      for (char c : s.toCharArray())
        $ = $.or(c);
    return $;
  }
}
