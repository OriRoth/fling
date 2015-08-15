package org.spartan.fajita.api.bnf.symbols;

public interface Terminal extends Symbol {
    public Class<?>[] type();

    @Override
    public default String methodSignatureString() {
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

	@Override
	public Class<?>[] type() {
	    return null;
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

	@Override
	public Class<?>[] type() {
	    return null;
	}
    };

    public static final Class<?>[] VoidType = new Class<?>[] { Void.class };
}