package net.cfauto.cypress_optifabric.impl;

public interface IShortArrayProvider {
	public default void setShortArray(String string1, short[] s2) {
		throw new AbstractMethodError();
	}

	public default short[] getShortArray(String string1) {
		throw new AbstractMethodError();
	}
}
