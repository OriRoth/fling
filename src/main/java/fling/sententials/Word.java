package fling.sententials;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

/**
 * An unmodifiable finite sequence. Supports stack notations.
 * 
 * @author Ori Roth
 */
@SuppressWarnings("unused") public class Word<T> implements List<T> {
  private List<T> inner;

  public Word() {
    inner = Collections.emptyList();
  }
  public Word(T t) {
    inner = Collections.singletonList(t);
  }
  public Word(T[] origin) {
    inner = Arrays.asList(origin);
  }
  public Word(List<T> origin) {
    inner = new ArrayList<>(origin);
  }
  @Override public boolean add(T e) {
    throw new UnsupportedOperationException();
  }
  @Override public void add(int index, T element) {
    throw new UnsupportedOperationException();
  }
  @Override public boolean addAll(Collection<? extends T> c) {
    throw new UnsupportedOperationException();
  }
  @Override public boolean addAll(int index, Collection<? extends T> c) {
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
  @Override public boolean remove(Object o) {
    throw new UnsupportedOperationException();
  }
  @Override public T remove(int index) {
    throw new UnsupportedOperationException();
  }
  @Override public boolean removeAll(Collection<?> c) {
    throw new UnsupportedOperationException();
  }
  @Override public boolean retainAll(Collection<?> c) {
    throw new UnsupportedOperationException();
  }
  @Override public T set(int index, T element) {
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
    return inner.get(0);
  }
  public Word<T> push(T t) {
    List<T> $ = new ArrayList<>(inner.size() + 1);
    $.add(0, t);
    return new Word<>($);
  }
  public Word<T> push(Collection<T> collection) {
    Objects.requireNonNull(collection);
    List<T> $ = new ArrayList<>(inner.size() + collection.size());
    $.addAll(0, collection);
    return new Word<>($);
  }
  public Word<T> pop() {
    assert !inner.isEmpty();
    return new Word<>(inner.subList(1, inner.size()));
  }
}
