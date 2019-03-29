package fling.compiler;

import java.util.Arrays;
import java.util.List;

import fling.grammar.sententials.Terminal;

public class Assignment {
  public final Terminal σ;
  public final List<Object> arguments;

  public Assignment(Terminal σ, Object... arguments) {
    this.σ = σ;
    this.arguments = Arrays.asList(arguments);
  }
}
