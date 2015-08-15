package org.spartan.fajita.api.bnf.symbols;

public interface Terminal extends Symbol {
    public default Class<?>[] type() {
	return new Class<?>[] { Void.class };
    }

    @Override
    public default String toString2() {
	if (type()[0] == Void.class)
	    return name() + "()";
	String methodSig = name() + "(";
	for (Class<?> clss : type())
	    methodSig += clss.getSimpleName() + ",";
	methodSig = methodSig.substring(0, methodSig.length() - 1);
	return methodSig + ")";
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