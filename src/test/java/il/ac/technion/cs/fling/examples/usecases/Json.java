package il.ac.technion.cs.fling.examples.usecases;

import static il.ac.technion.cs.fling.examples.generated.Json.value;
import static il.ac.technion.cs.fling.examples.generated.Json.array;
import static il.ac.technion.cs.fling.examples.generated.Json.object;

// @formatter:off
public class Json {
  public static void main(String[] args) {
    value("f");
    value(8);
    value(true);
    array(); //TODO: how come this is legal?
    array().end();
    array().value("f").end();
    array()
            .value(8)
            .value("fg")
            .array().end()
    .end();

    object().end();

    object().key("name").isSetTo().value(8).end();

    object()
            .key("name").isSetTo().value("Joe")
            .key("age").isSetTo().value(8)
    .end();

    object().
      key("name").isSetTo()
            .object()
              .key("first").isSetTo().value("Joe")
              .key("last").isSetTo()._null_()
            .end().
      key("ages").isSetTo()
            .array()
              .value(60)
              .value(70)
            .end()
    .end();
  }
}
