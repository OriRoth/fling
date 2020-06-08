package il.ac.technion.cs.fling.adapters;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import org.antlr.v4.Tool;
import org.antlr.v4.runtime.*;
import org.antlr.v4.tool.Grammar;

import il.ac.technion.cs.fling.internal.compiler.Namer;
import il.ac.technion.cs.fling.internal.compiler.api.APICompiler.ParameterFragment;
import il.ac.technion.cs.fling.internal.grammar.sententials.Token;

// TODO handle API function parameters
public class JavaANTLRAPIAdapter extends JavaAPIAdapter {
  protected final String grammarFileResourcePath;

  public JavaANTLRAPIAdapter(String grammarFileResourcePath, String packageName, String className, String terminationMethodName,
      Namer namer) {
    super(packageName, className, terminationMethodName, namer);
    this.grammarFileResourcePath = grammarFileResourcePath;
  }
  @Override public String printConcreteImplementationClassBody() {
    return String.format("public %s<%s> w = new %s<>();", //
        List.class.getCanonicalName(), //
        String.class.getCanonicalName(), //
        LinkedList.class.getCanonicalName());
  }
  @Override protected String printConcreteImplementationMethodBody(Token σ,
      @SuppressWarnings("unused") List<ParameterFragment> parameters) {
    return String.format("w.add(\"%s\");", σ.name());
  }
  @Override protected String printTerminationMethodReturnType() {
    return ParserRuleContext.class.getCanonicalName();
  }
  @Override protected String printTerminationMethodConcreteBody() {
    return String.format("" //
        + "try{" //
        + "%s $tool=new %s();" //
        + "%s $grammar=$tool.loadGrammar(\"%s\");" //
        + "%s $input=new %s(%s.join(\"\",w).getBytes(%s.UTF_8));" //
        + "%s $lexer=$grammar.createLexerInterpreter(%s.fromStream($input,%s.UTF_8));" //
        + "%s $tokens=new %s($lexer);" //
        + "%s $parser=$grammar.createParserInterpreter($tokens);" //
        + "return $parser.parse($grammar.rules.getElement(0).index);" //
        + "}catch(%s $e){throw new %s($e);}", //
        Tool.class.getCanonicalName(), //
        Tool.class.getCanonicalName(), //
        Grammar.class.getCanonicalName(), //
        grammarFileResourcePath, //
        InputStream.class.getCanonicalName(), //
        ByteArrayInputStream.class.getCanonicalName(), //
        String.class.getCanonicalName(), //
        StandardCharsets.class.getCanonicalName(), //
        LexerInterpreter.class.getCanonicalName(), //
        CharStreams.class.getCanonicalName(), //
        StandardCharsets.class.getCanonicalName(), //
        TokenStream.class.getCanonicalName(), //
        BufferedTokenStream.class.getCanonicalName(), //
        ParserInterpreter.class.getCanonicalName(), //
        IOException.class.getCanonicalName(), //
        RuntimeException.class.getCanonicalName());
  }
  @Override protected String printStartMethodBody(Token σ, @SuppressWarnings("unused") List<ParameterFragment> parameters) {
    return String.format("return new α().%s();", σ.name());
  }
}
