package org.spartan.fajita.api.bnf.symbols.type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClassesType implements ParameterType {
  public final List<Class<?>> classes;

  public ClassesType(final Class<?>... classes) {
    if (classes.length == 0)
      this.classes = an.empty.list();
    else
      this.classes = new ArrayList<>(Arrays.asList(classes));
  }
  @Override public String toString() {
    StringBuilder sb = new StringBuilder();
    if (classes.size() != 0) {
      classes.forEach(clss -> sb.append(clss.getName()).append(" * "));
      sb.delete(sb.length() - 3, sb.length());
    }
    return sb.toString();
  }
  @Override public int hashCode() {
    final int prime = 17;
    int result = 1;
    result = prime * result + ((classes == null) ? 0 : classes.hashCode());
    return result;
  }
  @Override public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (!(obj instanceof ClassesType))
      return false;
    ClassesType other = (ClassesType) obj;
    if (classes == null) {
      if (other.classes != null)
        return false;
    } else if (!classes.equals(other.classes))
      return false;
    return true;
  }
  @Override public boolean isEmpty() {
    return this == VOID || classes.size() == 1 && classes.get(0).equals(Void.class);
  }

  public static final ClassesType VOID = new ClassesType();
  // public String serialize() {
  // try (ByteArrayOutputStream o = new ByteArrayOutputStream() //
  // ; ObjectOutputStream oos = new ObjectOutputStream(o)) {
  // oos.writeObject(classes);
  // byte[] byteArray = o.toByteArray();
  // String $ = "";
  // for (byte b : byteArray)
  // $ += (String.format("%d:", new Byte(b)));
  // return $.substring(0, $.length() - 1);
  // } catch (IOException e) {
  // throw new RuntimeException("serialization of type failed.", e);
  // }
  // }
  // @SuppressWarnings("unchecked") public static Type deserialize(String $) {
  // String split[] = $.split(":");
  // byte[] byteArray = new byte[split.length];
  // int i = 0; for (String strByte : split) byteArray[i++] =
  // Byte.decode(strByte).byteValue();
  // try (ByteArrayInputStream o = new ByteArrayInputStream(byteArray) //
  // ; ObjectInputStream oos = new ObjectInputStream(o)) {
  // List<Class<?>> l = (List<Class<?>>) oos.readObject();
  // return new Type(l.toArray(new Class<?>[]{}));
  // } catch (IOException | ClassNotFoundException e) {
  // throw new RuntimeException("deserialization of type failed",e);
  // }
  // }
}
