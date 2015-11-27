package org.spartan.fajita.api.bnf.rules;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.SpecialSymbols;
import org.spartan.fajita.api.bnf.symbols.Symbol;
import org.spartan.fajita.api.bnf.symbols.Terminal;

public class DerivationRule implements Comparable<DerivationRule>, Serializable {
  private static final long serialVersionUID = -1509740550195808093L;
  public final NonTerminal lhs;
  private transient final int index;
  private final List<Symbol> expression;

  public DerivationRule(final NonTerminal lhs, final List<Symbol> expression, final int index) {
    this.lhs = lhs;
    this.index = index;
    this.expression = new ArrayList<>(expression.stream().filter(s -> s != SpecialSymbols.epsilon).collect(Collectors.toList()));
  }
  @Override public String toString() {
    StringBuilder sb = new StringBuilder(lhs.methodSignatureString() + " ::= ");
    for (Symbol symb : expression)
      sb.append(symb.methodSignatureString() + " ");
    return sb.toString();
  }
  public List<Symbol> getChildren() {
    return new ArrayList<>(expression);
  }
  @Override public boolean equals(final Object obj) {
    return obj.getClass() == getClass() && lhs.equals(((DerivationRule) obj).lhs)
        && getChildren().equals(((DerivationRule) obj).getChildren());
  }
  @Override public int hashCode() {
    return lhs.hashCode() + 7 * getChildren().hashCode();
  }
  public int getIndex() {
    return index;
  }
  @Override public int compareTo(final DerivationRule other) {
    return Integer.compare(index, other.index);
  }
  public String serialize() {
    StringBuilder $ = new StringBuilder();
    $.append(index).append(',').append(lhs.serialize());
    for(Symbol s:getChildren())
      $.append(","+s.serialize());
    return $.toString();
  }
  public static DerivationRule deserialize(String desc){
    String[] data = desc.split(",");
    int index = Integer.parseInt(data[0]);
    NonTerminal lhs = NonTerminal.deserialize(data[1]);
    List<Symbol> rhs = new ArrayList<>();
    for (int i=2;i<data.length;i++)
      switch(data[i].charAt(0)){
        case '^': rhs.add(Symbol.deserialize(data[i]));
          break;
        case '%': rhs.add(Terminal.deserialize(data[i]));
          break;
        case '<': rhs.add(NonTerminal.deserialize(data[i]));
          break;
        default:
          throw new IllegalArgumentException("serialized object: `"+desc+"` is illegal");
      }
    return new DerivationRule(lhs, rhs, index);
  }
}