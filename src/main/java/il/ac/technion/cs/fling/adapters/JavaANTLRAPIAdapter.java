package il.ac.technion.cs.fling.adapters;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import org.antlr.v4.Tool;
import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.LexerInterpreter;
import org.antlr.v4.runtime.ParserInterpreter;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.tool.Grammar;
import il.ac.technion.cs.fling.internal.compiler.Linker;
import il.ac.technion.cs.fling.internal.compiler.api.dom.MethodParameter;
import il.ac.technion.cs.fling.internal.grammar.rules.Token;

// TODO handle API function parameters
public class JavaANTLRAPIAdapter extends JavaGenerator {
  protected final String grammarFileResourcePath;

  public JavaANTLRAPIAdapter(final String grammarFileResourcePath, final String packageName, final String className,
      final Linker namer) {
    super(packageName, className);
    this.grammarFileResourcePath = grammarFileResourcePath;
  }

  @Override public String printConcreteImplementationClassBody() {
    return String.format("public %s<%s> w = new %s<>();", //
        List.class.getCanonicalName(), //
        String.class.getCanonicalName(), //
        LinkedList.class.getCanonicalName());
  }

  @Override protected String printConcreteImplementationMethodBody(final Token σ,
      @SuppressWarnings("unused") final List<MethodParameter> parameters) {
    return String.format("w.add(\"%s\");", σ.name());
  }

  @Override protected String printStartMethodBody(final Token σ,
      @SuppressWarnings("unused") final List<MethodParameter> parameters) {
    return String.format("return new α().%s();", σ.name());
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

  @Override protected String printTerminationMethodReturnType() {
    return ParserRuleContext.class.getCanonicalName();
  }
}
