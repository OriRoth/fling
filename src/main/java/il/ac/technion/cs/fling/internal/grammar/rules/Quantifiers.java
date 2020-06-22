package il.ac.technion.cs.fling.internal.grammar.rules;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
public interface Quantifiers {
  static Opt optional(final TempComponent s, final TempComponent... ss) {
    return new Opt(normalize(s, ss));
  }
  static NoneOrMore noneOrMore(final TempComponent s, final TempComponent... ss) {
    return new NoneOrMore(normalize(s, ss));
  }
  static OneOrMore oneOrMore(final TempComponent s, final TempComponent... ss) {
    return new OneOrMore(normalize(s, ss));
  }
  static List<Symbol> normalize(final TempComponent s, final TempComponent... ss) {
    final List<Symbol> $ = new ArrayList<>();
    $.add((Symbol) s.normalize());
    Arrays.stream(ss).map(TempComponent::normalize).map(Symbol.class::cast) //
        .forEach($::add);
    return $;
  }
}
