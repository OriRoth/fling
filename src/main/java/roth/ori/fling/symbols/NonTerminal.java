package roth.ori.fling.symbols;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public interface NonTerminal extends GrammarElement {
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
  public static NonTerminal of(String name) {
    return new NonTerminal() {
      @Override public String name() {
        return name;
      }
      @Override public boolean equals(Object obj) {
        return obj instanceof NonTerminal && name.equals(((NonTerminal) obj).name());
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
  @Override default NonTerminal head() {
    return this;
  }
}