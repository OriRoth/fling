package org.spartan.fajita.revision.export;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.spartan.fajita.revision.junk.DatalogAST.Program;

@SuppressWarnings({ "unused", "static-method" }) public class ASTVisitor {
  private final Map<Class<?>, Method> preVisit = new HashMap<>();
  private final Map<Class<?>, Method> visit = new HashMap<>();
  private final Map<Class<?>, Method> postVisit = new HashMap<>();
  private Object currentParent;

  // NOTE may be extended to include several AST classes
  public ASTVisitor(Class<? extends AST> astTopClass) {
    for (Class<?> i : astTopClass.getClasses()) {
      preVisit.put(i, getPreVisitMethod(this, i));
      visit.put(i, getVisitMethod(this, i));
      postVisit.put(i, getPostVisitMethod(this, i));
    }
  }
  public void startVisit(Object node) {
    _visit(node);
  }
  public boolean preVisit(Object node) {
    return true;
  }
  private void postVisit(Object node) {
    //
  }
  private boolean visit(Object node) {
    return true;
  }
  private final Object parent() {
    return currentParent;
  }
  private void _visit(Object o) {
    if (o == null)
      return;
    Class<?> oc = o.getClass();
//    if (!preVisit(o))
//      return;
//    for (Field f : oc.getFields())
//      _visit(o, f);
  }
  private void _visit(Object o, Field f) {
    // TODO Auto-generated method stub
  }
  private static Method getPreVisitMethod(ASTVisitor v, Class<?> i) {
    return getMethod(v, i, "preVisit");
  }
  private static Method getVisitMethod(ASTVisitor v, Class<?> i) {
    return getMethod(v, i, "visit");
  }
  private static Method getPostVisitMethod(ASTVisitor v, Class<?> i) {
    return getMethod(v, i, "postVisit");
  }
  private static Method getMethod(ASTVisitor v, Class<?> i, String name) {
    try {
      System.out.println(v.getClass().getDeclaredMethod(name, i));
      return v.getClass().getDeclaredMethod(name, i);
    } catch (NoSuchMethodException | SecurityException e) {
      try {
        return ASTVisitor.class.getMethod(name, Object.class);
      } catch (NoSuchMethodException | SecurityException e1) {
        assert false : "Should not reach";
        return null;
      }
    }
  }
}
