package org.spartan.fajita.api.rllp.generation;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.spartan.fajita.api.bnf.symbols.Verb;
import org.spartan.fajita.api.rllp.Item;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeVariableName;

public class GeneratorStrings {
   public static final String enclosingClass = "Container";
   public static final String errorClass = "ERROR";

  public static TypeVariableName typeArg(final Verb verb) {
    String name = verb.name();
    return TypeVariableName.get(name);
  }
  public static String itemTypeName(final Item i) {
    return i.rule.lhs + "_" + i.dotIndex + "_" + Math.abs(i.rule.hashCode());
  }
  public static ClassName itemClass(final Item i){
    return ClassName.get("",itemTypeName(i));
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
