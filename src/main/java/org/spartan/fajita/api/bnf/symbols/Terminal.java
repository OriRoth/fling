package org.spartan.fajita.api.bnf.symbols;

public interface Terminal extends Symbol {
    public default Class<?> type() {
	return Void.class;
    }

    @Override
    public default String toString2() {
	if (type() == Void.class)
	    return name() + "()";
	else
	    return name() + "(" + type().getSimpleName() + ")";
    }

    public static final Terminal epsilon = new Terminal() {
	@Override
	public String name() {
	    return "epsilon";
	}

	@Override
	public String toString() {
	    return name();
	}
    };

    public static final Terminal $ = new Terminal() {
	@Override
	public String name() {
	    return "$";
	}

	@Override
	public String toString() {
	    return name();
	}
    };
}