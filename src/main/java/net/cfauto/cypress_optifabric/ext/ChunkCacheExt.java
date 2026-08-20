package net.cfauto.cypress_optifabric.ext;

import net.cfauto.cypress_optifabric.region.EmptyChunk;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkCache;
import net.minecraft.world.chunk.ChunkSource;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.storage.ChunkStorage;

public class ChunkCacheExt extends ChunkCache implements ChunkSource {
	protected WorldChunk newempty;

	public ChunkCacheExt(World world, ChunkStorage storage, ChunkSource generator) {
		super(world, storage, generator);
		this.newempty = new EmptyChunk(world, new short[32768], 0, 0);
		this.newempty.dummy = true;
		this.newempty.empty = true;
		this.world = world;
		this.storage = storage;
		this.generator = generator;
	}

	public boolean hasChunk(int chunkX, int chunkZ) {
		if(chunkX == this.cachedChunkX && chunkZ == this.cachedChunkZ && this.cachedChunk != null) {
			return true;
		} else {
			int i3 = chunkX & 31;
			int i4 = chunkZ & 31;
			int i5 = i3 + i4 * 32;
			return this.chunks[i5] != null && (this.chunks[i5] == this.newempty || this.chunks[i5].isAt(chunkX, chunkZ));
		}
	}

	public WorldChunk getChunk(int chunkX, int chunkZ) {
		if(chunkX == this.cachedChunkX && chunkZ == this.cachedChunkZ && this.cachedChunk != null) {
			return this.cachedChunk;
		} else {
			int i3 = chunkX & 31;
			int i4 = chunkZ & 31;
			int i5 = i3 + i4 * 32;
			if(!this.hasChunk(chunkX, chunkZ)) {
				if(this.chunks[i5] != null) {
					this.chunks[i5].unload();
					this.saveChunk(this.chunks[i5]);
					this.saveEntities(this.chunks[i5]);
				}

				WorldChunk worldChunk6 = this.loadChunkFromStorage(chunkX, chunkZ);
				if(worldChunk6 == null) {
					if(this.generator == null) {
						worldChunk6 = this.newempty;
					} else {
						worldChunk6 = this.generator.getChunk(chunkX, chunkZ);
					}
				}

				this.chunks[i5] = worldChunk6;
				if(this.chunks[i5] != null) {
					this.chunks[i5].load();
				}

				if(!this.chunks[i5].terrainPopulated && this.hasChunk(chunkX + 1, chunkZ + 1) && this.hasChunk(chunkX, chunkZ + 1) && this.hasChunk(chunkX + 1, chunkZ)) {
					this.populateChunk(this, chunkX, chunkZ);
				}

				if(this.hasChunk(chunkX - 1, chunkZ) && !this.getChunk(chunkX - 1, chunkZ).terrainPopulated && this.hasChunk(chunkX - 1, chunkZ + 1) && this.hasChunk(chunkX, chunkZ + 1) && this.hasChunk(chunkX - 1, chunkZ)) {
					this.populateChunk(this, chunkX - 1, chunkZ);
				}

				if(this.hasChunk(chunkX, chunkZ - 1) && !this.getChunk(chunkX, chunkZ - 1).terrainPopulated && this.hasChunk(chunkX + 1, chunkZ - 1) && this.hasChunk(chunkX, chunkZ - 1) && this.hasChunk(chunkX + 1, chunkZ)) {
					this.populateChunk(this, chunkX, chunkZ - 1);
				}

				if(this.hasChunk(chunkX - 1, chunkZ - 1) && !this.getChunk(chunkX - 1, chunkZ - 1).terrainPopulated && this.hasChunk(chunkX - 1, chunkZ - 1) && this.hasChunk(chunkX, chunkZ - 1) && this.hasChunk(chunkX - 1, chunkZ)) {
					this.populateChunk(this, chunkX - 1, chunkZ - 1);
				}
			}

			this.cachedChunkX = chunkX;
			this.cachedChunkZ = chunkZ;
			this.cachedChunk = this.chunks[i5];
			return this.chunks[i5];
		}
	}
}
