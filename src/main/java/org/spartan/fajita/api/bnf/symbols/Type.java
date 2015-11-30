package org.spartan.fajita.api.bnf.symbols;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Type {
  public final List<Class<?>> classes;

  public Type(final Class<?>... classes) {
    if (classes.length == 0)
      this.classes = new ArrayList<>();
    else
      this.classes = new ArrayList<>(Arrays.asList(classes));
  }
  @Override public String toString() {
    StringBuilder sb = new StringBuilder().append("(");
    if (classes.size() != 0) {
      classes.forEach(clss -> sb.append(clss.getSimpleName()).append(" X "));
      sb.delete(sb.length() - 3, sb.length());
    }
    sb.append(")");
    return sb.toString();
  }
  @Override public int hashCode() {
    final int prime = 19;
    int result = 1;
    result = prime * result + ((classes == null) ? 0 : classes.hashCode());
    return result;
  }
  @Override public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (!(obj instanceof Type))
      return false;
    Type other = (Type) obj;
    if (classes == null) {
      if (other.classes != null)
        return false;
    } else if (!classes.equals(other.classes))
      return false;
    return true;
  }

  public static final Type VOID = new Type();

  public String serialize() {
    try (ByteArrayOutputStream o = new ByteArrayOutputStream() //
    ; ObjectOutputStream oos = new ObjectOutputStream(o)) {
      oos.writeObject(classes);
      byte[] byteArray = o.toByteArray();
      String $ = "";
      for (byte b : byteArray)
        $ += (String.format("%d,", new Byte(b)));
      return $.substring(0, $.length() - 1);
    } catch (IOException e) {
      throw new RuntimeException("serialization of type failed.", e);
    }
  }
  @SuppressWarnings("unchecked") public static Type deserialize(String $) {
    Byte[] bArray = (Byte[])Arrays.stream($.split(",")).map(b -> Byte.decode(b)).collect(Collectors.toList()).toArray();
    byte[] byteArray = new byte[bArray.length];
    int i = 0; for (Byte B : bArray) byteArray[i++] = B.byteValue();
    try (ByteArrayInputStream o = new ByteArrayInputStream(byteArray) //
    ; ObjectInputStream oos = new ObjectInputStream(o)) {
      List<Class<?>> l = (List<Class<?>>) oos.readObject();
      return new Type(l.toArray(new Class<?>[]{}));
    } catch (IOException | ClassNotFoundException e) {
      throw new RuntimeException("deserialization of type failed",e);
    }
  }
}
