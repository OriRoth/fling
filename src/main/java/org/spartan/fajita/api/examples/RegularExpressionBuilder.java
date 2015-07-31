package org.spartan.fajita.api.examples;
public class RegularExpressionBuilder {

	// in this implementation the semantics is written in these classes
	// i will try to solve that in the next implementation
	// we have no real Data structure here, just a bunch of hierarchical lambda
	// functions

	public static void main(final String args[]) {
		matchRE("Za", or(range('A', 'Z'), range('a', 'z')));
		matchRE("AAA", kleeneP(range('a', 'z')));
		matchRE("ASDSD", or(chars('a', 'b', '[', ']', '3'), range('A', 'V')));

		matchRE("ay", range('a', 'c').then(except(chars('^', '#', 'A'))));

		Matcher m = word("http://").then(or(range('a', 'z'), range('A', 'Z')));
		matchRE("http://WebSitE", m);
	}

	public static abstract class Matcher {
		abstract boolean match(String s);

		final private Matcher then(final Matcher m) {
			return new Matcher() {
				@Override
				boolean match(final String s) {
					for (int i = 0; i < s.length() - 1; i++)
						if (match(s.substring(0, i)) && m.match(s.substring(i + 1)))
							return true;
					return (Matcher.this.match("") && m.match(s)) || (Matcher.this.match(s) && m.match(""));
				}
			};
		}
	}

	public static boolean matchRE(final String s, final Matcher m) {
		return m.match(s);
	}

	static Matcher range(final char c, final char d) {
		return new Matcher() {
			@Override
			public boolean match(final String s) {
				// return s.length() == 1 && s.charAt(0) <= d && s.charAt(0) >=
				// c;
				return true;
			}
		};
	}

	static Matcher chars(final char... chars) {
		return new Matcher() {
			@Override
			public boolean match(final String s) {
				if (s.length() != 1)
					return false;

				for (char c : chars) {
					if (s.charAt(0) == c)
						return true;
				}
				return false;
			}
		};
	}

	static Matcher word(final String word) {
		return new Matcher() {
			@Override
			boolean match(final String s) {
				return s.equals(word);
			}
		};
	}

	static Matcher or(final Matcher m1, final Matcher m2) {
		return new Matcher() {
			@Override
			public boolean match(final String s) {
				return m1.match(s) || m2.match(s);
			}
		};
	}

	static Matcher and(final Matcher m1, final Matcher m2) {
		return new Matcher() {
			@Override
			public boolean match(final String s) {
				return m1.match(s) && m2.match(s);
			}
		};
	}

	static Matcher kleene(final Matcher m) {
		return new Matcher() {
			@Override
			public boolean match(final String s) {
				return s.isEmpty() || m.match(s);
			}
		};
	}

	static Matcher kleeneP(final Matcher m) {
		return new Matcher() {
			@Override
			public boolean match(final String s) {
				return m.match(s);
			}
		};
	}

	static Matcher except(final Matcher m) {
		return new Matcher() {
			@Override
			public boolean match(final String s) {
				return !m.match(s);
			}
		};
	}

	// public class SyntexTree {
	// } // maybe for RE
	//
	// public class SyntexRule {
	// } // maybe for BNF?
	//
	// public class NonTerminal {
	// } // a straight forward approach to CNF's?``

}
