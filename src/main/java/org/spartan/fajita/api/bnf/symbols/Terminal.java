package org.spartan.fajita.api.bnf.symbols;

public interface Terminal extends Symbol {
    Class<?> type();
	@Override
	public default String toString2() {
	    if (type() == Void.class)
		return name()+"()";
	    else
		return name()+"("+type().getSimpleName()+")";
	}
}