package net.cfauto.cypress_optifabric.ext;

import net.cfauto.cypress_optifabric.impl.IWorldChunkProvider;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;

public class WorldChunkExt extends WorldChunk implements IWorldChunkProvider {
	public ChunkNibbleStorageExt newblockMetadata;
	public ChunkNibbleStorageExt newskyLight;
	public ChunkNibbleStorageExt newblockLight;
	public short[] newblocks;

	public WorldChunkExt(World world, int chunkX, int chunkZ) {
		super(world, chunkX, chunkZ);
	}

	public WorldChunkExt(World world1, short[] s2, int i3, int i4) {
		this(world1, i3, i4);
		this.newblocks = s2;
		this.newblockMetadata = new ChunkNibbleStorageExt(s2.length);
		this.newskyLight = new ChunkNibbleStorageExt(s2.length);
		this.newblockLight = new ChunkNibbleStorageExt(s2.length);
    }

	public short getBlockArray(int value) {
		return this.newblocks[value];
	}

	public short[] getBlocks() {
		return this.newblocks;
	}

	public ChunkNibbleStorageExt getBlockMetadata() {
		return newblockMetadata;
	}

	public ChunkNibbleStorageExt getSkyLight() {
		return newskyLight;
	}

	public ChunkNibbleStorageExt getBlockLight() {
		return newblockLight;
	}
}
