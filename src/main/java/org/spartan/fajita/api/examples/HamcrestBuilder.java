package org.spartan.fajita.api.examples;

import java.util.ArrayList;
import java.util.Stack;

import org.spartan.fajita.api.ast.Atomic;
import org.spartan.fajita.api.ast.Compound;
import org.spartan.fajita.api.ast.InheritedNonterminal;
import org.spartan.fajita.api.examples.HamcrestBuilder.EqualTo.Not;

//TODO : add anyof rules
public class HamcrestBuilder {

	// inherited nonTerminals definitions
	public interface IMatcher {
	}

	public static abstract class CompoundMatcher<T> extends Compound implements IMatcher {
		CompoundMatcher(final Matcher<T> parent) {
			this(parent, new Object[] {});
		}

		CompoundMatcher(final Matcher<T> parent, final Object... params) {
			super(parent, params);
			parent.deriveTo(this);
		}

		@SuppressWarnings("unchecked")
		@Override
		public Matcher<T> getParent() {
			return (Matcher<T>) super.getParent();
		}
		
		/**
		 * @return the highest root THAT is a CompoundMatcher 
		 * 
		 */
		@Override
		public Compound getRoot() {
			Stack<Compound> s = new Stack<>();
			Compound current = this;
			for (; current.getParent() != null; current = current.getParent())
				s.push(current);
			while(!CompoundMatcher.class.isAssignableFrom(s.peek().getClass()))
				s.pop();
			return s.peek();
		}
	}

	public static class Matcher<T> extends InheritedNonterminal implements IMatcher {
		// for top-down
		public Matcher(final Compound parent) {
			super(parent);
		}

		// for bottom-up
		public Matcher() {
			super(null);
		}

		@Override
		public String getName() {
			return "MATCHER";
		}

	}

	// inherited nonTerminals definitions

	/* %%%%%%%%%%%%%%%%%%%%%% */

	// nonTerminals definitions
	public static class S<T> extends InheritedNonterminal{
		public S(final T value, final CompoundMatcher<T> m) {
			super(null);
			deriveTo(new AssertThat<T>(this,value, m.getRoot()));
		}

		@Override
		public String getName() {
			return "S";
		}
		
	}

	public static class AssertThat<T> extends Compound {
		final AssertThatTerm child0;
		final ValueTerm<T> child1;
		final Matcher<T> child2;

		@SuppressWarnings("unchecked")
		public AssertThat(final Compound parent,final T value, final Compound c) {
			super(parent);
			child0 = (AssertThatTerm) getChild(0);
			child1 = (ValueTerm<T>) getChild(1);
			child2 = (Matcher<T>) getChild(2);

			child1.setValue(value);
			child2.deriveTo(c);
		}

		@Override
		protected ArrayList<Compound> getChildren() {
			ArrayList<Compound> $ = new ArrayList<>();
			$.add(new AssertThatTerm(this));
			$.add(new ValueTerm<T>(this));
			$.add(new Matcher<T>(this));
			return $;
		}

		@Override
		public String getName() {
			return "ASSERT_THAT";
		}
	}

	public static class InstaceOf<T> extends CompoundMatcher<T> {

		final InstanceOfTerm child0;
		final TypeTerm<T> child1;

		// for top-down
		@SuppressWarnings("unchecked")
		public InstaceOf(final Matcher<T> parent, final Class<? extends T> type) {
			super(parent);
			child0 = (InstanceOfTerm) getChild(0);
			child1 = (TypeTerm<T>) getChild(1);
			child1.setType(type);
		}

		// for bottom-up
		public InstaceOf(final Class<? extends T> type) {
			this(new Matcher<T>(), type);
		}

		@Override
		public ArrayList<Compound> getChildren() {
			ArrayList<Compound> $ = new ArrayList<>();
			$.add(new InstanceOfTerm(this));
			$.add(new TypeTerm<T>(this));
			return $;
		}

		@Override
		public String getName() {
			return "INSTANCE_OF";
		}
	}

	public static class Anything<T> extends CompoundMatcher<T> {
		final AnythingTerm child0;

		// for top-down
		public Anything(final Matcher<T> parent) {
			super(parent);
			child0 = (AnythingTerm) getChild(0);
		}

		// for bottom-up
		public Anything() {
			this(new Matcher<T>());
		}

		@Override
		protected ArrayList<Compound> getChildren() {
			ArrayList<Compound> $ = new ArrayList<>();
			$.add(new AnythingTerm(this));
			return $;
		}

		@Override
		public String getName() {
			return "ANYTHING";
		}
	}

	public static class EqualTo<T> extends CompoundMatcher<T> {
		final EqualToTerm child0;
		final ValueTerm<T> child1;

