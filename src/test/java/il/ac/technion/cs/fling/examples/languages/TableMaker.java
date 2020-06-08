package il.ac.technion.cs.fling.examples.languages;

import org.antlr.v4.Tool;
import org.antlr.v4.tool.Grammar;

import il.ac.technion.cs.fling.ANTLRImporter;
import il.ac.technion.cs.fling.DPDA;
import il.ac.technion.cs.fling.FancyEBNF;
import il.ac.technion.cs.fling.Named;
import il.ac.technion.cs.fling.adapters.JavaANTLRAPIAdapter;
import il.ac.technion.cs.fling.compilers.api.ReliableAPICompiler;
import il.ac.technion.cs.fling.grammars.LL1;
import il.ac.technion.cs.fling.internal.compiler.Namer;
import il.ac.technion.cs.fling.internal.compiler.api.APICompiler.InterfaceDeclaration;
import il.ac.technion.cs.fling.internal.compiler.api.APICompiler.MethodDeclaration;
import il.ac.technion.cs.fling.internal.compiler.api.APICompiler.TypeName;
import il.ac.technion.cs.fling.internal.compiler.api.nodes.APICompilationUnitNode;
import il.ac.technion.cs.fling.internal.grammar.rules.Token;
import il.ac.technion.cs.fling.namers.NaiveNamer;

public class TableMaker {
  public static final String name = "TableMaker";
  public final String apiClass;

  public TableMaker() {
    String grammarFilePath = TableMaker.class.getClassLoader().getResource("grammars/TableMaker.g").getPath();
    Tool tool = new Tool();
    Grammar grammar = tool.loadGrammar(grammarFilePath);
    FancyEBNF bnf = new ANTLRImporter(grammar).getEbnf();
    String packageName = "il.ac.technion.cs.fling.examples.generated";
    String apiName = name;
    Namer namer = new NaiveNamer(packageName, apiName);
    LL1 ll1 = new LL1(bnf, namer);
    JavaANTLRAPIAdapter adapter = new JavaANTLRAPIAdapter(grammarFilePath, packageName, apiName, "$", namer);
    DPDA<Named, Token, Named> buildAutomaton = ll1.buildAutomaton(ll1.bnf.reduce());
    ReliableAPICompiler reliableAPICompiler = new ReliableAPICompiler(buildAutomaton);
    APICompilationUnitNode<TypeName, MethodDeclaration, InterfaceDeclaration> compileFluentAPI = reliableAPICompiler
        .compileFluentAPI();
    apiClass = adapter.printFluentAPI(compileFluentAPI);
  }
}
