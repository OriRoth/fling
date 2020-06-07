package il.ac.technion.cs.fling.internal.grammar.sententials;

import java.util.List;

import il.ac.technion.cs.fling.GeneralizedSymbol;

public class ExtendedSententialForm extends Word<GeneralizedSymbol> {
  public ExtendedSententialForm(final GeneralizedSymbol... symbols) {
    super(symbols);
  }
  public ExtendedSententialForm(final List<GeneralizedSymbol> symbols) {
    super(symbols);
  }
}
