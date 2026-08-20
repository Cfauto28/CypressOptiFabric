package net.cfauto.cypress_optifabric.impl;

public interface IChunkNibbleStorageProvider {
	public default short[] getData() {
		throw new AbstractMethodError();
	}
}
