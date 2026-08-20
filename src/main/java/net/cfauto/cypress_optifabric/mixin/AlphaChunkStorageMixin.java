package net.cfauto.cypress_optifabric.mixin;

import java.util.Iterator;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import net.cfauto.cypress_optifabric.ext.ChunkNibbleStorageExt;
import net.cfauto.cypress_optifabric.ext.WorldChunkExt;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entities;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.storage.AlphaChunkStorage;

@Mixin(AlphaChunkStorage.class)
public class AlphaChunkStorageMixin {

	@Overwrite
	public void saveChunkToNbt(WorldChunk chunk, World world, NbtCompound nbt) {
		WorldChunkExt worldChunkext = (WorldChunkExt) chunk;
		world.checkSessionLock();;
		nbt.putInt("xPos", chunk.chunkX);
		nbt.putInt("zPos", chunk.chunkZ);
		nbt.putLong("LastUpdate", world.ticks);
		nbt.setShortArray("Blocks", worldChunkext.newblocks);
		nbt.setShortArray("Data", worldChunkext.newblockMetadata.newdata);
		nbt.setShortArray("SkyLight", worldChunkext.newskyLight.newdata);
		nbt.setShortArray("BlockLight", worldChunkext.newblockLight.newdata);
		nbt.putByteArray("HeightMap", chunk.heightMap);
		nbt.putBoolean("TerrainPopulated", chunk.terrainPopulated);
		chunk.lastSaveHadEntities = false;
		NbtList nbtList4 = new NbtList();

		NbtCompound nbtCompound5;
		Iterator iterator7;
		for(int i6 = 0; i6 < chunk.entities.length; ++i6) {
			iterator7 = chunk.entities[i6].iterator();

			while(iterator7.hasNext()) {
				Entity entity8 = (Entity)iterator7.next();
				chunk.lastSaveHadEntities = true;
				nbtCompound5 = new NbtCompound();
				if(entity8.writeNbt(nbtCompound5)) {
					nbtList4.addElement(nbtCompound5);
				}
			}
		}

		nbt.put("Entities", nbtList4);
		NbtList nbtList9 = new NbtList();
		iterator7 = chunk.blockEntities.values().iterator();

		while(iterator7.hasNext()) {
			BlockEntity blockEntity10 = (BlockEntity)iterator7.next();
			nbtCompound5 = new NbtCompound();
			blockEntity10.writeNbt(nbtCompound5);
			nbtList9.addElement(nbtCompound5);
		}

		nbt.put("TileEntities", nbtList9);
	}

	@Overwrite
	public static WorldChunk loadChunkFromNbt(World world, NbtCompound nbt) {
		int i2 = nbt.getInt("xPos");
		int i3 = nbt.getInt("zPos");
		WorldChunkExt worldChunk4 = new WorldChunkExt(world, i2, i3);
		worldChunk4.newblocks = nbt.getShortArray("Blocks");
		worldChunk4.newblockMetadata = new ChunkNibbleStorageExt(nbt.getShortArray("Data"));
		worldChunk4.newskyLight = new ChunkNibbleStorageExt(nbt.getShortArray("SkyLight"));
		worldChunk4.newblockLight = new ChunkNibbleStorageExt(nbt.getShortArray("BlockLight"));
		worldChunk4.heightMap = nbt.getByteArray("HeightMap");
		worldChunk4.terrainPopulated = nbt.getBoolean("TerrainPopulated");
		if(!worldChunk4.newblockMetadata.hasData()) {
			worldChunk4.newblockMetadata = new ChunkNibbleStorageExt(worldChunk4.newblocks.length);
		}

		if(worldChunk4.heightMap == null || !worldChunk4.newskyLight.hasData()) {
			worldChunk4.heightMap = new byte[256];
			worldChunk4.newskyLight = new ChunkNibbleStorageExt(worldChunk4.newblocks.length);
			worldChunk4.populateHeightMap();
		}

		if(!worldChunk4.newblockLight.hasData()) {
			worldChunk4.newblockLight = new ChunkNibbleStorageExt(worldChunk4.newblocks.length);
			worldChunk4.populateLight();
		}

		NbtList nbtList5 = nbt.getList("Entities");
		if(nbtList5 != null) {
			for(int i6 = 0; i6 < nbtList5.size(); ++i6) {
				NbtCompound nbtCompound7 = (NbtCompound)nbtList5.get(i6);
				Entity entity8 = Entities.create(nbtCompound7, world);
				worldChunk4.lastSaveHadEntities = true;
				if(entity8 != null) {
					worldChunk4.addEntity(entity8);
				}
			}
		}

		NbtList nbtList10 = nbt.getList("TileEntities");
		if(nbtList10 != null) {
			for(int i11 = 0; i11 < nbtList10.size(); ++i11) {
				NbtCompound nbtCompound12 = (NbtCompound)nbtList10.get(i11);
				BlockEntity blockEntity9 = BlockEntity.fromNbt(nbtCompound12);
				if(blockEntity9 != null) {
					worldChunk4.addBlockEntity(blockEntity9);
				}
			}
		}

		return worldChunk4;
	}
}
