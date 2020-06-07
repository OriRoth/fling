package il.ac.technion.cs.fling.internal.grammar.sententials;

import java.util.List;

import il.ac.technion.cs.fling.Symbol;

public class ExtendedSententialForm extends Word<Symbol> {
  public ExtendedSententialForm(final Symbol... symbols) {
    super(symbols);
  }
  public ExtendedSententialForm(final List<Symbol> symbols) {
    super(symbols);
  }
}
