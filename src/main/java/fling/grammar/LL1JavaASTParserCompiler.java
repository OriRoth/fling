package fling.grammar;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import fling.compiler.Assignment;
import fling.compiler.Namer;
import fling.compiler.ast.ASTParserCompiler;
import fling.compiler.ast.nodes.FieldNode.FieldNodeFragment;
import fling.grammar.sententials.Constants;
import fling.grammar.sententials.Notation;
import fling.grammar.sententials.Symbol;
import fling.grammar.sententials.Terminal;
import fling.grammar.sententials.Variable;
import fling.grammar.sententials.Verb;
import fling.grammar.sententials.notations.JavaCompatibleNotation;
import fling.grammar.types.TypeParameter;
import fling.namers.NaiveNamer;
import fling.util.Collections;

public class LL1JavaASTParserCompiler<Σ extends Enum<Σ> & Terminal> implements ASTParserCompiler {
  @SuppressWarnings("rawtypes") private static final Class<? extends List> inputClass = List.class;
  private final BNF bnf;
  private final Class<Σ> Σ;
  private final Namer namer;
  private final String packageName;
  private final String apiName;
  private final String astClassesContainerName;

  public LL1JavaASTParserCompiler(BNF bnf, Class<Σ> Σ, Namer namer, String packageName, String apiName,
      String astClassesContainerName) {
    this.bnf = bnf;
    this.Σ = Σ;
    this.namer = namer;
    this.packageName = packageName;
    this.apiName = apiName;
    this.astClassesContainerName = astClassesContainerName;
  }
  @Override public String printParserClass() {
    return String.format("package %s;@SuppressWarnings(\"all\")public interface %s{%s}", //
        packageName, //
        apiName, //
        bnf.V.stream() //
            .filter(v -> !Constants.S.equals(v)) //
            .filter(bnf::isOriginalVariable) //
            .map(this::printParserVariableCompilerMethod) //
            .collect(joining()));
  }
  @Override public String getParsingMethodName(Variable variable) {
    return String.format("%s.%s.parse_%s", //
        packageName, //
        apiName, //
        variable.name());
  }
  private String printParserVariableCompilerMethod(Variable v) {
    return String.format("public static %s parse_%s(%s<%s> w){%s}", //
        getClassForVariable(v), //
        v.name(), //
        inputClass.getCanonicalName(), //
        Assignment.class.getCanonicalName(), //
        Grammar.isSequenceRHS(bnf, v) ? //
            printConcreteChildMethodBody(v) : //
            printAbstractParentMethodBody(v));
  }
  private String printAbstractParentMethodBody(Variable v) {
    List<Variable> children = bnf.rhs(v).stream() //
        .map(sf -> sf.get(0)) //
        .map(Symbol::asVariable) //
        .collect(toList());
    Optional<Variable> optionalNullableChild = children.stream() //
        .filter(bnf::isNullable) //
        .findAny();
    StringBuilder body = new StringBuilder();
    if (bnf.isNullable(v))
      // Nullable child.
      body.append(String.format("if(w.isEmpty())return parse_%s(w);", //
          optionalNullableChild.get().name()));
    // Read input letter:
    body.append(Assignment.class.getCanonicalName() + " a = w.get(0);");
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
  private String printConcreteChildMethodBody(Variable v) {
    List<Symbol> children = bnf.rhs(v).get(0);
    StringBuilder body = new StringBuilder();
    body.append(Assignment.class.getCanonicalName() + " _a;");
    body.append(String.format("%s<%s> _b;", List.class.getCanonicalName(), Object.class.getCanonicalName()));
    Map<String, Integer> usedNames = new HashMap<>();
    usedNames.put("_a", 1);
    usedNames.put("_b", 1);
    List<String> argumentNames = new ArrayList<>();
    // Consume input as necessary:
    for (Symbol child : children) {
      // TODO support more complex structures.
      if (child.isVariable() && bnf.isOriginalVariable(child)) {
        String variableName = NaiveNamer.getNameFromBase(NaiveNamer.lowerCamelCase(child.name()), usedNames);
        body.append(String.format("%s %s=parse_%s(w);", //
            getClassForVariable(child.asVariable()), //
            variableName, //
            child.name()));
        argumentNames.add(variableName);
      } else if (child.isVerb()) {
        body.append("_a=w.remove(0);");
        int index = 0;
        for (TypeParameter parameter : child.asVerb().parameters) {
          String variableName = NaiveNamer.getNameFromBase(parameter.baseParameterName(), usedNames);
          String typeName = parameter.isStringTypeParameter() ? parameter.asStringTypeParameter().typeName()
              : String.format("%s.%s.%s", //
                  packageName, //
                  astClassesContainerName, //
                  namer.headVariableClassName(parameter.asVariableTypeParameter().variable));
          body.append(String.format("%s %s=(%s)_a.arguments.get(%s);", //
              typeName, //
              variableName, //
              typeName, //
              index++));
          argumentNames.add(variableName);
        }
      } else if (child.isVariable()) {
        assert bnf.extensionHeadsMapping.containsKey(child);
        Notation notation = bnf.extensionHeadsMapping.get(child);
        assert notation.getClass().isAnnotationPresent(JavaCompatibleNotation.class) : //
        "notation is not Java compatible";
        List<FieldNodeFragment> fields = getFieldsInClassContext(notation, usedNames);
        body.append(String.format("_b=%s.%s(parse_%s(w), %s);", //
            notation.getClass().getCanonicalName(), //
            JavaCompatibleNotation.abbreviationMethodName, //
            child.name(), //
            fields.size()));
        int index = 0;
        for (FieldNodeFragment field : fields) {
          String variableName = field.parameterName;
          body.append(String.format("%s %s=(%s)_b.get(%s);", //
              field.parameterType, //
              variableName, //
              field.parameterType, //
              index++));
          argumentNames.add(variableName);
        }
      }
    }
    // Compose abstract syntax node:
    body.append(String.format("return new %s(%s);", //
        getClassForVariable(v), //
        String.join(",", argumentNames)));
    return body.toString();
  }
  private List<FieldNodeFragment> getFieldsInClassContext(Symbol symbol, Map<String, Integer> usedNames) {
    if (symbol.isVerb())
      return symbol.asVerb().parameters.stream() //
          .map(parameter -> FieldNodeFragment.of( //
              parameter.isStringTypeParameter() ? parameter.asStringTypeParameter().typeName()
                  : getClassForVariable(parameter.asVariableTypeParameter().variable), //
              NaiveNamer.getNameFromBase(parameter.baseParameterName(), usedNames))) //
          .collect(toList());
    if (symbol.isVariable())
      return singletonList(FieldNodeFragment.of( //
          getClassForVariable(symbol.asVariable()), //
          NaiveNamer.getNameFromBase(getBaseParameterName(symbol.asVariable()), usedNames)));
    if (symbol.isNotation())
      return symbol.asNotation().getFields(s -> getFieldsInClassContext(s, usedNames),
          baseName -> NaiveNamer.getNameFromBase(baseName, usedNames));
    throw new RuntimeException("problem while building AST types");
  }
  @SuppressWarnings("static-method") protected String getBaseParameterName(Variable variable) {
    return NaiveNamer.lowerCamelCase(variable.name());
  }
  private String printTerminalInclusionCondition(Set<Verb> firsts) {
    return String.format("%s.included(a.σ,%s)", //
        Collections.class.getCanonicalName(), //
        firsts.stream() //
            .map(terminal -> String.format("%s.%s", //
                Σ.getCanonicalName(), //
                terminal.name())) //
            .collect(joining(",")));
  }
  private String getClassForVariable(Variable v) {
    return String.format("%s.%s.%s", //
        packageName, //
        astClassesContainerName, //
        v.name());
  }
}
