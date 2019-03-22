package fling.grammar.sententials;

import java.util.Collections;
import java.util.List;

import fling.grammar.types.TypeParameter;

public interface Terminal extends Symbol {
  default List<TypeParameter> parameters() {
    return Collections.emptyList();
  }
}
