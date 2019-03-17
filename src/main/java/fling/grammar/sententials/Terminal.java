package fling.grammar.sententials;

import java.util.Collections;
import java.util.List;

public interface Terminal extends Symbol {
  default List<String> parameters() {
    return Collections.emptyList();
  }
}
