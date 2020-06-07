package il.ac.technion.cs.fling.internal.grammar.sententials;

import java.util.List;

import il.ac.technion.cs.fling.SymbolX;

public class SententialForm extends Word<SymbolX> {
  public SententialForm(final SymbolX... symbols) {
    super(symbols);
  }
  public SententialForm(final List<SymbolX> symbols) {
    super(symbols);
  }
}
