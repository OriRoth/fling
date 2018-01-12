package org.spartan.fajita.revision.export;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public abstract class ASTVisitor {
  private final Map<Class<?>, Method> preVisit = new HashMap<>();
  private final Map<Class<?>, Method> visit = new HashMap<>();
  private final Map<Class<?>, Method> postVisit = new HashMap<>();

  // NOTE may be extended to include several AST classes
  public ASTVisitor(Class<? extends AST> astTopClass) {
    for (Class<?> i : astTopClass.getClasses()) {
      Method m = getPreVisitMethod(this, i);
      if (m != null) {
        m.setAccessible(true);
        preVisit.put(i, m);
      }
      m = getVisitMethod(this, i);
      if (m != null) {
        m.setAccessible(true);
        visit.put(i, m);
      }
      m = getPostVisitMethod(this, i);
      if (m != null) {
        m.setAccessible(true);
        postVisit.put(i, m);
      }
    }
  }
  public void startVisit(Object node) {
    _visit(node);
  }
  private void _visit(Object o) {
    if (o == null)
      return;
    Class<?> oc = o.getClass();
    if (oc.isArray()) {
      _visitArray(o);
      return;
    }
    if (Either.class.equals(oc)) {
      _visitEither((Either) o);
      return;
    }
    if (!_doPreVisit(o, oc))
      return;
    if (!_doVisit(o, oc))
      return;
    _visitChildren(o, oc);
    _doPostVisit(o, oc);
  }
  private void _visitChildren(Object o, Class<?> oc) {
    for (Field f : oc.getFields())
      try {
        _visit(f.get(o));
      } catch (@SuppressWarnings("unused") IllegalArgumentException | IllegalAccessException e) {
        assert false : "Should not reach"; // TODO Roth: not true
      }
  }
  private boolean _doPreVisit(Object o, Class<?> oc) {
    return _do(o, oc, preVisit);
  }
  private boolean _doVisit(Object o, Class<?> oc) {
    return _do(o, oc, visit);
  }
  private void _doPostVisit(Object o, Class<?> oc) {
    _do(o, oc, postVisit);
  }
  @SuppressWarnings("boxing") private boolean _do(Object o, Class<?> oc, Map<Class<?>, Method> visitors) {
    if (!visitors.containsKey(oc))
      return true;
    Object $ = null;
    try {
      $ = visitors.get(oc).invoke(this, o);
    } catch (IllegalAccessException | IllegalArgumentException e) {
      e.printStackTrace();
      assert false : "Should not happen"; // TODO Roth: not true
      return false;
    } catch (InvocationTargetException e) {
      throw new RuntimeException(e.getTargetException());
    }
    if ($ instanceof Boolean)
      return ((Boolean) $).booleanValue();
    if (boolean.class.isInstance($))
      return (boolean) $;
    return true;
  }
  private void _visitArray(Object o) {
    int length = Array.getLength(o);
    for (int i = 0; i < length; ++i)
      _visit(Array.get(o, i));
  }
  private void _visitEither(Either e) {
    _visit(e.get(e.type()));
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
      return v.getClass().getDeclaredMethod(name, i);
    } catch (@SuppressWarnings("unused") NoSuchMethodException | SecurityException e) {
      return null;
    }
  }
}
