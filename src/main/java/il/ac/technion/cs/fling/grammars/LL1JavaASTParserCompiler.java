package il.ac.technion.cs.fling.grammars;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.*;

import java.util.*;

import il.ac.technion.cs.fling.*;
import il.ac.technion.cs.fling.internal.compiler.*;
import il.ac.technion.cs.fling.internal.compiler.ast.ASTParserCompiler;
import il.ac.technion.cs.fling.internal.compiler.ast.nodes.FieldNode.FieldNodeFragment;
import il.ac.technion.cs.fling.internal.grammar.Grammar;
import il.ac.technion.cs.fling.internal.grammar.sententials.*;
import il.ac.technion.cs.fling.internal.grammar.sententials.notations.JavaCompatibleQuantifier;
import il.ac.technion.cs.fling.internal.grammar.types.TypeParameter;
import il.ac.technion.cs.fling.namers.NaiveNamer;

/**
 * Compiles BNF to run-time LL(1) compiler, generating AST from sequence of
 * terminals.
 * 
 * @author Ori Roth
 * @param <Σ> terminals enum
 */
public class LL1JavaASTParserCompiler<Σ extends Enum<Σ> & Terminal> implements ASTParserCompiler {
  @SuppressWarnings("rawtypes") private static final Class<? extends List> inputClass = List.class;
  private static final String ListObject = String.format("%s<%s>", List.class.getCanonicalName(), Object.class.getCanonicalName());
  private static final String ListWild = String.format("%s<?>", List.class.getCanonicalName());
  private final BNF bnf;
  private final Class<Σ> Σ;
  private final Namer namer;
  private final String packageName;
  private final String apiName;
  private final String astClassesContainerName;

