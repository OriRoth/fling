package il.ac.technion.cs.fling.adapters;

import static java.util.stream.Collectors.joining;

import java.util.stream.Stream;

import il.ac.technion.cs.fling.internal.compiler.Namer;
import il.ac.technion.cs.fling.internal.compiler.api.dom.MethodParameter;

public abstract class CLikeGenerator extends APIGenerator {

  CLikeGenerator(Namer namer) {
    super(namer);
  }

  @Override final String comment(String text) {
    return String.format("/* %s */", text);
  }

  @Override final String render(Stream<MethodParameter> ps) {
    return ps.map(p -> p.type + " " + p.name).collect(joining(", "));
  }
}
