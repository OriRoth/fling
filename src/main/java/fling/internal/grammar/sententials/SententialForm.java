package fling.internal.grammar.sententials;

import java.util.List;

public class SententialForm extends Word<Symbol> {
  public SententialForm(Symbol... symbols) {
    super(symbols);
  }
  public SententialForm(List<Symbol> symbols) {
    super(symbols);
  }
}
