package fling.internal.grammar.sententials;

import java.util.List;

public class SententialForm extends Word<Symbol> {
  public SententialForm(final Symbol... symbols) {
    super(symbols);
  }
  public SententialForm(final List<Symbol> symbols) {
    super(symbols);
  }
}
