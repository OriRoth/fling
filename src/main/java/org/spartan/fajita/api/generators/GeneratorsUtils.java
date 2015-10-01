package org.spartan.fajita.api.generators;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.AnnotationSpec.Builder;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.WildcardTypeName;

class GeneratorsUtils {
  public enum Classname {
    BASE_STATE("BaseState"), BASE_STACK("IStack"), EMPTY_STACK("EmptyStack"), ERROR_STATE("ErrorState"), PARSE_ERROR("ParseError");
    public final String typename;

    private Classname(final String s) {
      typename = s;
    }
  }

  public static final String STACK_TYPE_PARAMETER = "Stack";

  public static ClassName type(final String classname) {
    return ClassName.get("", classname);
  }
  public static ClassName type(final Classname name) {
    return ClassName.get("", name.typename);
  }
  public static TypeName parameterizeWithWildcard(final String name) {
    return ParameterizedTypeName.get(type(name), WildcardTypeName.subtypeOf(Object.class));
  }
  public static AnnotationSpec suppressWarningAnnot(final String... types) {
    Builder $ = AnnotationSpec.builder(SuppressWarnings.class);
    com.squareup.javapoet.CodeBlock.Builder typeCode = CodeBlock.builder().add("{");
    for (int i = 0; i < types.length - 1; i++)
      typeCode.add("$S,", types[i]);
    typeCode.add("$S}", types[types.length - 1]);
    return $.addMember("value", typeCode.build()).build();
  }
}
