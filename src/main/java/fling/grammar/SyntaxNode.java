package fling.grammar;

import java.util.ArrayList;
import java.util.List;

import fling.grammar.sententials.Symbol;

public class SyntaxNode {
  public final Symbol symbol;
  public final List<Object> elements;

  public SyntaxNode(Symbol symbol) {
    this.symbol = symbol;
    this.elements = new ArrayList<>();
  }
  public static SyntaxNode of(Symbol symbol) {
    return new SyntaxNode(symbol);
  }
  public void addElement(Object element) {
    elements.add(element);
  }
}