		// for top-down
		@SuppressWarnings("unchecked")
		public EqualTo(final Matcher<T> parent, final T value) {
			super(parent);
			child0 = (EqualToTerm) getChild(0);
			child1 = (ValueTerm<T>) getChild(1);
			child1.setValue(value);
		}

		// for bottom-up
		public EqualTo(final T value) {
			this(new Matcher<T>(), value);
		}

		@Override
		protected ArrayList<Compound> getChildren() {
			ArrayList<Compound> $ = new ArrayList<>();
			$.add(new EqualToTerm(this));
			$.add(new ValueTerm<T>(this));
			return $;
		}

		@Override
		public String getName() {
			return "EQUAL_TO";
		}

		public static class Not<T> extends CompoundMatcher<T> {
			final NotTerm child0;
			final Matcher<T> child1;

			// for top-down
			@SuppressWarnings("unchecked")
			public Not(final Matcher<T> parent) {
				super(parent);
				child0 = (NotTerm) getChild(0);
				child1 = (Matcher<T>) getChild(1);
			}

			public Not() {
				this(new Matcher<T>());
			}

			// for bottom-up
			public Not(final CompoundMatcher<T> matcher) {
				this(new Matcher<T>());
				child1.deriveTo(matcher);
			}

			@Override
			protected ArrayList<Compound> getChildren() {
				ArrayList<Compound> $ = new ArrayList<>();
				$.add(new NotTerm(this));
				$.add(new Matcher<T>(this));
				return $;
			}

			@Override
			public String getName() {
				return "NOT";
			}

			// bottom-up methods : all terminals in FIRST(Matcher)

			public Not<T> not() {
				return new Not<>(child1);
			}

			public EqualTo<T> equals_to(final T value) {
				return new EqualTo<T>(child1,value);
			}

			public Anything<T> anything() {
				return new Anything<T>(child1);
			}

			public InstaceOf<T> instance_of(final Class<? extends T> type) {
				return new InstaceOf<>(child1,type);
			}

		}
	}
	// end of nonTerminals definitions

	/* %%%%%%%%%%%%%%%%%%%%%% */

	// Terminals definitions

	public static class AssertThatTerm extends Atomic {
		public AssertThatTerm(final Compound parent) {
			super(parent);
		}

		@Override
		public String getName() {
			return "assertThat";
		}
	}

	public static class InstanceOfTerm extends Atomic {
		public InstanceOfTerm(final Compound parent) {
			super(parent);
		}

		@Override
		public String getName() {
			return "instance_of";
		}
	}

	public static class AnythingTerm extends Atomic {
		public AnythingTerm(final Compound parent) {
			super(parent);
		}

		@Override
		public String getName() {
			return "anything";
		}
	}

	public static class EqualToTerm extends Atomic {
		public EqualToTerm(final Compound parent) {
			super(parent);
		}

		@Override
		public String getName() {
			return "equal_to";
		}
	}

	public static class NotTerm extends Atomic {
		public NotTerm(final Compound parent) {
			super(parent);
		}

		@Override
		public String getName() {
			return "not";
		}
	}

	// public static class AnyOfTerm extends Atomic {
	// public AnyOfTerm(final Compound parent) {
	// super(parent);
	// }
	//
	// @Override
	// public String getName() {
	// return "any_of";
	// }
	// }

	public static class TypeTerm<T> extends Atomic {
		public TypeTerm(final Compound parent) {
			super(parent);
		}

		@Override
		public String getName() {
			return "type";
		}

		public void setType(final Class<? extends T> type) {
			params = new Object[] { type };
		}

		@SuppressWarnings("unchecked")
		public Class<? extends T> getType() {
			return (Class<? extends T>) params[0];
		}
	}

	public static class ValueTerm<T> extends Atomic {
		public ValueTerm(final Compound parent) {
			super(parent);
		}

		@Override
		public String getName() {
			return "value";
		}

		public void setValue(final T t) {
			params = new Object[] { t };
		}

		@SuppressWarnings("unchecked")
		public T getValue() {
			return (T) params[0];
		}

	}

	// end of terminal definitions

	/* %%%%%%%%%%%%%%%%%%%%%% */

	// static functions definitions

	public static <T> Compound assertThat(final T value, final CompoundMatcher<T> matcher) {
		return new S<T>(value, matcher);
	}

	public static <T> CompoundMatcher<T> not(final CompoundMatcher<T> matcher) {
		return new Not<T>(matcher);
	}

	public static <T> Not<T> not() {
		return new Not<T>();
	}

	public static <T> EqualTo<T> equal_to(final T value) {
		return new EqualTo<T>(value);
	}

	public static <T> Anything<T> anything() {
		return new Anything<T>();
	}

	public static <T> InstaceOf<T> instance_of(final Class<? extends T> type) {
		return new InstaceOf<T>(type);
	}
}
