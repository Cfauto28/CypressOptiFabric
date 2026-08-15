package net.cfauto.cypress_optifabric.impl;

public interface IOptipineScreenProvider {
	default void drawBG(String string) {
		throw new AbstractMethodError();
	}
}
