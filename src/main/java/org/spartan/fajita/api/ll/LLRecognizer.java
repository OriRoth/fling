package org.spartan.fajita.api.ll;

import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.BNFAnalyzer;

public class LLRecognizer {
  public final BNF bnf;
  private BNFAnalyzer analyzer;

  public LLRecognizer(final BNF bnf) {
    this.bnf=bnf;
    analyzer = new BNFAnalyzer(bnf);
  }
}
