package org.spartan.fajita.revision.symbols;

import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import org.spartan.fajita.revision.bnf.DerivationRule;
import org.spartan.fajita.revision.export.RuntimeVerb;
import org.spartan.fajita.revision.symbols.extendibles.Extendible;
import org.spartan.fajita.revision.symbols.types.ClassType;
import org.spartan.fajita.revision.symbols.types.NestedType;
import org.spartan.fajita.revision.symbols.types.ParameterType;
import org.spartan.fajita.revision.symbols.types.VarArgs;

public class Verb implements Terminal, Comparable<Verb> {
  public final Terminal terminal;
  public final ParameterType[] type;

  public Verb(Terminal terminal, Object... parameterTypes) {
    this.terminal = terminal;
    for (int i = 0; i < parameterTypes.length; ++i) {
      Object o = parameterTypes[i];
      // TODO Roth: add parameters check
      if (i < parameterTypes.length - 1 && o instanceof VarArgs)
        throw new IllegalArgumentException("VarArgs can only be the last parameter of a Terminal");
    }
    List<Object> t = Arrays.stream(parameterTypes).map(x -> x instanceof Class<?> ? new ClassType((Class<?>) x)
        : x instanceof NonTerminal || x instanceof Extendible ? new NestedType((Symbol) x) : x).collect(toList());
    this.type = t.toArray(new ParameterType[t.size()]);
  }
  @Override public String toString() {
    return name() + Arrays.deepToString(type);
  }
  @Override public String name() {
    return terminal.name();
  }
  @Override public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (obj instanceof RuntimeVerb)
      return obj.equals(this);
    if (!(obj instanceof Verb))
      return false;
    Verb other = (Verb) obj;
    if (!terminal.equals(other.terminal) || type.length != other.type.length)
      return false;
    for (int i = 0; i < type.length; ++i)
      if (!type[i].equals(other.type[i]))
        return false;
    return true;
  }
  // TODO Roth: set proper hash with respect to RuntimeVerb
  @Override public int hashCode() {
    // final int prime = 19;
    // int result = 1;
    // result = prime * result + ((name == null) ? 0 : name.hashCode());
    // result = prime * result + ((type == null) ? 0 : type.hashCode());
    // return result;
    return 0;
  }
  public boolean accepts(Object[] args) {
    if (type.length == 0)
      return args.length == 0;
    ParameterType last = type[type.length - 1];
    if (!(last instanceof VarArgs)) {
      if (type.length != args.length)
        return false;
      for (int i = 0; i < type.length; ++i)
        if (!type[i].accepts(args[i]))
          return false;
      return true;
    }
    if (args.length < type.length - 1)
      return false;
    for (int i = 0; i < type.length - 1; ++i)
      if (!type[i].accepts(args[i]))
        return false;
    for (int i = type.length - 1; i < args.length - 1; ++i)
      if (!last.accepts(args[i]))
        return false;
    return true;
  }
  @Override public List<DerivationRule> solve(NonTerminal lhs, Function<NonTerminal, NonTerminal> producer) {
    List<DerivationRule> $ = new LinkedList<>();
    for (ParameterType t : type)
      if (t instanceof NestedType)
        $.addAll(((NestedType) t).nested.solve(lhs, producer));
    return $;
  }
  @Override public int compareTo(Verb v) {
    return equals(v) ? 0 : terminal.name().compareTo(v.name());
  }
}
