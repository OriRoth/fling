package il.ac.technion.cs.fling.internal.grammar.rules;

import java.util.List;

public class SententialForm extends Word<Symbol> {
  public SententialForm(final Symbol... symbols) {
    super(symbols);
  }

  public SententialForm(final List<Symbol> symbols) {
    super(symbols);
  }
}
