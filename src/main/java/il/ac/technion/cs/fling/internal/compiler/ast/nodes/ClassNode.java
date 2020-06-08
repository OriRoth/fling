package il.ac.technion.cs.fling.internal.compiler.ast.nodes;

import il.ac.technion.cs.fling.internal.grammar.rules.Variable;

public abstract class ClassNode {
  public final Variable source;
  private String className;

  public ClassNode(final Variable source) {
    this.source = source;
  }

  public boolean isConcrete() {
    return this instanceof ConcreteClassNode;
  }

  public boolean isAbstract() {
    return this instanceof AbstractClassNode;
  }

  public ConcreteClassNode asConcrete() {
    return (ConcreteClassNode) this;
  }

  public AbstractClassNode asAbstract() {
    return (AbstractClassNode) this;
  }

  public String getClassName() {
    if (className == null)
      throw new IllegalStateException("class name not decided");
    return className;
  }

  public void setClassName(final String className) {
    this.className = className;
  }
}
