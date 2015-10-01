package org.spartan.fajita.api.generators;

import static org.spartan.fajita.api.generators.GeneratorsUtils.*;
import static org.spartan.fajita.api.generators.GeneratorsUtils.Classname.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.lang.model.element.Modifier;

import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.symbols.Symbol;
import org.spartan.fajita.api.bnf.symbols.Terminal;
import org.spartan.fajita.api.parser.LRParser;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;
import com.squareup.javapoet.TypeVariableName;

public class BaseStateSpec {
  private final Builder builder;
  public final List<TypeVariableName> baseStateTypeArguments;
  public final List<Symbol> symbols;
  public final FieldSpec stackField;
  private final BNF bnf;

  public BaseStateSpec(final LRParser parser) {
    bnf = parser.bnf;
    symbols = initializeSymbolIndexes();
    baseStateTypeArguments = initializeTypes();
    stackField = initializeStackField();
    builder = TypeSpec.classBuilder(BASE_STATE.typename);
    builder.addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC);
  }
  private FieldSpec initializeStackField() {
    return FieldSpec.//
        builder(baseStateTypeArguments.get(0), "stack", Modifier.FINAL, Modifier.PROTECTED) //
        .build();
  }
  private ArrayList<TypeVariableName> initializeTypes() {
    ArrayList<TypeVariableName> $ = new ArrayList<>();
    // Stack type parameter
    $.add(TypeVariableName.get(STACK_TYPE_PARAMETER, parameterizeWithWildcard(BASE_STACK.typename)));
    // symbol type parameters
    for (Symbol s : symbols)
      $.add(TypeVariableName.get(s.name(), type(BASE_STATE)));
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
    builder.addAnnotation(suppressWarningAnnot("rawtypes"));
    return this;
  }
  private BaseStateSpec addImplements() {
    builder.addSuperinterface(ParameterizedTypeName.get(type(BASE_STACK), type(STACK_TYPE_PARAMETER)));
    return this;
  }
  private TypeSpec finish() {
    return builder.build();
  }
  private BaseStateSpec addTypeParameters() {
    for (TypeVariableName type : baseStateTypeArguments)
      builder.addTypeVariable(type);
    return this;
  }
  private TypeVariableName getType(final Symbol s) {
    return baseStateTypeArguments.get(symbols.indexOf(s) + 1);
  }
  public TypeSpec generate() {
    return addTypeParameters()//
        .addImplements() //
        .addAnnotations() //
        .addFields() //
        .addConstructor() //
        .addSymbolMethods() //
        .addParseErrorException() //
        .addErrorState() //
        .finish();
  }
  private BaseStateSpec addErrorState() {
    TypeName[] typeArguments = new TypeName[baseStateTypeArguments.size()];
    Arrays.fill(typeArguments, type(ERROR_STATE));
    typeArguments[0] = type(EMPTY_STACK);
    ParameterizedTypeName superclass = ParameterizedTypeName.get(type(BASE_STATE), typeArguments);
    TypeSpec errorState = TypeSpec.classBuilder(ERROR_STATE.typename) //
        .superclass(superclass)//
        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)//
        .addMethod(MethodSpec.constructorBuilder().addStatement("super(new $T())", type(EMPTY_STACK)).build()) //
        .build();
    builder.addType(errorState);
    return this;
  }
  private BaseStateSpec addParseErrorException() {
    TypeSpec parseError = TypeSpec.classBuilder(PARSE_ERROR.typename) //
        .superclass(RuntimeException.class) //
        .addModifiers(Modifier.PUBLIC, Modifier.STATIC) //
        .addMethod(
            MethodSpec.constructorBuilder().addParameter(String.class, "msg", Modifier.FINAL).addCode("super(msg);\n").build()) //
        .addAnnotation(suppressWarningAnnot("serial")).build();
    builder.addType(parseError);
    return this;
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
    ParameterSpec stack = ParameterSpec.builder(baseStateTypeArguments.get(0), "stack", Modifier.FINAL).build();
    MethodSpec constuctor = MethodSpec.constructorBuilder() //
        .addParameter(stack) //
        .addCode("this.$N = $N", stackField, stack) //
        .build();
    builder.addMethod(constuctor);
    return this;
  }
}
