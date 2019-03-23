package fling.compiler.ast.nodes;

import fling.grammar.sententials.Variable;

public abstract class ClassNode {
  public final Variable source;
  private String className;
  
  public ClassNode(Variable source) {
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
  public void setClassName(String className) {
    this.className = className;
  }
}
