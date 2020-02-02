package fling.examples.languages;

import org.antlr.v4.Tool;
import org.antlr.v4.tool.Grammar;

import fling.BNF;
import fling.adapters.JavaAPIAdapter;
import fling.compilers.api.ReliableAPICompiler;
import fling.grammars.LL1;
import fling.internal.compiler.Namer;
import fling.namers.NaiveNamer;

public class Exp {
  public static void main(String[] args) {
    String grammarFilePath = Exp.class.getClassLoader().getResource("Exp.g").getPath();
    Tool tool = new Tool();
    Grammar grammar = tool.loadGrammar(grammarFilePath);
    BNF bnf = BNF.fromANTLR(grammar);
    String packageName = "fling.examples.generated";
    String apiName = "Exp";
    Namer namer = new NaiveNamer(packageName, apiName);
    LL1 ll1 = new LL1(bnf, namer);
    JavaAPIAdapter adapter = new JavaAPIAdapter(packageName, apiName, "$", namer);
    String api = adapter.printFluentAPI(new ReliableAPICompiler(ll1.buildAutomaton(ll1.bnf.reachableSubBNF())).compileFluentAPI());
    System.out.println(api);
  }
}
