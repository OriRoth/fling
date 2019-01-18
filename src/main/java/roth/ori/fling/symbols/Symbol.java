package roth.ori.fling.symbols;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public interface Symbol extends GrammarElement {
  default String name(String packagePath) {
    return packagePath + "." + name();
  }
  default String name(String packagePath, String containingClass) {
    return packagePath + "." + containingClass + "." + name();
  }
  public default String toString2() {
    return "<" + name() + ">";
  }
  @SuppressWarnings("rawtypes") @Override default List<Class> toClasses(Function<GrammarElement, Class> classSolution) {
    return Collections.singletonList(classSolution.apply(this));
  }
  public static Symbol of(String name) {
    return new Symbol() {
      @Override public String name() {
        return name;
      }
      @Override public boolean equals(Object obj) {
        return obj instanceof Symbol && name.equals(((Symbol) obj).name());
      }
      @Override public int hashCode() {
        return name.hashCode();
      }
      @Override public String toString() {
        return name;
      }
    };
  }
  public default String iname(String packagePath, String containingClass) {
    return packagePath + "." + containingClass + ".I" + name();
  }
  @Override default Symbol head() {
    return this;
  }
}