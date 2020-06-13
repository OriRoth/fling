package il.ac.technion.cs.fling.namers;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import il.ac.technion.cs.fling.internal.compiler.Namer;
import il.ac.technion.cs.fling.internal.compiler.api.APICompiler;
import il.ac.technion.cs.fling.internal.compiler.api.APICompiler.ParameterFragment;
import il.ac.technion.cs.fling.internal.compiler.api.dom.CompilationUnit;
import il.ac.technion.cs.fling.internal.compiler.api.dom.AbstractMethod;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Interfac;
import il.ac.technion.cs.fling.internal.compiler.api.dom.AbstractMethod.Chained;
import il.ac.technion.cs.fling.internal.compiler.api.dom.AbstractMethod.Intermediate;
import il.ac.technion.cs.fling.internal.compiler.api.dom.AbstractMethod.Start;
import il.ac.technion.cs.fling.internal.compiler.ast.nodes.ASTCompilationUnitNode;
import il.ac.technion.cs.fling.internal.compiler.ast.nodes.ClassNode;
import il.ac.technion.cs.fling.internal.compiler.ast.nodes.ConcreteClassNode;
import il.ac.technion.cs.fling.internal.compiler.ast.nodes.FieldNode;
import il.ac.technion.cs.fling.internal.compiler.ast.nodes.FieldNode.FieldNodeFragment;
import il.ac.technion.cs.fling.internal.grammar.rules.Component;
import il.ac.technion.cs.fling.internal.grammar.rules.Constants;
import il.ac.technion.cs.fling.internal.grammar.rules.Variable;

public class NaiveNamer implements Namer {
  private final Map<Variable, Integer> astChildrenCounter = new HashMap<>();
  private final Map<Component, Integer> notationsChildrenCounter = new HashMap<>();
  private final String packageName;
  private final String apiName;

  public NaiveNamer(final String apiName) {
    this(null, apiName);
  }

  public NaiveNamer(final String packageName, final String apiName) {
    this.packageName = packageName;
    this.apiName = apiName;
  }

  @Override public Variable createASTChild(final Variable variable) {
    if (!astChildrenCounter.containsKey(variable))
      astChildrenCounter.put(variable, 1);
    final String name = variable.name() + astChildrenCounter.put(variable, astChildrenCounter.get(variable) + 1);
    return Variable.byName(name);
  }

  @Override public Variable createQuantificationChild(final List<? extends Component> symbols) {
    final Component symbol = symbols.isEmpty() || !symbols.get(0).isToken() && !symbols.get(0).isVariable() ? //
        Constants.$$ : symbols.get(0);
    if (!notationsChildrenCounter.containsKey(symbol))
      notationsChildrenCounter.put(symbol, 1);
    final String name = "_" + symbol.name()
        + notationsChildrenCounter.put(symbol, notationsChildrenCounter.get(symbol) + 1);
    return Variable.byName(name);
  }

  @Override public void name(final ASTCompilationUnitNode compilationUnit) {
    // Set class names:
    compilationUnit.classes //
        .forEach(clazz -> clazz.setClassName(getASTClassName(clazz.source)));
    // Set field names:
    compilationUnit.classes.stream() //
        .filter(ClassNode::isConcrete) //
        .map(ClassNode::asConcrete) //
        .map(ConcreteClassNode::getFields) //
        .forEach(this::setInferredFieldsInClass);
  }

  @Override public void name(
      final CompilationUnit<APICompiler.TypeName, APICompiler.MethodDeclaration, APICompiler.InterfaceDeclaration> fluentAPI) {
    // Set intermediate methods parameter names:
    fluentAPI.interfaces.stream() //
        .filter(interfaze -> !interfaze.isBot() && !interfaze.isTop()) //
        .map(Interfac::methods) //
        .flatMap(List::stream) //
        .filter(AbstractMethod::isIntermediateMethod) //
        .map(AbstractMethod::asIntermediateMethod) //
        .map(Intermediate::declaration) //
        .forEach(this::setInferredParametersIntermediateInMethod);
    // Set start methods parameter names:
    fluentAPI.startMethods.stream() //
        .map(AbstractMethod::asStartMethod) //
        .map(Start::declaration) //
        .forEach(this::setInferredParametersIntermediateInMethod);
    // Set concrete class methods parameter names:
    fluentAPI.concreteImplementation.methods.stream() //
        .map(AbstractMethod::asChainedMethod) //
        .map(Chained::declaration) //
        .forEach(this::setInferredParametersIntermediateInMethod);
  }

