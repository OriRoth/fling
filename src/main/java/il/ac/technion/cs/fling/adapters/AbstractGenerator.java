package il.ac.technion.cs.fling.adapters;

import il.ac.technion.cs.fling.internal.compiler.Namer;
import il.ac.technion.cs.fling.internal.compiler.api.APIGenerator;

abstract class AbstractGenerator implements APIGenerator {
  protected AbstractGenerator(final String terminationMethodName, final Namer namer) {
    this.namer = namer;
    this.terminationMethodName = terminationMethodName;
  }

  protected final Namer namer;
  protected final String terminationMethodName;

}