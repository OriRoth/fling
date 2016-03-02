package org.spartan.fajita.api.generators;

import static org.spartan.fajita.api.generators.GeneratorsUtils.Classname.BASE_STACK;
import static org.spartan.fajita.api.generators.GeneratorsUtils.Classname.BASE_STATE;
import static org.spartan.fajita.api.generators.GeneratorsUtils.Classname.EMPTY_STACK;
import static org.spartan.fajita.api.generators.GeneratorsUtils.Classname.ERROR_STATE;
import static org.spartan.fajita.api.generators.GeneratorsUtils.Classname.PARSE_ERROR;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.lang.model.element.Modifier;

import org.spartan.fajita.api.bnf.rules.DerivationRule;
import org.spartan.fajita.api.bnf.symbols.SpecialSymbols;
import org.spartan.fajita.api.bnf.symbols.Symbol;
import org.spartan.fajita.api.bnf.symbols.Verb;
import org.spartan.fajita.api.generators.typeArguments.TypeArgumentManager;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.AnnotationSpec.Builder;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import com.squareup.javapoet.WildcardTypeName;

public class GeneratorsUtils {
  public enum Classname {
    BASE_STATE("BaseState"), BASE_STACK("IStack"), EMPTY_STACK("EmptyStack"), ERROR_STATE("ErrorState"), PARSE_ERROR("ParseError");
    public final String typename;

    private Classname(final String s) {
      typename = s;
    }
  }

  public static final String STACK_TYPE_PARAMETER = "Stack";
  public static final String STACK_FIELD = "stack";
  public static final TypeName REDUCES_TYPE = ParameterizedTypeName.get(List.class, DerivationRule.class);
  public static final String REDUCES_FIELD = "reduces";

  public static ClassName type(final String classname) {
    return ClassName.get("", classname);
  }
  public static ClassName type(final Classname name) {
    return ClassName.get("", name.typename);
  }
  public static WildcardTypeName[] wildcardArray(final int n) {
    WildcardTypeName[] wildcards = new WildcardTypeName[n];
    Arrays.fill(wildcards, WildcardTypeName.subtypeOf(Object.class));
    return wildcards;
  }
  public static <T> T[] merge(final T[] array1, final T[] array2) {
    T[] $ = Arrays.copyOf(array1, array1.length + array2.length);
    System.arraycopy(array2, 0, $, array1.length, array2.length);
    return $;
  }
  public static AnnotationSpec suppressWarningAnnot(final String... types) {
    Builder $ = AnnotationSpec.builder(SuppressWarnings.class);
    com.squareup.javapoet.CodeBlock.Builder typeCode = CodeBlock.builder().add("{");
    for (int i = 0; i < types.length - 1; i++)
      typeCode.add("$S,", types[i]);
    typeCode.add("$S}", types[types.length - 1]);
    return $.addMember("value", typeCode.build()).build();
  }
  static TypeSpec generateIStack() {
    // public static interface IStack<Tail extends IStack>
    return TypeSpec.interfaceBuilder(BASE_STACK.typename) //
        .addModifiers(Modifier.STATIC).addTypeVariable(TypeVariableName.get("Tail", type(BASE_STACK))) //
        .addAnnotation(suppressWarningAnnot("all"))//
        .build();
  }
  static TypeSpec generateEmptyStack() {
    // public static class EmptyStack implements IStack<EmptyStack>
    return TypeSpec.classBuilder(Classname.EMPTY_STACK.typename) //
        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)//
        .addAnnotation(suppressWarningAnnot("all"))//
        .addSuperinterface(ParameterizedTypeName.get(type(BASE_STACK.typename), type(Classname.EMPTY_STACK.typename))).build();
  }
  static TypeSpec generateErrorState(TypeArgumentManager tam) {
    TypeName[] typeArguments = new TypeName[tam.baseStateArgumentNumber()];
    Arrays.fill(typeArguments, type(ERROR_STATE));
    typeArguments[0] = type(EMPTY_STACK);
    ParameterizedTypeName superclass = ParameterizedTypeName.get(type(BASE_STATE), typeArguments);
    TypeSpec errorState = TypeSpec.classBuilder(ERROR_STATE.typename) //
        .superclass(superclass)//
        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)//
        .addMethod(MethodSpec.constructorBuilder().addStatement("super(new $T(),null)", type(EMPTY_STACK)).build()) //
        .build();
    return errorState;
  }
  static TypeSpec generateParseErrorException() {
    TypeSpec parseError = TypeSpec.classBuilder(PARSE_ERROR.typename) //
        .superclass(RuntimeException.class) //
        .addModifiers(Modifier.PUBLIC, Modifier.STATIC) //
        .addMethod(
            MethodSpec.constructorBuilder().addParameter(String.class, "msg", Modifier.FINAL).addStatement("super(msg)").build()) //
        .addAnnotation(suppressWarningAnnot("serial")).build();
    return parseError;
  }
  static TypeSpec generateBaseState(TypeArgumentManager tam) {
    FieldSpec stackField = FieldSpec.builder(tam.getType(0), STACK_FIELD, Modifier.FINAL, Modifier.PRIVATE).build();
    FieldSpec reducesField = FieldSpec.builder(REDUCES_TYPE, REDUCES_FIELD, Modifier.FINAL, Modifier.PROTECTED).build();
    com.squareup.javapoet.TypeSpec.Builder builder = TypeSpec.classBuilder(BASE_STATE.typename);
    builder.addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC, Modifier.STATIC);
    builder.addMethod(MethodSpec.methodBuilder("pop").returns(type(STACK_TYPE_PARAMETER)).addModifiers(Modifier.PROTECTED)
        .addStatement("return " + STACK_FIELD).build());
    for (int i = 0; i < tam.baseStateArgumentNumber(); i++)
      builder.addTypeVariable(tam.getType(i));
    builder.addSuperinterface(ParameterizedTypeName.get(type(BASE_STACK), type(STACK_TYPE_PARAMETER)));
    builder.addAnnotation(suppressWarningAnnot("rawtypes"));
    builder.addField(stackField);
    builder.addField(reducesField);
    ParameterSpec stack = ParameterSpec.builder(tam.getType(0), STACK_FIELD, Modifier.FINAL).build();
    ParameterSpec reduces = ParameterSpec.builder(REDUCES_TYPE, REDUCES_FIELD, Modifier.FINAL).build();
    MethodSpec constuctor = MethodSpec.constructorBuilder() //
        .addParameter(stack).addParameter(reduces) //
        .addStatement("this.$N = $N", STACK_FIELD, STACK_FIELD) //
        .addStatement("this.$N = $N", REDUCES_FIELD, REDUCES_FIELD) //
        .build();
    builder.addMethod(constuctor);
    List<Symbol> symbols = new ArrayList<>(tam.symbols);
    symbols.add(SpecialSymbols.$);
    for (Symbol s : symbols) {
      com.squareup.javapoet.MethodSpec.Builder method = MethodSpec.methodBuilder(s.name()) //
          .addModifiers(Modifier.PROTECTED) //
          .returns(tam.getType(s)) //
          .addStatement("throw new ParseError(\"unexpected symbol on state \" + getClass().getSimpleName())");
      if (s.isVerb())
        for (int i = 0; i < ((Verb) s).type.classes.size(); i++)
          method.addParameter(((Verb) s).type.classes.get(i), "arg" + i);
      builder.addMethod(method.build());
    }
    return builder.build();
  }
  public static String commaSeperatedArgs(int n) {
    if (n == 0)
      return "";
    String $ = "arg0";
    for (int i = 1; i < n; i++)
      $ += ",arg" + i;
    return $;
  }
}
