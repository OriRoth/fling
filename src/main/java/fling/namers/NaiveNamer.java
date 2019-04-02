package fling.namers;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fling.compiler.Namer;
import fling.compiler.api.APICompiler;
import fling.compiler.api.APICompiler.ParameterFragment;
import fling.compiler.api.nodes.APICompilationUnitNode;
import fling.compiler.api.nodes.AbstractMethodNode;
import fling.compiler.api.nodes.AbstractMethodNode.Chained;
import fling.compiler.api.nodes.AbstractMethodNode.Intermediate;
import fling.compiler.api.nodes.AbstractMethodNode.Start;
import fling.compiler.api.nodes.InterfaceNode;
import fling.compiler.ast.nodes.ASTCompilationUnitNode;
import fling.compiler.ast.nodes.ClassNode;
import fling.compiler.ast.nodes.ConcreteClassNode;
import fling.compiler.ast.nodes.FieldNode;
import fling.compiler.ast.nodes.FieldNode.FieldNodeFragment;
import fling.grammar.sententials.Symbol;
import fling.grammar.sententials.Variable;

public class NaiveNamer implements Namer {
  private final Map<Variable, Integer> childrenCounter = new HashMap<>();
  private final String packageName;
  private final String apiName;

  public NaiveNamer(String packageName, String apiName) {
    this.packageName = packageName;
    this.apiName = apiName;
  }
  @Override public Variable createChild(Variable v) {
    if (!childrenCounter.containsKey(v))
      childrenCounter.put(v, 1);
    String name = v.name() + childrenCounter.put(v, childrenCounter.get(v) + 1);
    return new Variable() {
      @Override public String name() {
        return name;
      }
      @Override public String toString() {
        return name;
      }
      @Override public int hashCode() {
        return name.hashCode();
      }
      @Override public boolean equals(Object obj) {
        if (this == obj)
          return true;
        if (!(obj instanceof Variable))
          return false;
        Variable other = (Variable) obj;
        return name().equals(other.name());
      }
    };
  }
  @Override public void name(ASTCompilationUnitNode compilationUnit) {
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
      APICompilationUnitNode<APICompiler.TypeName, APICompiler.MethodDeclaration, APICompiler.InterfaceDeclaration> fluentAPI) {
    // TODO set start methods parameter names.
    // Set intermediate methods parameter names:
    fluentAPI.interfaces.stream() //
        .filter(interfaze -> !interfaze.isBot() && !interfaze.isTop()) //
        .map(InterfaceNode::methods) //
        .flatMap(List::stream) //
        .filter(AbstractMethodNode::isIntermediateMethod) //
        .map(AbstractMethodNode::asIntermediateMethod) //
        .map(Intermediate::declaration) //
        .forEach(this::setInferredParametersIntermediateInMethod);
    // Set start methods parameter names:
    fluentAPI.startMethods.stream() //
        .map(AbstractMethodNode::asStartMethod) //
        .map(Start::declaration) //
        .forEach(this::setInferredParametersIntermediateInMethod);
    // Set concrete class methods parameter names:
    fluentAPI.concreteImplementation.methods.stream() //
        .map(AbstractMethodNode::asChainedMethod) //
        .map(Chained::declaration) //
        .forEach(this::setInferredParametersIntermediateInMethod);
  }
  @Override public String getASTClassName(Variable v) {
    return v.name();
  }
  @SuppressWarnings("static-method") protected String getBaseParameterName(Variable v) {
    return lowerCamelCase(v.name());
  }
  protected void setInferredFieldsInClass(List<FieldNode> fields) {
    Map<String, Integer> usedNames = new HashMap<>();
    for (FieldNode field : fields) {
      Symbol source = field.source;
      // TODO support more complex types.
      assert source.isVerb() || source.isVariable();
      if (source.isVariable()) {
        Variable v = source.asVariable();
        field.setInferredFieldFragments(singletonList(FieldNodeFragment.of( //
            getASTClassName(v), //
            getNameFromBase(getBaseParameterName(v), usedNames))));
      } else if (source.isVerb())
        field.setInferredFieldFragments(source.asVerb().parameters.stream() //
            .map(parameter -> FieldNodeFragment.of( //
                parameter.isStringTypeParameter() ? parameter.asStringTypeParameter().typeName()
                    : getASTClassName(parameter.asVariableTypeParameter().variable), //
                getNameFromBase(parameter.baseParameterName(), usedNames))) //
            .collect(toList()));
    }
  }
  protected void setInferredParametersIntermediateInMethod(APICompiler.MethodDeclaration declaration) {
    Map<String, Integer> usedNames = new HashMap<>();
    declaration.setInferredParameters(declaration.name.parameters.stream() //
        .map(parameter -> ParameterFragment.of( //
            parameter.isStringTypeParameter() ? parameter.asStringTypeParameter().parameterTypeName()
                : String.format("%s.%s.%s.%s", //
                    packageName, //
                    apiName, //
                    headVariableClassName(parameter.asVariableTypeParameter().variable), //
                    headVariableConclusionTypeName()), //
            getNameFromBase(parameter.baseParameterName(), usedNames)))
        .collect(toList()));
  }
  public static String lowerCamelCase(String string) {
    if (string.isEmpty())
      return string;
    return Character.toLowerCase(string.charAt(0)) + string.substring(1);
  }
  public static String getNameFromBase(String baseName, Map<String, Integer> usedNames) {
    if (!usedNames.containsKey(baseName)) {
      usedNames.put(baseName, 2);
      return baseName;
    }
    int position = usedNames.put(baseName, usedNames.get(baseName) + 1);
    return baseName + position;
  }
  @Override public String headVariableClassName(Variable variable) {
    return variable.name();
  }
  @Override public String headVariableConclusionTypeName() {
    return "$";
  }
}
