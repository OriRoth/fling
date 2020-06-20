package il.ac.technion.cs.fling.internal.compiler.api.dom;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import il.ac.technion.cs.fling.adapters.APIGenerator;
import il.ac.technion.cs.fling.internal.grammar.rules.Named;
import il.ac.technion.cs.fling.internal.grammar.rules.Word;
/** A full blown type in the model, including the type name, name of formal type
 * parameters, a list of methods, and a Boolean indication on whether the type
 * is accepting or not.
 * 
 * @author Yossi Gil
 *
 * @since 2020-06-18 */
public class Type {
  /** True, if an only if, type is an accepting type */
  public final boolean isAccepting;
  /** List of methods defined in this type; empty if no methods */
  public final List<Method> methods;
  /** Name of this type */
  public final Name name;
  public Name name() {
    return name;
  }
  /** Names of formal parameters; empty if type is polymorphic */
  public final Word<Named> parameters;
  Type(Name name, List<Method> methods, Word<Named> parameters, boolean isAccepting) {
    Objects.requireNonNull(name);
    Objects.requireNonNull(methods);
    Objects.requireNonNull(parameters);
    this.name = name;
    this.methods = methods;
    this.parameters = parameters;
    this.isAccepting = isAccepting;
  }
  public Type accepting() {
    return new Type(name, methods, parameters, true);
  }
  @Override public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Type other = (Type) obj;
    return isAccepting == other.isAccepting && Objects.equals(methods, other.methods)
        && Objects.equals(name, other.name) && Objects.equals(parameters, other.parameters);
  }
  @Override public int hashCode() {
    return Objects.hash(methods, name, parameters) >>> (isAccepting ? 1 : 0);
  }
  public Stream<Method> methods() {
    return methods.stream();
  }
  public Stream<Named> parameters() {
    return parameters.stream();
  }
  private static List<Method> noMethods = Collections.emptyList();
  private static Word<Named> noParameters = Word.empty();
  public static Type named(Named n) {
    return named(Type.Name.q(n));
  }
  public static Type named(Type.Name n) {
    return new Type(n, noMethods, noParameters, false);
  }
  /** A representation of an instantiation of a polymorphic type
   * 
   * @author Yossi Gil
   *
   * @since 2020-06-19 */
  public interface Grounded {
    default public String render(APIGenerator g) {
      throw new RuntimeException(this + ": " + g);
    }
    static Grounded BOTTOM = Grounded.of(Type.Name.BOTTOM);
    static Grounded TOP = Grounded.of(Type.Name.TOP);
    static Leaf of(final Type.Name n) {
      return new Leaf(n);
    }
    class Leaf implements Grounded {
      @Override public int hashCode() {
        return Objects.hash(name);
      }
      @Override public boolean equals(Object obj) {
        if (this == obj)
          return true;
        if (obj == null)
          return false;
        if (getClass() != obj.getClass())
          return false;
        Leaf other = (Leaf) obj;
        return Objects.equals(name, other.name);
      }
      @Override public String render(APIGenerator g) {
        return g.toString(this);
      }
      public final Name name;
      Leaf(final Type.Name name) {
        this.name = name;
      }
      public final InnerNode with(final List<Grounded> arguments) {
        return new InnerNode(arguments);
      }
      public class InnerNode implements Grounded {
        @Override public String render(APIGenerator g) {
          return g.toString(this);
        }
        final List<Grounded> arguments;
        InnerNode(final List<Grounded> arguments) {
          this.arguments = arguments;
        }
        public Stream<Grounded> arguments() {
          return arguments.stream();
        }
        @Override public int hashCode() {
          return 31 * outer().hashCode() + Objects.hash(arguments);
        }
        @Override public boolean equals(Object that) {
          if (this == that)
            return true;
          if (that == null)
            return false;
          if (getClass() != that.getClass())
            return false;
          return equals((InnerNode) that);
        }
        private boolean equals(InnerNode other) {
          if (!outer().equals(other.outer()))
            return false;
          return Objects.equals(arguments, other.arguments);
        }
        public Leaf outer() {
          return Leaf.this;
        }
      }
    }
  }
  public interface Name {
    default String render(APIGenerator g) {
      throw new RuntimeException(this + ": " + g);
    }
    Name BOTTOM = new Name() {
      @Override public String render(APIGenerator g) {
        return g.renderBottomTypeName();
      }
    };
    Name END = new Name() {
      @Override public String render(APIGenerator g) {
        return g.renderEndTypeName();
      }
    };
    Name TOP = new Name() {
      @Override public String render(APIGenerator g) {
        return g.renderTopTypeName();
      }
    };
    static q q(Named q) {
      return new q(q);
    }
    static class q implements Name {
      public final Named q;
      q(Named q) {
        this.q = q;
      }
      @Override public boolean equals(Object o) {
        if (this == o)
          return true;
        if (o == null)
          return false;
        if (getClass() != o.getClass())
          return false;
        q other = (q) o;
        return Objects.equals(q, other.q);
      }
      @Override public int hashCode() {
        return Objects.hash(q);
      }
      @Override public String render(APIGenerator g) {
        return g.toString(this);
      }
      α α(Word<Named> α) {
        return new α(α);
      }
      public class α implements Name {
        public final Word<Named> α;
        public α(Word<Named> α) {
          this.α = α;
        }
        @Override public boolean equals(Object obj) {
          if (this == obj)
            return true;
          if (obj == null)
            return false;
          if (getClass() != obj.getClass())
            return false;
          α other = (α) obj;
          if (!outer().equals(other.outer()))
            return false;
          return Objects.equals(α, other.α);
        }
        @Override public int hashCode() {
          return 31 * outer().hashCode() + Objects.hash(α);
        }
        public q outer() {
          return q.this;
        }
        public Named q() {
          return outer().q;
        }
        @Override public String render(APIGenerator g) {
          return g.toString(this);
        }
        β β(Set<Named> β) {
          return new β(β);
        }
        public class β implements Name {
          public final Set<Named> β;
          β(Set<Named> β) {
            this.β = β;
          }
          @Override public boolean equals(Object o) {
            if (this == o)
              return true;
            if (o == null)
              return false;
            if (getClass() != o.getClass())
              return false;
            return equals((β) o);
          }
          private boolean equals(β other) {
            if (!outer().equals(other.outer()))
              return false;
            return Objects.equals(β, other.β);
          }
          @Override public int hashCode() {
            return outer().hashCode() + 31 * Objects.hash(β);
          }
          public α outer() {
            return α.this;
          }
          public Named q() {
            return α.this.q();
          }
          @Override public String render(APIGenerator g) {
            return g.toString(this);
          }
          public Word<Named> α() {
            return α.this.α;
          }
        }
      }
    }
  }
}
