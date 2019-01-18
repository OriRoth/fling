package roth.ori.fling.symbols;

import roth.ori.fling.symbols.types.VarArgs;

public interface Terminal extends GrammarElement {
  default Verb with(Object... parameterTypes) {
    return new Verb(this, parameterTypes);
  }
  default Verb many(NonTerminal nt) {
    return with(new VarArgs(nt));
  }
  default Verb many(Class<?> parameterType) {
    return with(new VarArgs(parameterType));
  }
}
