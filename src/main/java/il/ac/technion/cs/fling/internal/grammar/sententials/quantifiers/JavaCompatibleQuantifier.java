package il.ac.technion.cs.fling.internal.grammar.sententials.quantifiers;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
@Retention(RetentionPolicy.RUNTIME) public @interface JavaCompatibleQuantifier {
  String abbreviationMethodName = "abbreviate";
}
