//package org.spartan.fajita.api.examples.hamcrest;
//
//import java.util.ArrayList;
//
//import org.spartan.fajita.api.ast.Atomic;
//import org.spartan.fajita.api.ast.Compound;
//import org.spartan.fajita.api.ast.InheritedNonterminal;
//import org.spartan.fajita.api.examples.hamcrest.HamcrestBuilder.EqualTo.AnyOf;
//import org.spartan.fajita.api.examples.hamcrest.HamcrestBuilder.EqualTo.Not;
//
//public class HamcrestBuilder {
//  // inherited nonTerminals definitions
//  interface IMatcher {
//    //
//  }
//
//  public static abstract class CompoundMatcher<T> extends Compound implements IMatcher {
//    CompoundMatcher(final Matcher<T> parent) {
//      super(parent);
//      parent.deriveTo(this);
//    }
//    @SuppressWarnings("unchecked") @Override public Matcher<T> getParent() {
//      return (Matcher<T>) super.getParent();
//    }
//  }
//
//  @SuppressWarnings("unused") static class Matcher<T> extends InheritedNonterminal implements IMatcher {
//    // for top-down
//    Matcher(final Compound parent) {
//      super(parent);
//    }
//    // for bottom-up
//    Matcher() {
//      super(null);
//    }
//    @Override public String getName() {
//      return "MATCHER";
//    }
//  }
//  // inherited nonTerminals definitions
//  /* %%%%%%%%%%%%%%%%%%%%%% */
//
//  // nonTerminals definitions
//  public static class S<T> extends InheritedNonterminal {
//    @SuppressWarnings("unused") S(final T value, final CompoundMatcher<T> m) {
//      super(null);
//      deriveTo(new AssertThat<T>(this, value, m.getRoot()));
//    }
//    @Override public String getName() {
//      return "S";
//    }
//  }
//
//  public static class AssertThat<T> extends Compound {
//    final AssertThatTerm child0;
//    final ValueTerm<T> child1;
//    final Matcher<T> child2;
//
//    @SuppressWarnings("unchecked") AssertThat(final Compound parent, final T value, final Compound c) {
//      super(parent);
//      child0 = (AssertThatTerm) getChild(0);
//      child1 = (ValueTerm<T>) getChild(1);
//      child2 = (Matcher<T>) getChild(2);
//      child1.setValue(value);
//      child2.deriveTo(c);
//    }
//    @Override protected ArrayList<Compound> getChildren() {
//      ArrayList<Compound> $ = new ArrayList<>();
//      $.add(new AssertThatTerm(this));
//      $.add(new ValueTerm<T>(this));
//      $.add(new Matcher<T>(this));
//      return $;
//    }
//    @Override public String getName() {
//      return "ASSERT_THAT";
//    }
//  }
//
//  public static class InstaceOf<T> extends CompoundMatcher<T> {
//    final InstanceOfTerm child0;
//    final TypeTerm<T> child1;
//
//    // for top-down
//    @SuppressWarnings("unchecked") InstaceOf(final Matcher<T> parent, final Class<? extends T> type) {
//      super(parent);
//      child0 = (InstanceOfTerm) getChild(0);
//      child1 = (TypeTerm<T>) getChild(1);
//      child1.setType(type);
//    }
//    // for bottom-up
//    InstaceOf(final Class<? extends T> type) {
//      this(new Matcher<T>(), type);
//    }
//    @Override public ArrayList<Compound> getChildren() {
//      ArrayList<Compound> $ = new ArrayList<>();
//      $.add(new InstanceOfTerm(this));
//      $.add(new TypeTerm<T>(this));
//      return $;
//    }
//    @Override public String getName() {
//      return "INSTANCE_OF";
//    }
//  }
//
//  public static class Anything<T> extends CompoundMatcher<T> {
//    final AnythingTerm child0;
//
//    // for top-down
//    Anything(final Matcher<T> parent) {
//      super(parent);
//      child0 = (AnythingTerm) getChild(0);
//    }
//    // for bottom-up
//    Anything() {
//      this(new Matcher<T>());
//    }
//    @Override protected ArrayList<Compound> getChildren() {
//      ArrayList<Compound> $ = new ArrayList<>();
//      $.add(new AnythingTerm(this));
//      return $;
//    }
//    @Override public String getName() {
//      return "ANYTHING";
//    }
//  }
//
//  public static class EqualTo<T> extends CompoundMatcher<T> {
//    final EqualToTerm child0;
//    final ValueTerm<T> child1;
//
//    // for top-down
//    @SuppressWarnings("unchecked") EqualTo(final Matcher<T> parent, final T value) {
//      super(parent);
//      child0 = (EqualToTerm) getChild(0);
//      child1 = (ValueTerm<T>) getChild(1);
//      child1.setValue(value);
//    }
//    // for bottom-up
//    EqualTo(final T value) {
//      this(new Matcher<T>(), value);
//    }
//    @Override protected ArrayList<Compound> getChildren() {
//      ArrayList<Compound> $ = new ArrayList<>();
//      $.add(new EqualToTerm(this));
//      $.add(new ValueTerm<T>(this));
//      return $;
//    }
//    @Override public String getName() {
//      return "EQUAL_TO";
//    }
//
//    public static class AnyOf<T> extends CompoundMatcher<T> {
//      final AnyOfTerm<T> child0;
//
//      // for top-down
//      @SuppressWarnings("unchecked") AnyOf(final Matcher<T> parent, final CompoundMatcher<T>[] matchers) {
//        super(parent);
//        child0 = (AnyOfTerm<T>) getChild(0);
//        for (CompoundMatcher<T> matcher : matchers) {
//          children.add(new Matcher<T>(this));
//          ((Matcher<T>) children.get(children.size() - 1)).deriveTo(matcher.getRoot());
//        }
//      }
//      // for bottom-up
//      AnyOf(final CompoundMatcher<T>[] value) {
//        this(new Matcher<T>(), value);
//      }
//      @Override protected ArrayList<Compound> getChildren() {
//        ArrayList<Compound> $ = new ArrayList<>();
//        $.add(new AnyOfTerm<T>(this));
//        return $;
//      }
//      @Override public String getName() {
//        return "ANY_OF";
//      }
//    }
//
//    public static class Not<T> extends CompoundMatcher<T> {
//      final NotTerm child0;
//      final Matcher<T> child1;
//
//      // for top-down
//      @SuppressWarnings("unchecked") Not(final Matcher<T> parent) {
//        super(parent);
//        child0 = (NotTerm) getChild(0);
//        child1 = (Matcher<T>) getChild(1);
//      }
//      Not() {
//        this(new Matcher<T>());
//      }
//      // for bottom-up
//      Not(final CompoundMatcher<T> matcher) {
//        this(new Matcher<T>());
//        child1.deriveTo(matcher);
//      }
//      @Override protected ArrayList<Compound> getChildren() {
//        ArrayList<Compound> $ = new ArrayList<>();
//        $.add(new NotTerm(this));
//        $.add(new Matcher<T>(this));
//        return $;
//      }
//      @Override public String getName() {
//        return "NOT";
//      }
//      // bottom-up methods : all terminals in FIRST(Matcher)
//      public Not<T> not() {
//        return new Not<>(child1);
//      }
//      @SuppressWarnings("unused") public EqualTo<T> equals_to(final T value) {
//        return new EqualTo<T>(child1, value);
//      }
//      @SuppressWarnings("unused") public Anything<T> anything() {
//        return new Anything<T>(child1);
//      }
//      public InstaceOf<T> instance_of(final Class<? extends T> type) {
//        return new InstaceOf<>(child1, type);
//      }
//      @SuppressWarnings("unchecked") public AnyOf<T> any_of(final CompoundMatcher<T>... compoundMatchers) {
//        return new AnyOf<>(child1, compoundMatchers);
//      }
//    }
//  }
//  // end of nonTerminals definitions
//  /* %%%%%%%%%%%%%%%%%%%%%% */
//
//  // Terminals definitions
//  public static class AssertThatTerm extends Atomic {
//    AssertThatTerm(final Compound parent) {
//      super(parent);
//    }
//    @Override public String getName() {
//      return "assertThat";
//    }
//  }
//
//  public static class InstanceOfTerm extends Atomic {
//    InstanceOfTerm(final Compound parent) {
//      super(parent);
//    }
//    @Override public String getName() {
//      return "instance_of";
//    }
//  }
//
//  public static class AnythingTerm extends Atomic {
//    AnythingTerm(final Compound parent) {
//      super(parent);
//    }
//    @Override public String getName() {
//      return "anything";
//    }
//  }
//
//  public static class EqualToTerm extends Atomic {
//    EqualToTerm(final Compound parent) {
//      super(parent);
//    }
//    @Override public String getName() {
//      return "equal_to";
//    }
//  }
//
//  public static class NotTerm extends Atomic {
//    NotTerm(final Compound parent) {
//      super(parent);
//    }
//    @Override public String getName() {
//      return "not";
//    }
//  }
//
//  @SuppressWarnings("unused") public static class AnyOfTerm<T> extends Atomic {
//    AnyOfTerm(final Compound parent) {
//      super(parent);
//    }
//    @Override public String getName() {
//      return "any_of";
//    }
//  }
//
//  public static class TypeTerm<T> extends Atomic {
//    private Class<? extends T> type;
//
//    TypeTerm(final Compound parent) {
//      super(parent);
//    }
//    @Override public String getName() {
//      return "type";
//    }
//    public void setType(final Class<? extends T> type) {
//      this.type = type;
//    }
//    public Class<? extends T> getType() {
//      return type;
//    }
//    @Override public String toString() {
//      return super.toString() + " = " + type.getSimpleName();
//    }
//  }
//
//  public static class ValueTerm<T> extends Atomic {
//    private T t;
//
//    ValueTerm(final Compound parent) {
//      super(parent);
//    }
//    @Override public String getName() {
//      return "value";
//    }
//    public void setValue(final T t) {
//      this.t = t;
//    }
//    public T getValue() {
//      return t;
//    }
//    @Override public String toString() {
//      return super.toString() + " = " + t;
//    }
//  }
//  // end of terminal definitions
//  /* %%%%%%%%%%%%%%%%%%%%%% */
//
//  // bottom-up methods , return CompoundMatcher so no additional methods will
//  // we invoked
//  @SuppressWarnings("unused") public static <T> CompoundMatcher<T> not(final CompoundMatcher<T> matcher) {
//    return new Not<T>(matcher);
//  }
//  // top-down methods
//  @SuppressWarnings("unused") public static <T> Compound assertThat(final T value, final CompoundMatcher<T> matcher) {
//    return new S<T>(value, matcher);
//  }
//  @SuppressWarnings("unused") public static <T> Not<T> not() {
//    return new Not<T>();
//  }
//  @SuppressWarnings("unused") public static <T> EqualTo<T> equal_to(final T value) {
//    return new EqualTo<T>(value);
//  }
//  @SuppressWarnings("unused") public static <T> Anything<T> anything() {
//    return new Anything<T>();
//  }
//  @SuppressWarnings("unused") public static <T> InstaceOf<T> instance_of(final Class<? extends T> type) {
//    return new InstaceOf<T>(type);
//  }
//  @SuppressWarnings("unchecked") public static <T> AnyOf<T> any_of(final CompoundMatcher<T>... matchers) {
//    return new AnyOf<>(matchers);
//  }
//}