  @Override public String getASTClassName(final Variable v) {
    return v.name();
  }

  @SuppressWarnings("static-method") protected String getBaseParameterName(final Variable v) {
    return lowerCamelCase(v.name());
  }

  protected void setInferredFieldsInClass(final List<FieldNode> fields) {
    final Map<String, Integer> usedNames = new HashMap<>();
    fields.forEach(field -> field.setInferredFieldFragments(getFields(field.source, usedNames)));
  }

  private List<FieldNodeFragment> getFields(final Component symbol, final Map<String, Integer> usedNames) {
    if (symbol.isToken())
      return symbol.asToken().parameters() //
          .map(parameter -> {
            final String typeName;
            if (parameter.isStringTypeParameter())
              typeName = parameter.asStringTypeParameter().typeName();
            else if (parameter.isVariableTypeParameter())
              typeName = getASTClassName(parameter.asVariableTypeParameter().variable);
            else if (parameter.isVarargsTypeParameter())
              typeName = getASTClassName(parameter.asVarargsVariableTypeParameter().variable) + "[]";
            else
              throw new RuntimeException("problem while naming AST types");
            return FieldNodeFragment.of( //
                typeName, //
                getNameFromBase(lowerCamelCase(symbol.name()), usedNames));
          }) //
          .collect(toList());
    if (symbol.isVariable())
      return singletonList(new FieldNodeFragment( //
          getASTClassName(symbol.asVariable()), //
          getNameFromBase(getBaseParameterName(symbol.asVariable()), usedNames)) {
        @SuppressWarnings("unused") @Override public String visitingStatement(
            final BiFunction<Variable, String, String> variableVisitingSolver, final String accessor,
            final Supplier<String> variableNamesGenerator) {
          return variableVisitingSolver.apply(symbol.asVariable(), accessor);
        }
      });
    if (symbol.isQuantifier())
      return symbol.asQuantifier().getFields(s -> getFields(s, usedNames),
          baseName -> getNameFromBase(baseName, usedNames));
    throw new RuntimeException("problem while building AST types");
  }

  protected void setInferredParametersIntermediateInMethod(final APICompiler.MethodDeclaration declaration) {
    final Map<String, Integer> usedNames = new HashMap<>();
    declaration.setInferredParameters(declaration.name.parameters() //
        .map(parameter -> {
          final String typeName;
          if (parameter.isStringTypeParameter())
            typeName = parameter.asStringTypeParameter().parameterTypeName();
          else if (parameter.isVariableTypeParameter())
            typeName = String.format("%s.%s.%s.%s", //
                packageName, //
                apiName, //
                headVariableClassName(parameter.asVariableTypeParameter().variable), //
                headVariableConclusionTypeName());
          else if (parameter.isVarargsTypeParameter())
            typeName = String.format("%s.%s.%s.%s...", //
                packageName, //
                apiName, //
                headVariableClassName(parameter.asVarargsVariableTypeParameter().variable), //
                headVariableConclusionTypeName());
          else
            throw new RuntimeException("problem while naming API types");
          return ParameterFragment.of( //
              typeName, //
              getNameFromBase(parameter.baseParameterName(), usedNames));
        }).collect(toList()));
  }

  public static String lowerCamelCase(final String string) {
    if (string.isEmpty())
      return string;
    return Character.toLowerCase(string.charAt(0)) + string.substring(1);
  }

  public static String upperCamelCase(final String string) {
    if (string.isEmpty())
      return string;
    return Character.toUpperCase(string.charAt(0)) + string.substring(1);
  }

  public static String getNameFromBase(final String baseName, final Map<String, Integer> usedNames) {
    if (!usedNames.containsKey(baseName)) {
      usedNames.put(baseName, 2);
      return baseName;
    }
    final int position = usedNames.put(baseName, usedNames.get(baseName) + 1);
    return baseName + position;
  }

  @Override public String headVariableClassName(final Variable variable) {
    return variable.name();
  }

  @Override public String headVariableConclusionTypeName() {
    return "$";
  }
}
