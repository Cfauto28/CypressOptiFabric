package net.cfauto.cypress_optifabric.region;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.storage.AlphaChunkStorage;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.World;
import net.cfauto.cypress_optifabric.region.regiondata.RegionFileCache;
import net.minecraft.world.chunk.storage.ChunkStorage;

public class McRegionChunkLoader implements ChunkStorage {
	private final File worldDir;
	private AlphaChunkStorage acs;

	public McRegionChunkLoader(File file1, AlphaChunkStorage acs) {
		this.worldDir = file1;
		this.acs = acs;
	}

	public WorldChunk loadChunk(World world, int chunkX, int chunkZ) throws IOException {
		DataInputStream dataInputStream4 = RegionFileCache.getChunkDataInputStream(this.worldDir, chunkX, chunkZ);
		if(dataInputStream4 != null) {
			NbtCompound nbtCompound = NbtIo.read(dataInputStream4);
			if(!nbtCompound.contains("Level")) {
				System.out.println("Chunk file at " + chunkX + "," + chunkZ + " is missing level data, skipping");
				return null;
			} else if(!nbtCompound.getCompound("Level").contains("Blocks")) {
				System.out.println("Chunk file at " + chunkX + "," + chunkZ + " is missing block data, skipping");
				return null;
			} else {
				WorldChunk chunk6 = AlphaChunkStorage.loadChunkFromNbt(world, nbtCompound.getCompound("Level"));
				if(!chunk6.isAt(chunkX, chunkZ)) {
					System.out.println("Chunk file at " + chunkX + "," + chunkZ + " is in the wrong location; relocating. (Expected " + chunkX + ", " + chunkZ + ", got " + chunk6.chunkX + ", " + chunk6.chunkZ + ")");
					nbtCompound.putInt("xPos", chunkX);
					nbtCompound.putInt("zPos", chunkZ);
					chunk6 = AlphaChunkStorage.loadChunkFromNbt(world, nbtCompound.getCompound("Level"));
				}

				return chunk6;
			}
		} else {
			return null;
		}
	}

	public void saveChunk(World worldObj, WorldChunk chunk) throws IOException {
		worldObj.checkSessionLock();

		try {
			DataOutputStream dataOutputStream3 = RegionFileCache.getChunkDataOutputStream(this.worldDir, chunk.chunkX, chunk.chunkZ);
			NbtCompound nbtCompound1 = new NbtCompound();
			NbtCompound nbtCompound2 = new NbtCompound();
			nbtCompound1.put("Level", nbtCompound2);
			acs.saveChunkToNbt(chunk, worldObj, nbtCompound2);
			NbtIo.write(nbtCompound1, dataOutputStream3);
			dataOutputStream3.close();
			worldObj.sizeOnDisk += RegionFileCache.getSizeDelta(this.worldDir, chunk.chunkX, chunk.chunkZ);
		} catch (Exception exception6) {
			exception6.printStackTrace();
		}

	}

	public void saveEntities(World worldObj, WorldChunk chunk) {
	}

	public void tick() {
	}

	public void flush() {
	}
}
