package org.spartan.fajita.api.rllp;

import org.spartan.fajita.api.bnf.BNF;

public class RLLPInstanceProvider extends RLLPConcrete {
  public RLLPInstanceProvider(BNF bnf) {
    super(bnf);
  }
  @Override public JSM getJSM() {
    return new JSMInstances(rllp);
  }

  class JSMInstances extends JSM {
    public JSMInstances(RLLP rllp) {
      super(rllp);
    }
  }
}
