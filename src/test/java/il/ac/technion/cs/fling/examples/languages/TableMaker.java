package il.ac.technion.cs.fling.examples.languages;
import org.antlr.v4.Tool;
import il.ac.technion.cs.fling.*;
import il.ac.technion.cs.fling.adapters.JavaANTLRAPIAdapter;
import il.ac.technion.cs.fling.grammars.LL1;
import il.ac.technion.cs.fling.internal.compiler.Linker;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Model;
import il.ac.technion.cs.fling.internal.compiler.api.dom.ReliableAPICompiler;
import il.ac.technion.cs.fling.internal.grammar.Grammar;
import il.ac.technion.cs.fling.internal.grammar.rules.Named;
import il.ac.technion.cs.fling.internal.grammar.rules.Token;
public class TableMaker {
  private static final String name = "TableMaker";
  public final String apiClass;
  public TableMaker() {
    final String grammarFilePath = TableMaker.class.getClassLoader().getResource("grammars/TableMaker.g").getPath();
    final Tool tool = new Tool();
    final Grammar grammar = tool.loadGrammar(grammarFilePath);
    final FancyEBNF bnf = FancyEBNF.from(new ANTLRImporter(grammar).getEbnf());
    final String packageName = "il.ac.technion.cs.fling.examples.generated";
    final String apiName = name;
    final Linker namer = new Linker(packageName, apiName);
    final Grammar g = new Grammar(bnf);
    final JavaANTLRAPIAdapter adapter = new JavaANTLRAPIAdapter(grammarFilePath, packageName, apiName, namer);
    final DPDA<Named, Token, Named> buildAutomaton = LL1.buildAutomaton(g.bnf.clean());
    final ReliableAPICompiler reliableAPICompiler = new ReliableAPICompiler(buildAutomaton);
    final Model compileFluentAPI = reliableAPICompiler.go();
    apiClass = adapter.go(compileFluentAPI);
  }
}
