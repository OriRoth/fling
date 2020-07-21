package il.ac.technion.cs.fling.examples.languages;
import org.antlr.v4.Tool;
import il.ac.technion.cs.fling.*;
import il.ac.technion.cs.fling.adapters.JavaANTLRAPIAdapter;
import il.ac.technion.cs.fling.grammars._.LL1;
import il.ac.technion.cs.fling.internal.compiler.Linker;
import il.ac.technion.cs.fling.internal.compiler.api.dom.ReliableAPICompiler;
import il.ac.technion.cs.fling.internal.grammar._.Grammar;
import il.ac.technion.cs.fling.internal.grammar.rules.Named;
import il.ac.technion.cs.fling.internal.grammar.rules.Token;
public class TableMaker {
  private static final String name = "TableMaker";
  public final String apiClass;
  public TableMaker() {
    final var grammarFilePath = TableMaker.class.getClassLoader().getResource("grammars/TableMaker.g").getPath();
    final var tool = new Tool();
    final Grammar grammar = tool.loadGrammar(grammarFilePath);
    final var bnf = FancyEBNF.from(new ANTLRImporter(grammar).getEbnf());
    final var packageName = "il.ac.technion.cs.fling.examples.generated";
    final var apiName = name;
    final var namer = new Linker(packageName, apiName);
    final var g = new Grammar(bnf);
    final var adapter = new JavaANTLRAPIAdapter(grammarFilePath, packageName, apiName, namer);
    final DPDA<Named, Token, Named> buildAutomaton = LL1.buildAutomaton(g.bnf.clean());
    final var reliableAPICompiler = new ReliableAPICompiler(buildAutomaton);
    final var compileFluentAPI = reliableAPICompiler.go();
    apiClass = adapter.go(compileFluentAPI);
  }
}
