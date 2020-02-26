package fling.examples.usecases;

public interface Chars {
  boolean member(char c);

  interface To {
    Chars to(char c);
  }

  static To from(char from) {
    return new To() {
      @Override public Chars to(char to) {
        return new Chars() {
          @Override public boolean member(char c) {
            return c >= from && c <= to;
          }
        };
      }
    };
  }
  default Chars not() {
    return new Chars() {
      @Override public boolean member(char c) {
        return !Chars.this.member(c);
      }
    };
  }
  default Chars or(Chars other) {
    return new Chars() {
      @Override public boolean member(char c) {
        return Chars.this.member(c) || other.member(c);
      }
    };
  }
  default Chars and(Chars other) {
    return new Chars() {
      @Override public boolean member(char c) {
        return Chars.this.member(c) && other.member(c);
      }
    };
  }
  default Chars xor(Chars other) {
    return new Chars() {
      @Override public boolean member(char c) {
        return Chars.this.member(c) ^ other.member(c);
      }
    };
  }
  default Chars except(Chars other) {
    return new Chars() {
      @Override public boolean member(char c) {
        return Chars.this.member(c) && !other.member(c);
      }
    };
  }
  default Chars or(char singleton) {
    return new Chars() {
      @Override public boolean member(char c) {
        return Chars.this.member(c) || c == singleton;
      }
    };
  }

  /** No character */
  static Chars EMPTY = new Chars() {
    @Override public boolean member(char c) {
      return false;
    }
  };
  /** . Any character */
  static final Chars ANY = EMPTY.not();
  /** \d A digit: [0-9] aka \p{Digit} a decimal digit: [0-9] */
  static final Chars DIGIT = Chars.from('0').to('9');
  /** \D A non-digit: [^0-9] */
  static final Chars NON_DIGIT = DIGIT.not();
  /** \s//\p{Space} a space character: [ \t\n\x0B\f\r] */
  static final Chars SPACE = Chars.of(' ', '\t', '\n', '\u000B', '\f', '\r');
  /** \S A non-whitespace character: [^\s] */
  static final Chars NOT_SPACE = SPACE.not();
  /** \p{Lower} A lower-case alphabetic character: [a-z] */
  static final Chars LOWER = Chars.from('a').to('z');
  /** \p{Upper} An upper-case alphabetic character:[A-Z] */
  static final Chars UPPER = Chars.from('A').to('Z');
  /** \p{Alpha} An alphabetic character:[\p{Lower}\p{Upper}] */
  static final Chars ALPHA = LOWER.or(UPPER);
  /** \w A word character: [a-zA-Z_0-9] */
  static final Chars WORD = ALPHA.or(DIGIT).or('_');
  /** \W A non-word character: [^\w] */
  static final Chars NOT_WORD = WORD.not();
  /** \p{ASCII} All ASCII:[\x00-\x7F] */
  static final Chars ASCII = Chars.from('\u0000').to('\u007F');
  /** \p{Alnum} An alphanumeric character:[\p{Alpha}\p{Digit}] */
  static final Chars ALNUM = ALPHA.or(DIGIT);
  /** \p{Punct} Punctuation: One of !"#$%&'()*+,-./:;<=>?@[\]^_`{|}~ */
  static final Chars PUNCT = Chars.of("!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~");
  static final Chars PUNCT1 = Chars.of('!', '"', '#', '$', '%', '&', '\'', '(', ')', '*', '+', '-', '.', '/', ';', '<', '=', '>',
      '?', '@', '[', '\\', ']', '^', '_', '`', '{', '|', '}');
  /** \p{Graph} A visible character: [\p{Alnum}\p{Punct}] */
  static final Chars GRAPH = ALNUM.or(PUNCT);
  /** \p{Print} A printable character: [\p{Graph}\x20] */
  static final Chars PRINT = GRAPH.or(Chars.of('\u0020'));
  /** \p{Blank} A space or a tab: [ \t] */
  static final Chars BLANK = Chars.of(' ', '\t');
  /** \p{Cntrl} A control character: [\x00-\x1F\x7F] */
  static final Chars CNTRL = Chars.from('\u0000').to('\u001F').or('\u007F');
  /** \p{XDigit} A hexadecimal digit: [0-9a-fA-F] */
  static final Chars XDIGIT = DIGIT.or(Chars.from('a').to('f')).or(Chars.from('A').to('F'));

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
