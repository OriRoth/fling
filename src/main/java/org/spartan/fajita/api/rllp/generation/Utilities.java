package org.spartan.fajita.api.rllp.generation;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.spartan.fajita.api.bnf.symbols.Verb;
import org.spartan.fajita.api.rllp.Item;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeVariableName;

public class Utilities {
  @SuppressWarnings("boxing") public static final String enclosingClass = "Container"
      + String.format("%x", Math.abs(Long.hashCode(System.currentTimeMillis())));
  public static final TypeVariableName errorClass = TypeVariableName.get("ERROR");

  public static TypeVariableName verbTypeName(final Verb verb) {
    return TypeVariableName.get(verb.name());
  }
  @SuppressWarnings("boxing") public static ClassName itemClassName(final Item i) {
    final String suffix = String.format("%x", Math.abs(i.rule.hashCode()));
    final String itemTypeName = i.rule.lhs + "_" + i.dotIndex + "_" + suffix;
    return ClassName.get("", itemTypeName);
  }
  public static <T> Mapper<T> map(Collection<T> toMap) {
    return new Mapper<>(toMap);
  }

  public static class Mapper<T> {
    private Collection<T> items;

    Mapper(Collection<T> items) {
      this.items = items;
    }
    public <S> Filter<T, S> with(Function<T, S> func) {
      return new Filter<>(items, func);
    }
  }

  public static class Filter<T, S> implements Iterable<S> {
    private final Function<T, S> func;
    private final Collection<T> items;

    public Filter(Collection<T> items, Function<T, S> func) {
      this.items = items;
      this.func = func;
    }
    public List<S> asList() {
      return items.stream().map(t -> func.apply(t)).collect(Collectors.toList());
    }
    @Override public Iterator<S> iterator() {
      return asList().iterator();
    }
    public Collection<S> filter(Predicate<T> pred) {
      items.removeIf(pred);
      return asList();
    }
  }
}
