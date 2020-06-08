package il.ac.technion.cs.fling.examples.languages;

import org.antlr.v4.Tool;
import org.antlr.v4.tool.Grammar;

import il.ac.technion.cs.fling.FancyEBNF;
import il.ac.technion.cs.fling.adapters.JavaANTLRAPIAdapter;
import il.ac.technion.cs.fling.compilers.api.ReliableAPICompiler;
import il.ac.technion.cs.fling.grammars.LL1;
import il.ac.technion.cs.fling.internal.compiler.Namer;
import il.ac.technion.cs.fling.namers.NaiveNamer;

public class TableMaker {
  public static final String name = "TableMaker";
  public static final String apiClass;
  static {
    String grammarFilePath = TableMaker.class.getClassLoader().getResource("grammars/TableMaker.g").getPath();
    Tool tool = new Tool();
    Grammar grammar = tool.loadGrammar(grammarFilePath);
    FancyEBNF bnf = FancyEBNF.fromANTLR(grammar);
    String packageName = "il.ac.technion.cs.fling.examples.generated";
    String apiName = name;
    Namer namer = new NaiveNamer(packageName, apiName);
    LL1 ll1 = new LL1(bnf, namer);
    JavaANTLRAPIAdapter adapter = new JavaANTLRAPIAdapter(grammarFilePath, packageName, apiName, "$", namer);
    apiClass = adapter
        .printFluentAPI(new ReliableAPICompiler(ll1.buildAutomaton(ll1.bnf.reachableSubBNF())).compileFluentAPI());
  }
}
