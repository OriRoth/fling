package roth.ori.fling.symbols;

import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import roth.ori.fling.bnf.DerivationRule;
import roth.ori.fling.export.RuntimeVerb;
import roth.ori.fling.symbols.types.ClassType;
import roth.ori.fling.symbols.types.NestedType;
import roth.ori.fling.symbols.types.ParameterType;
import roth.ori.fling.symbols.types.VarArgs;

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
      if (o instanceof Terminal)
        throw new IllegalArgumentException("Nested terminals are not yet supported");
    }
    List<Object> t = Arrays.stream(parameterTypes)
        .map(x -> x instanceof Class<?> ? new ClassType((Class<?>) x) : x instanceof GrammarElement ? new NestedType((GrammarElement) x) : x)
        .collect(toList());
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
  @Override public Verb head() {
    Object[] $ = new ParameterType[type.length];
    for (int i = 0; i < type.length; ++i)
      if (type[i] instanceof NestedType)
        $[i] = new NestedType(((NestedType) type[i]).nested.head());
      else
        $[i] = type[i];
    return new Verb(terminal, $);
  }
  @Override public int compareTo(Verb v) {
    return equals(v) ? 0 : terminal.name().compareTo(v.name());
  }
  @SuppressWarnings("rawtypes") @Override public List<Class> toClasses(Function<GrammarElement, Class> classSolution) {
    List<Class> $ = new LinkedList<>();
    for (ParameterType t : type)
      $.addAll(t.toClasses(classSolution));
    return $;
  }
  @SuppressWarnings({ "unchecked", "rawtypes" }) public List conclude(List args, BiFunction<GrammarElement, List, List> solution,
      String astPath) {
    // NOTE this assertion might need Verb to deal with Interpretation (?)
    // assert accepts(args.toArray(new Object[args.size()]));
    List $ = new LinkedList<>();
    if (type.length == 0)
      return $;
    ParameterType last = type[type.length - 1];
    if (!(last instanceof VarArgs)) {
      for (int i = 0; i < type.length; ++i)
        $.addAll(type[i].conclude(args.get(i), solution, astPath));
      return $;
    }
    for (int i = 0; i < type.length - 1; ++i)
      $.addAll(type[i].conclude(args.get(i), solution, astPath));
    $.addAll(last.conclude(args.subList(type.length - 1, args.size()), solution, astPath));
    return $;
  }
}
