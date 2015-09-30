package org.spartan.fajita.api.generators;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.WildcardTypeName;

class GeneratorsUtils {
  public static final String BASE_STATE_CLASSNAME = "BaseState";
  public static final String BASE_STACK_CLASSNAME = "IStack";
  public static final String STACK_TYPE_PARAMETER = "Stack";

  public static ClassName type(final String classname) {
    return ClassName.get("", classname);
  }
  public static TypeName parameterizeWithWildcard(final String name) {
    return ParameterizedTypeName.get(type(name), WildcardTypeName.subtypeOf(Object.class));
  }
}
