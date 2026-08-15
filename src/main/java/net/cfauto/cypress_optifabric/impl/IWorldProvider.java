package net.cfauto.cypress_optifabric.impl;

import java.util.List;

public interface IWorldProvider {
	public default boolean getRegion() {
		throw new AbstractMethodError();
	}
	
	public default void doLightUpdatesExt() {
		throw new AbstractMethodError();
	}
	
	public default List getBlocklightingToUpdate() {
		throw new AbstractMethodError();
	}
}
