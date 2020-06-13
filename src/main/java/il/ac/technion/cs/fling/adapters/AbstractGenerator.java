package il.ac.technion.cs.fling.adapters;

import il.ac.technion.cs.fling.internal.compiler.Namer;
import il.ac.technion.cs.fling.internal.compiler.api.PolymorphicLanguageAPIBaseAdapter;

abstract class AbstractGenerator implements PolymorphicLanguageAPIBaseAdapter {
  protected AbstractGenerator(final String terminationMethodName, final Namer namer) {
    this.namer = namer;
    this.terminationMethodName = terminationMethodName;
  }

  protected final Namer namer;
  protected final String terminationMethodName;

}