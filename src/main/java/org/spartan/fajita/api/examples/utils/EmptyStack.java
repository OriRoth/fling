package org.spartan.fajita.api.examples.utils;

@SuppressWarnings({"all"})
public class EmptyStack implements AbstractStack<EmptyStack> {
  Stack<EmptyStack> push(){return null;}
}