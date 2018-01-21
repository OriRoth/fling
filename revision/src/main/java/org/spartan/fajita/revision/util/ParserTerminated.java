package org.spartan.fajita.revision.util;

public class ParserTerminated extends RuntimeException {
  private static final long serialVersionUID = -8093331196908937759L;

  public ParserTerminated(String message) {
    super(message);
  }
}
