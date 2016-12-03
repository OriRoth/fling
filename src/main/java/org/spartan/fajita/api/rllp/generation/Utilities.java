package org.spartan.fajita.api.rllp.generation;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.spartan.fajita.api.bnf.symbols.Verb;
import org.spartan.fajita.api.rllp.Item;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeVariableName;

public class Utilities {
  @SuppressWarnings("boxing") public static final String enclosingClass = "Container"
      + String.format("%x", Math.abs(Long.hashCode(System.currentTimeMillis())));
  public static final String errorClass = "ERROR";

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
    private Collection<T> arg;

    Mapper(Collection<T> arg) {
      this.arg = arg;
    }
    public <S> Collection<S> with(Function<T, S> func) {
      return arg.stream().map(t -> func.apply(t)).collect(Collectors.toList());
    }
  }
}
