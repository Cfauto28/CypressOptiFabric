package net.cfauto.cypress_optifabric.impl;

public interface IGameOptionsProvider {
	default boolean getAsyncGen() {
		throw new AbstractMethodError();
	}
}
