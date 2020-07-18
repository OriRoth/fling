package il.ac.technion.cs.fling.grammars;
import java.util.*;
import static java.util.stream.Collectors.*;
import static java.util.Collections.singletonList;
import il.ac.technion.cs.fling.FancyEBNF;
import il.ac.technion.cs.fling.internal.compiler.Invocation;
import il.ac.technion.cs.fling.internal.compiler.Linker;
import il.ac.technion.cs.fling.internal.compiler.ast.ASTParserCompiler;
import il.ac.technion.cs.fling.internal.compiler.ast.nodes.FieldNode.FieldNodeFragment;
import il.ac.technion.cs.fling.internal.grammar.rules.*;
import il.ac.technion.cs.fling.internal.grammar.sententials.quantifiers.JavaCompatibleQuantifier;
import il.ac.technion.cs.fling.internal.grammar.types.Parameter;
/** Compiles BNF to run-time LL(1) compiler, generating AST from sequence of
 * terminals.
 *
 * @author Ori Roth
 * @param <Σ> terminals enum */
public class LL1JavaASTParserCompiler<Σ extends Enum<Σ> & Terminal> implements ASTParserCompiler {
  @SuppressWarnings("rawtypes") private static final Class<? extends List> inputClass = List.class;
  private static final String ListObject = String.format("%s<%s>", List.class.getCanonicalName(),
      Object.class.getCanonicalName());
  private static final String ListWild = String.format("%s<?>", List.class.getCanonicalName());
  private final FancyEBNF bnf;
  private final Class<Σ> Σ;
  private final Linker namer;
  private final String packageName;
  private final String apiName;
  private final String astClassesContainerName;
  public LL1JavaASTParserCompiler(final FancyEBNF bnf, final Class<Σ> Σ, final Linker namer, final String packageName,
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
    //
    //
    return String.format("public static %s parse_%s(%s<%s> w){%s}", //
        bnf.isOriginalVariable(v) ? getClassForVariable(v) : ListObject, //
        v.name(), //
        inputClass.getCanonicalName(), //
        Invocation.class.getCanonicalName(), //
        bnf.isOriginalVariable(v) ? isSequenceRHS(bnf, v) ? //
            printConcreteChildMethodBody(v) : //
            printAbstractParentMethodBody(v) : printConcreteExtensionChildMethodBody(v));
  }
  private String printAbstractParentMethodBody(final Variable v) {
    final List<Variable> children = bnf.bodies(v)//
        .map(sf -> sf.get(0)) //
        .map(Component::asVariable) //
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
    body.append(Invocation.class.getCanonicalName()).append(" _a = w.get(0);");
    // Diverge by firsts sets:
    children.stream() //
        .filter(child -> optionalNullableChild.isEmpty() || !child.equals(optionalNullableChild.get())) //
        .map(child -> String.format("if(%s)return parse_%s(w);", //
            printTerminalInclusionCondition(bnf.firsts(child)), //
            child.name())) //
        .forEach(body::append);
    // Default to nullable child or unreachable null value:
    body.append(String.format("return %s;", optionalNullableChild.isEmpty() ? //
        "null" : //
        String.format("parse_%s(w)", optionalNullableChild.get().name())));
    return body.toString();
  }
  @SuppressWarnings("boxing") private String printConcreteChildMethodBody(final Variable v) {
    final List<Component> children = bnf.bodiesList(v).get(0);
    final StringBuilder body = new StringBuilder();
    body.append(Invocation.class.getCanonicalName()).append(" _a;");
    body.append(ListWild).append(" _b;");
    final Map<String, Integer> usedNames = new HashMap<>();
    usedNames.put("_a", 1);
    usedNames.put("_b", 1);
    final List<String> argumentNames = new ArrayList<>();
    // Consume input as necessary:
    for (final Component child : children)
      // TODO support more complex structures.
      if (child.isVariable() && bnf.isOriginalVariable(child)) {
        final String variableName = Linker.getNameFromBase(Linker.lowerCamelCase(child.name()), usedNames);
        body.append(String.format("%s %s=parse_%s(w);", //
            getClassForVariable(child.asVariable()), //
            variableName, //
            child.name()));
        argumentNames.add(variableName);
      } else if (child.isToken()) {
        body.append("_a=w.remove(0);");
        int index = 0;
        for (final Parameter parameter : child.asToken().parameters) {
          final String variableName = Linker.getNameFromBase(parameter.baseParameterName(), usedNames);
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
    final List<Component> children = bnf.bodiesList(v).get(0);
    final StringBuilder body = new StringBuilder();
    body.append(Invocation.class.getCanonicalName()).append(" _a;");
    body.append(ListObject).append(" _b;");
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
    for (final Component child : children)
      // TODO support more complex structures.
      if (child.isVariable() && bnf.isOriginalVariable(child)) {
        final String variableName = Linker.getNameFromBase(Linker.lowerCamelCase(child.name()), usedNames);
        body.append(String.format("%s %s=parse_%s(w);", //
            getClassForVariable(child.asVariable()), //
            variableName, //
            child.name()));
        argumentNames.add(variableName);
      } else if (child.isToken()) {
        body.append("_a=w.remove(0);");
        int index = 0;
        for (final Parameter parameter : child.asToken().parameters) {
          final String variableName = Linker.getNameFromBase(parameter.baseParameterName(), usedNames);
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
        final String variableName = Linker.getNameFromBase("_c", usedNames);
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
  private String getTypeName(final Parameter parameter) {
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
  private List<FieldNodeFragment> getFieldsInClassContext(final Component symbol,
      final Map<String, Integer> usedNames) {
    if (symbol.isToken())
      return symbol.asToken().parameters() //
          .map(parameter -> FieldNodeFragment.of( //
              getTypeName(parameter), //
              Linker.getNameFromBase(parameter.baseParameterName(), usedNames))) //
          .collect(toList());
    if (symbol.isVariable())
      return singletonList(FieldNodeFragment.of( //
          getClassForVariable(symbol.asVariable()), //
          Linker.getNameFromBase(getBaseParameterName(symbol.asVariable()), usedNames)));
    if (symbol.isQuantifier())
      return symbol.asQuantifier().getFields(s -> getFieldsInClassContext(s, usedNames),
          baseName -> Linker.getNameFromBase(baseName, usedNames));
    throw new RuntimeException("problem while building AST types");
  }
  @SuppressWarnings("static-method")
  private String getBaseParameterName(final Variable variable) {
    return Linker.lowerCamelCase(variable.name());
  }
  private String printTerminalInclusionCondition(final Set<Token> firsts) {
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
  private static boolean isSequenceRHS(final FancyEBNF bnf, final Variable v) {
    final List<Body> rhs = bnf.bodiesList(v);
    return rhs.size() == 1 && (rhs.get(0).size() != 1 || !bnf.isOriginalVariable(rhs.get(0).get(0)));
  }
}
