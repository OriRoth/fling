package il.ac.technion.cs.fling.examples.languages;

import org.antlr.v4.Tool;
import org.antlr.v4.tool.Grammar;

import il.ac.technion.cs.fling.ANTLRImporter;
import il.ac.technion.cs.fling.DPDA;
import il.ac.technion.cs.fling.FancyEBNF;
import il.ac.technion.cs.fling.adapters.JavaANTLRAPIAdapter;
import il.ac.technion.cs.fling.grammars.LL1;
import il.ac.technion.cs.fling.internal.compiler.Linker;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Model;
import il.ac.technion.cs.fling.internal.compiler.api.dom.ReliableAPICompiler;
import il.ac.technion.cs.fling.internal.grammar.rules.Named;
import il.ac.technion.cs.fling.internal.grammar.rules.Token;
import il.ac.technion.cs.fling.namers.NaiveNamer;

public class TableMaker {
  public static final String name = "TableMaker";
  public final String apiClass;

  public TableMaker() {
    final String grammarFilePath = TableMaker.class.getClassLoader().getResource("grammars/TableMaker.g").getPath();
    final Tool tool = new Tool();
    final Grammar grammar = tool.loadGrammar(grammarFilePath);
    final FancyEBNF bnf = FancyEBNF.from(new ANTLRImporter(grammar).getEbnf());
    final String packageName = "il.ac.technion.cs.fling.examples.generated";
    final String apiName = name;
    final Linker namer = new NaiveNamer(packageName, apiName);
    final LL1 ll1 = new LL1(bnf, namer);
    final JavaANTLRAPIAdapter adapter = new JavaANTLRAPIAdapter(grammarFilePath, packageName, apiName, namer);
    final DPDA<Named, Token, Named> buildAutomaton = ll1.buildAutomaton(ll1.bnf.reduce());
    final ReliableAPICompiler reliableAPICompiler = new ReliableAPICompiler(buildAutomaton);
    final Model compileFluentAPI = reliableAPICompiler.makeModel();
    apiClass = adapter.go(compileFluentAPI);
  }
}
