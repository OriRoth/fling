package il.ac.technion.cs.fling.internal.grammar.rules;

public interface Quantifiers {

  static Opt optional(final TempComponent s) {
    return new Opt((Symbol) s.normalize());
  }

  static NoneOrMore noneOrMore(final TempComponent s) {
    return new NoneOrMore((Symbol) s.normalize());
  }

  static OneOrMore oneOrMore(final TempComponent s) {
    return new OneOrMore((Symbol) s.normalize());
  }

}
