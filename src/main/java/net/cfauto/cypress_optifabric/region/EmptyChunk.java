package net.cfauto.cypress_optifabric.region;

import java.util.List;
import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;

public class EmptyChunk extends WorldChunk {
	public EmptyChunk(World world1, byte[] b2, int i3, int i4) {
		super(world1, b2, i3, i4);
		this.empty = true;
	}

	@Override
	public void populateHeightMap() {
	}

	public void generateSkylightMap() {
	}

	@Override
	public int getBlockAt(int x, int y, int z) {
		return 0;
	}

	@Override
	public boolean setBlockWithMetadataAt(int x, int y, int z, int id, int metadata) {
		return true;
	}

	@Override
	public boolean setBlockAt(int x, int y, int z, int id) {
		return true;
	}

	@Override
	public int getBlockMetadataAt(int x, int y, int z) {
		return 0;
	}

	@Override
	public void setBlockMetadataAt(int x, int y, int z, int metadata) {
	}

	@Override
	public int getActualLightAt(int x, int y, int z, int skyLightSubtracted) {
		return 0;
	}

	@Override
	public void load() {
	}

	@Override
	public void unload() {
	}

	@Override
	public BlockEntity getBlockEntityAt(int x, int y, int z) {
		return null;
	}

	@Override
	public boolean hasSkyAccessAt(int x, int y, int z) {
		return false;
	}

	@Override
	public void removeEntity(Entity entity) {
	}

	@Override
	public void addEntity(Entity entity) {
	}

	@Override
	public void removeEntity(Entity entity, int index) {
	}

	@Override
	public void addBlockEntity(BlockEntity blockEntity) {
	}

	@Override
	public void setBlockEntityAt(int x, int y, int z, BlockEntity blockEntity) {
	}

	@Override
	public void removeBlockEntityAt(int x, int y, int z) {
	}

	@Override
	public void markDirty() {
	}

	@Override
	public void getEntities(Entity exclude, Box bounds, List entities) {
	}

	@Override
	public boolean shouldSave(boolean z1) {
		return false;
	}

	@Override
	public int unpackChunkData(byte[] blocks, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, int size) {
		int i9 = maxX - minX;
		int i10 = maxY - minY;
		int i11 = maxZ - minZ;
		int i12 = i9 * i10 * i11;
		return i12 + i12 / 2 * 3;
	}

	@Override
	public Random getRandomForSlime(long j1) {
		return new Random(this.world.seed + ((long) this.chunkX * this.chunkX * 4987142) + (this.chunkX * 5947611L) + ((long) this.chunkZ * this.chunkZ) * 4392871L + (this.chunkZ * 389711L) ^ j1);
	}
}
