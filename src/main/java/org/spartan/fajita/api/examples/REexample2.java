package org.spartan.fajita.api.examples;
import java.util.ArrayList;

public class REexample2 {


	/**
	 * has an ordered list of children
	 * 
	 * @author Tomer
	 *
	 */
	public static class Compound {
		public static enum NodeType {
			OR, AND, KLEENE, KLEENE_P, EXCEPT, STUB, LEAF;
		}

		public final NodeType type;
		public final ArrayList<Compound> children;

		public Compound(final NodeType t,final Compound ... nodes) {
			children = new ArrayList<>();
			for (Compound compound : nodes)
				children.add(compound);
			type = t;
		}

		public Compound then(final Compound sibling) {
			if (type != NodeType.STUB)
				return new Compound(NodeType.STUB, this, sibling);
			children.add(sibling);
			return this;
		}
	}

	public static class Atomic extends Compound {

		public static enum LeafType {
			WORD, Chars
		}

		public final Object content;
		public final LeafType leafType;

		public Atomic(final LeafType leafType, final Object content) {
			super(NodeType.LEAF, new Compound[] {});
			this.content = content;
			this.leafType = leafType;
		}
	}

	public static void main(final String args[]) {
		matchRE("Za", or(range('A', 'Z'), range('a', 'z')));

		matchRE("Bs", and(range('a', 'z'), range('b', 'c')));

		matchRE("AAA", kleeneP(range('a', 'z')));

		matchRE("ASDSD", or(chars('a', 'b', '[', ']', '3'), range('A', 'V')));

		matchRE("ay", range('a', 'c').then(except(chars('^', '#', 'A'))));

		Compound m = word("http://").then(or(range('a', 'z'), range('A', 'Z')));
		matchRE("http://WebSitE", m);

	}

	/**
	 * This is the only function that needs to be implemented by the "user" This
	 * is the "semantic parser" of the syntex tree denoted by nodes.
	 **/
	public static boolean matchRE(final String s, final Compound matcher) {
		return true;
	}

	private static Atomic word(final String s) {
		return new Atomic(Atomic.LeafType.WORD, s);
	}

	private static Compound kleeneP(final Compound n) {
		return new Compound(Compound.NodeType.KLEENE_P, n);
	}

	@SuppressWarnings("unused")
	private static Compound kleene(final Compound n) {
		return new Compound(Compound.NodeType.KLEENE, n);
	}

	private static Compound or(final Compound... nodes) {
		return new Compound(Compound.NodeType.OR, nodes);
	}

	private static Compound and(final Compound... nodes) {
		return new Compound(Compound.NodeType.AND, nodes);
	}

	private static Atomic range(final char from, final char to) {
		char chars[] = new char[to-from+1];
		for (int i=0;i<to-from+1;i++)
			chars[i] = (char)(i+from);
		return new Atomic(Atomic.LeafType.Chars, chars);
	}

	private static Compound except(final Compound n) {
		return new Compound(Compound.NodeType.EXCEPT, n);
	}

	public static Atomic chars(final char... cs) {
		return new Atomic(Atomic.LeafType.Chars, cs);
	}



}
