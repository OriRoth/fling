package fling.grammar.sententials;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * An unmodifiable finite sequence. Supports stack notations.
 * 
 * @author Ori Roth
 */
public class Word<T> implements List<T> {
  private List<T> inner;

  public Word() {
    inner = Collections.emptyList();
    verify();
  }
  public Word(T t) {
    inner = Collections.singletonList(t);
    verify();
  }
  @SafeVarargs public Word(T... origin) {
    inner = Arrays.asList(origin);
    verify();
  }
  public Word(Collection<T> origin) {
    inner = new ArrayList<>(origin);
    verify();
  }
  public static <T> Word<T> empty() {
    return new Word<>();
  }
  @SuppressWarnings("unused") @Override public boolean add(T t) {
    throw new UnsupportedOperationException();
  }
  @SuppressWarnings("unused") @Override public void add(int index, T element) {
    throw new UnsupportedOperationException();
  }
  @SuppressWarnings("unused") @Override public boolean addAll(Collection<? extends T> c) {
    throw new UnsupportedOperationException();
  }
  @SuppressWarnings("unused") @Override public boolean addAll(int index, Collection<? extends T> c) {
    throw new UnsupportedOperationException();
  }
  @Override public void clear() {
    throw new UnsupportedOperationException();
  }
  @Override public boolean contains(Object o) {
    return inner.contains(o);
  }
  @Override public boolean containsAll(Collection<?> c) {
    return inner.containsAll(c);
  }
  @Override public T get(int index) {
    return inner.get(index);
  }
  @Override public int indexOf(Object o) {
    return inner.indexOf(o);
  }
  @Override public boolean isEmpty() {
    return inner.isEmpty();
  }
  @Override public Iterator<T> iterator() {
    return inner.iterator();
  }
  @Override public int lastIndexOf(Object o) {
    return inner.lastIndexOf(o);
  }
  @Override public ListIterator<T> listIterator() {
    return inner.listIterator();
  }
  @Override public ListIterator<T> listIterator(int index) {
    return inner.listIterator(index);
  }
  @SuppressWarnings("unused") @Override public boolean remove(Object o) {
    throw new UnsupportedOperationException();
  }
  @SuppressWarnings("unused") @Override public T remove(int index) {
    throw new UnsupportedOperationException();
  }
  @SuppressWarnings("unused") @Override public boolean removeAll(Collection<?> c) {
    throw new UnsupportedOperationException();
  }
  @SuppressWarnings("unused") @Override public boolean retainAll(Collection<?> c) {
    throw new UnsupportedOperationException();
  }
  @SuppressWarnings("unused") @Override public T set(int index, T element) {
    throw new UnsupportedOperationException();
  }
  @Override public int size() {
    return inner.size();
  }
  @Override public List<T> subList(int fromIndex, int toIndex) {
    return inner.subList(fromIndex, toIndex);
  }
  @Override public Object[] toArray() {
    return inner.toArray();
  }
  @Override public <U> U[] toArray(U[] a) {
    return inner.toArray(a);
  }
  public T top() {
    assert !inner.isEmpty();
    return inner.get(inner.size() - 1);
  }
  public Word<T> push(T t) {
    List<T> $ = new ArrayList<>(inner.size() + 1);
    $.add(t);
    return new Word<>($);
  }
  public Word<T> push(List<T> list) {
    List<T> $ = new ArrayList<>(inner.size() + list.size());
    $.addAll(inner);
    $.addAll(list);
    return new Word<>($);
  }
  public Word<T> pop() {
    assert !inner.isEmpty();
    return new Word<>(inner.subList(0, inner.size() - 1));
  }
  @Override public int hashCode() {
    return inner.hashCode();
  }
  @Override public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof Word))
      return false;
    return inner.equals(((Word<?>) o).inner);
  }
  @Override public String toString() {
    return inner.stream().map(Object::toString).collect(Collectors.joining(""));
  }
  private void verify() {
    inner.stream().forEach(Objects::requireNonNull);
  }
}
