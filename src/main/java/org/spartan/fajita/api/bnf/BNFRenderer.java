package org.spartan.fajita.api.bnf;

public interface BNFRenderer {
  // @formatter:off
  String NL = System.getProperty("line.separator");
  default String bodyAnte() {return ε();}
  default String bodyPost() {return ε();}
  default String grammarAnte() {return ε();}
  default String grammarPost() {return ε();}
  default String headAnte() {return ε();}
  default String headPost() {return ε();}
  default String ruleAnte() {return ε();}
  default String rulePost() {return ε();}
  default String startSymbolAnte() {return termAnte();}
  default String startSymbolPost() {return termAnte();}
  default String symbolAnte() {return termAnte();}
  default String symbolPost() {return termPost();}
  default String termAnte() {return ε();}
  default String terminalAnte() {return ε();}
  default String terminalPost() {return ε();}
  default String termPost() {return ε();}
  default String ε() {return "";}
  // @formatter:on

  enum builtin implements BNFRenderer {
    ASCII {
      @Override public String headPost() {
        return " =";
      }
      @Override public String rulePost() {
        return NL;
      }
      @Override public String termAnte() {
        return " ";
      }
      @Override public String symbolAnte() {
        return "";
      }
      @Override public String startSymbolAnte() {
        return "// Start symbol: ";
      }
      @Override public String startSymbolPost() {
        return NL;
      }
    },
    LATEX {
    }
  }
}
