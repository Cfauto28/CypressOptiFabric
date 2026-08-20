package net.cfauto.cypress_optifabric.impl;

import net.cfauto.cypress_optifabric.ext.ChunkNibbleStorageExt;

public interface IWorldChunkProvider {
	public default short getBlockArray(int value) {
		throw new AbstractMethodError();
	}

	public default short[] getBlocks() {
		throw new AbstractMethodError();
	}

	public default ChunkNibbleStorageExt getBlockMetadata() {
		throw new AbstractMethodError();
	}

	public default ChunkNibbleStorageExt getSkyLight() {
		throw new AbstractMethodError();
	}

	public default ChunkNibbleStorageExt getBlockLight() {
		throw new AbstractMethodError();
	}
}
