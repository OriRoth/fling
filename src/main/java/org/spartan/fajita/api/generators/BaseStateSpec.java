package org.spartan.fajita.api.generators;

import static org.spartan.fajita.api.generators.GeneratorsUtils.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.lang.model.element.Modifier;

import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.symbols.Symbol;
import org.spartan.fajita.api.bnf.symbols.Terminal;
import org.spartan.fajita.api.parser.LRParser;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;
import com.squareup.javapoet.TypeVariableName;

public class BaseStateSpec {
  private final Builder builder;
  public final List<TypeVariableName> types;
  public final List<Symbol> symbols;
  public final FieldSpec stackField;
  private final BNF bnf;

  public BaseStateSpec(final LRParser parser) {
    bnf = parser.bnf;
    symbols = initializeSymbolIndexes();
    types = initializeTypes();
    stackField = initializeStackField();
    builder = TypeSpec.classBuilder(BASE_STATE_CLASSNAME);
    builder.addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC);
  }
  private FieldSpec initializeStackField() {
    return FieldSpec.//
        builder(types.get(0), "stack", Modifier.FINAL, Modifier.PROTECTED) //
        .build();
  }
  private ArrayList<TypeVariableName> initializeTypes() {
    ArrayList<TypeVariableName> $ = new ArrayList<>();
    // Stack type parameter
    $.add(TypeVariableName.get(STACK_TYPE_PARAMETER, parameterizeWithWildcard(BASE_STACK_CLASSNAME)));
    // symbol type parameters
    for (Symbol s : symbols)
      $.add(TypeVariableName.get(s.name(), type(BASE_STATE_CLASSNAME)));
    return $;
  }
  private List<Symbol> initializeSymbolIndexes() {
    List<Symbol> $ = new LinkedList<>();
    $.addAll(bnf.getTerminals());
    $.addAll(bnf.getNonTerminals());
    $.remove(Terminal.$);
    $.remove(bnf.getAugmentedStartSymbol());
    return $;
  }
  private BaseStateSpec addAnnotations() {
    builder.addAnnotation(AnnotationSpec.builder(SuppressWarnings.class).addMember("value", "\"rawtypes\"").build());
    return this;
  }
  private BaseStateSpec addImplements() {
    builder.addSuperinterface(ParameterizedTypeName.get(type(BASE_STACK_CLASSNAME), type(STACK_TYPE_PARAMETER)));
    return this;
  }
  private TypeSpec finish() {
    return builder.build();
  }
  private BaseStateSpec addTypeParameters() {
    for (TypeVariableName type : types)
      builder.addTypeVariable(type);
    return this;
  }
  private TypeVariableName getType(final Symbol s) {
    return types.get(symbols.indexOf(s) + 1);
  }
  public TypeSpec generate() {
    return addTypeParameters()//
        .addImplements() //
        .addAnnotations() //
        .addFields() //
        .addConstructor() //
        .addSymbolMethods() //
        .finish();
  }
  private BaseStateSpec addSymbolMethods() {
    for (Symbol s : symbols) {
      MethodSpec method = MethodSpec.methodBuilder(s.name()) //
          .addModifiers(Modifier.PROTECTED) //
          .returns(getType(s)) //
          .addCode("throw new ParseError(\"unexpected symbol on state \" + getClass().getSimpleName());\n") //
          .build();
      builder.addMethod(method);
    }
    return this;
  }
  private BaseStateSpec addFields() {
    builder.addField(stackField);
    return this;
  }
  private BaseStateSpec addConstructor() {
    ParameterSpec stack = ParameterSpec.builder(types.get(0), "stack", Modifier.FINAL).build();
    MethodSpec constuctor = MethodSpec.constructorBuilder() //
        .addParameter(stack) //
        .addCode("this.$N = $N", stackField, stack) //
        .build();
    builder.addMethod(constuctor);
    return this;
  }
}