  public LL1JavaASTParserCompiler(final BNF bnf, final Class<Σ> Σ, final Namer namer, final String packageName,
      final String apiName, final String astClassesContainerName) {
    this.bnf = bnf;
    this.Σ = Σ;
    this.namer = namer;
    this.packageName = packageName;
    this.apiName = apiName;
    this.astClassesContainerName = astClassesContainerName;
  }
  @Override public String printParserClass() {
    return String.format("package %s;\n import java.util.*;\n @SuppressWarnings(\"all\")public interface %s{%s}", //
        packageName, //
        apiName, //
        bnf.Γ.stream() //
            .filter(v -> !Constants.S.equals(v)) //
            .map(this::printParserVariableCompilerMethod) //
            .collect(joining()));
  }
  @Override public String getParsingMethodName(final Variable variable) {
    return String.format("%s.%s.parse_%s", //
        packageName, //
        apiName, //
        variable.name());
  }
  private String printParserVariableCompilerMethod(final Variable v) {
    return String.format("public static %s parse_%s(%s<%s> w){%s}", //
        bnf.isOriginalVariable(v) ? getClassForVariable(v) : ListObject, //
        v.name(), //
        inputClass.getCanonicalName(), //
        Assignment.class.getCanonicalName(), //
        !bnf.isOriginalVariable(v) ? //
            printConcreteExtensionChildMethodBody(v) : //
            Grammar.isSequenceRHS(bnf, v) ? //
                printConcreteChildMethodBody(v) : //
                printAbstractParentMethodBody(v));
  }
  private String printAbstractParentMethodBody(final Variable v) {
    final List<Variable> children = bnf.rhs(v).stream() //
        .map(sf -> sf.get(0)) //
        .map(GeneralizedSymbol::asVariable) //
        .collect(toList());
    final Optional<Variable> optionalNullableChild = children.stream() //
        .filter(bnf::isNullable) //
        .findAny();
    final StringBuilder body = new StringBuilder();
    if (bnf.isNullable(v))
      // Nullable child.
      body.append(String.format("if(w.isEmpty())return parse_%s(w);", //
          optionalNullableChild.get().name()));
    // Read input letter:
    body.append(Assignment.class.getCanonicalName() + " _a = w.get(0);");
    // Diverge by firsts sets:
    children.stream() //
        .filter(child -> !optionalNullableChild.isPresent() || !child.equals(optionalNullableChild.get())) //
        .map(child -> String.format("if(%s)return parse_%s(w);", //
            printTerminalInclusionCondition(bnf.firsts(child)), //
            child.name())) //
        .forEach(body::append);
    // Default to nullable child or unreachable null value:
    body.append(String.format("return %s;", !optionalNullableChild.isPresent() ? //
        "null" : //
        String.format("parse_%s(w)", optionalNullableChild.get().name())));
    return body.toString();
  }
  private String printConcreteChildMethodBody(final Variable v) {
    final List<GeneralizedSymbol> children = bnf.rhs(v).get(0);
    final StringBuilder body = new StringBuilder();
    body.append(Assignment.class.getCanonicalName() + " _a;");
    body.append(ListWild + " _b;");
    final Map<String, Integer> usedNames = new HashMap<>();
    usedNames.put("_a", 1);
    usedNames.put("_b", 1);
    final List<String> argumentNames = new ArrayList<>();
    // Consume input as necessary:
    for (final GeneralizedSymbol child : children)
      // TODO support more complex structures.
      if (child.isVariable() && bnf.isOriginalVariable(child)) {
        final String variableName = NaiveNamer.getNameFromBase(NaiveNamer.lowerCamelCase(child.name()), usedNames);
        body.append(String.format("%s %s=parse_%s(w);", //
            getClassForVariable(child.asVariable()), //
            variableName, //
            child.name()));
        argumentNames.add(variableName);
      } else if (child.isVerb()) {
        body.append("_a=w.remove(0);");
        int index = 0;
        for (final TypeParameter parameter : child.asVerb().parameters) {
          final String variableName = NaiveNamer.getNameFromBase(parameter.baseParameterName(), usedNames);
          final String typeName = getTypeName(parameter);
          body.append(String.format("%s %s=(%s)_a.arguments.get(%s);", //
              typeName, //
              variableName, //
              typeName, //
              index++));
          argumentNames.add(variableName);
        }
      } else if (child.isVariable()) {
        assert bnf.extensionHeadsMapping.containsKey(child);
        final Quantifier notation = bnf.extensionHeadsMapping.get(child);
        assert notation.getClass().isAnnotationPresent(JavaCompatibleQuantifier.class) : //
        "notation is not Java compatible";
        final List<FieldNodeFragment> fields = getFieldsInClassContext(notation, usedNames);
        body.append(String.format("_b=%s.%s(parse_%s(w), %s);", //
            notation.getClass().getCanonicalName(), //
            JavaCompatibleQuantifier.abbreviationMethodName, //
            child.name(), //
            fields.size()));
        int index = 0;
        for (final FieldNodeFragment field : fields) {
          final String variableName = field.parameterName;
          body.append(String.format("%s %s=(%s)_b.get(%s);", //
              field.parameterType, //
              variableName, //
              field.parameterType, //
              index++));
          argumentNames.add(variableName);
        }
      } else
        throw new RuntimeException("problem while creating real-time parser");
    // Compose abstract syntax node:
    body.append(String.format("return new %s(%s);", //
        getClassForVariable(v), //
        String.join(",", argumentNames)));
    return body.toString();
  }
  private String printConcreteExtensionChildMethodBody(final Variable v) {
    final List<GeneralizedSymbol> children = bnf.rhs(v).get(0);
    final StringBuilder body = new StringBuilder();
    body.append(Assignment.class.getCanonicalName() + " _a;");
    body.append(ListObject + " _b;");
    final Map<String, Integer> usedNames = new HashMap<>();
    usedNames.put("_a", 1);
    usedNames.put("_b", 1);
    final List<String> argumentNames = new ArrayList<>();
    if (bnf.isNullable(v)) {
      body.append(String.format("if(w.isEmpty())return %s.%s();", //
          Collections.class.getCanonicalName(), //
          "emptyList"));
      body.append("_a=w.get(0);");
      body.append(String.format("if(!(%s))return %s.%s();", //
          printTerminalInclusionCondition(bnf.firsts(v)), //
          Collections.class.getCanonicalName(), //
          "emptyList"));
    }
    // Consume input as necessary:
    for (final GeneralizedSymbol child : children)
      // TODO support more complex structures.
      if (child.isVariable() && bnf.isOriginalVariable(child)) {
        final String variableName = NaiveNamer.getNameFromBase(NaiveNamer.lowerCamelCase(child.name()), usedNames);
        body.append(String.format("%s %s=parse_%s(w);", //
            getClassForVariable(child.asVariable()), //
            variableName, //
            child.name()));
        argumentNames.add(variableName);
      } else if (child.isVerb()) {
        body.append("_a=w.remove(0);");
        int index = 0;
        for (final TypeParameter parameter : child.asVerb().parameters) {
          final String variableName = NaiveNamer.getNameFromBase(parameter.baseParameterName(), usedNames);
          final String typeName = getTypeName(parameter);
          body.append(String.format("%s %s=(%s)_a.arguments.get(%s);", //
              typeName, //
              variableName, //
              typeName, //
              index++));
          argumentNames.add(variableName);
        }
      } else if (child.isVariable()) {
        assert bnf.extensionProducts.contains(child);
        final String variableName = NaiveNamer.getNameFromBase("_c", usedNames);
        body.append(String.format("%s %s=parse_%s(w);", //
            ListObject, //
            variableName, //
            child.name()));
        argumentNames.add(variableName);
      } else
        throw new RuntimeException("problem while creating real-time parser");
    // Compose abstract syntax node:
    body.append(String.format("return %s.%s(%s);", //
        Arrays.class.getCanonicalName(), //
        "asList", //
        String.join(",", argumentNames)));
    return body.toString();
  }
  private String getTypeName(final TypeParameter parameter) {
    if (parameter.isStringTypeParameter())
      return parameter.asStringTypeParameter().typeName();
    else if (parameter.isVariableTypeParameter())
      return String.format("%s.%s.%s", //
          packageName, //
          astClassesContainerName, //
          namer.headVariableClassName(parameter.asVariableTypeParameter().variable));
    else if (parameter.isVarargsTypeParameter())
      return String.format("%s.%s.%s[]", //
          packageName, //
          astClassesContainerName, //
          namer.headVariableClassName(parameter.asVarargsVariableTypeParameter().variable));
    else
      throw new RuntimeException("problem while creating real-time parser");
  }
  private List<FieldNodeFragment> getFieldsInClassContext(final GeneralizedSymbol symbol, final Map<String, Integer> usedNames) {
    if (symbol.isVerb())
      return symbol.asVerb().parameters.stream() //
          .map(parameter -> FieldNodeFragment.of( //
              getTypeName(parameter), //
              NaiveNamer.getNameFromBase(parameter.baseParameterName(), usedNames))) //
          .collect(toList());
    if (symbol.isVariable())
      return singletonList(FieldNodeFragment.of( //
          getClassForVariable(symbol.asVariable()), //
          NaiveNamer.getNameFromBase(getBaseParameterName(symbol.asVariable()), usedNames)));
    if (symbol.isQuantifier())
      return symbol.asQuantifier().getFields(s -> getFieldsInClassContext(s, usedNames),
          baseName -> NaiveNamer.getNameFromBase(baseName, usedNames));
    throw new RuntimeException("problem while building AST types");
  }
  @SuppressWarnings("static-method") protected String getBaseParameterName(final Variable variable) {
    return NaiveNamer.lowerCamelCase(variable.name());
  }
  private String printTerminalInclusionCondition(final Set<Verb> firsts) {
    return String.format("%s.included(_a.σ,%s)", //
        il.ac.technion.cs.fling.internal.util.Is.class.getCanonicalName(), //
        firsts.stream() //
            .map(terminal -> String.format("%s.%s", //
                Σ.getCanonicalName(), //
                terminal.name())) //
            .collect(joining(",")));
  }
  private String getClassForVariable(final Variable v) {
    return String.format("%s.%s.%s", //
        packageName, //
        astClassesContainerName, //
        v.name());
  }
}
