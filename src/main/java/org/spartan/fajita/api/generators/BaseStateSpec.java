package org.spartan.fajita.api.generators;

import static org.spartan.fajita.api.generators.GeneratorsUtils.*;
import static org.spartan.fajita.api.generators.GeneratorsUtils.Classname.*;

import java.util.Arrays;

import javax.lang.model.element.Modifier;

import org.spartan.fajita.api.bnf.symbols.Symbol;
import org.spartan.fajita.api.generators.typeArguments.TypeArgumentManager;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;

public class BaseStateSpec {
  private final Builder builder;
  public final FieldSpec stackField;
  private final TypeArgumentManager tam;

  public BaseStateSpec(final TypeArgumentManager tam) {
    this.tam = tam;
    stackField = initializeStackField();
    builder = TypeSpec.classBuilder(BASE_STATE.typename);
    builder.addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC);
  }
  private FieldSpec initializeStackField() {
    return FieldSpec.//
        builder(tam.getType(0), "stack", Modifier.FINAL, Modifier.PROTECTED) //
        .build();
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
    for (int i = 0; i < tam.baseStateArgumentNumber(); i++)
      builder.addTypeVariable(tam.getType(i));
    return this;
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
    TypeName[] typeArguments = new TypeName[tam.baseStateArgumentNumber()];
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
            MethodSpec.constructorBuilder().addParameter(String.class, "msg", Modifier.FINAL).addStatement("super(msg)").build()) //
        .addAnnotation(suppressWarningAnnot("serial")).build();
    builder.addType(parseError);
    return this;
  }
  private BaseStateSpec addSymbolMethods() {
    for (Symbol s : tam.symbols) {
      MethodSpec method = MethodSpec.methodBuilder(s.name()) //
          .addModifiers(Modifier.PROTECTED) //
          .returns(tam.getType(s)) //
          .addStatement("throw new ParseError(\"unexpected symbol on state \" + getClass().getSimpleName())") //
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
    ParameterSpec stack = ParameterSpec.builder(tam.getType(0), "stack", Modifier.FINAL).build();
    MethodSpec constuctor = MethodSpec.constructorBuilder() //
        .addParameter(stack) //
        .addStatement("this.$N = $N", stackField, stack) //
        .build();
    builder.addMethod(constuctor);
    return this;
  }
}
