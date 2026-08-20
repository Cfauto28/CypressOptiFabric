package net.cfauto.cypress_optifabric.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import ext.block.ExtBlock;
import net.cfauto.cypress_optifabric.impl.IWorldChunkProvider;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;

@Mixin(WorldChunk.class)
public class WorldChunkMixin implements IWorldChunkProvider {

	@Shadow
	public static boolean hasSkyLight;

	@Shadow
	public World world;

	@Shadow
	public byte[] heightMap;

	@Shadow
	public int lowestHeight;

	@Shadow
	public int chunkX;

	@Shadow
	public int chunkZ;

	@Shadow
	public boolean dirty;

	@Shadow
	private void lightGaps(int localX, int localZ) {}

	@Overwrite
	public void populateHeightMapOnly() {
		int i1 = 127;

		for(int i2 = 0; i2 < 16; ++i2) {
			for(int i3 = 0; i3 < 16; ++i3) {
				int i4 = 127;

				for(int i5 = i2 << 11 | i3 << 7; i4 > 0 && ExtBlock.OPACITIES[this.getBlocks()[i5 + i4 - 1]] == 0; --i4) {
				}

				this.heightMap[i3 << 4 | i2] = (byte)i4;
				if(i4 < i1) {
					i1 = i4;
				}
			}
		}

		this.lowestHeight = i1;
		this.dirty = true;
	}

	@Overwrite
	private void updateHeightMap(int localX, int y, int localZ) {
		int i4 = this.heightMap[localZ << 4 | localX] & 255;
		int i5 = i4;
		if(y > i4) {
			i5 = y;
		}

		int i6;
		for(i6 = localX << 11 | localZ << 7; i5 > 0 && ExtBlock.OPACITIES[this.getBlocks()[i6 + i5 - 1]] == 0; --i5) {
		}

		if(i5 != i4) {
			this.world.onHeightMapChanged(localX, localZ, i5, i4);
			this.heightMap[localZ << 4 | localX] = (byte)i5;
			int i7;
			int i8;
			if(i5 < this.lowestHeight) {
				this.lowestHeight = i5;
			} else {
				i6 = 127;
				i7 = 0;

				while(true) {
					if(i7 >= 16) {
						this.lowestHeight = i6;
						break;
					}

					for(i8 = 0; i8 < 16; ++i8) {
						if((this.heightMap[i8 << 4 | i7] & 255) < i6) {
							i6 = this.heightMap[i8 << 4 | i7] & 255;
						}
					}

					++i7;
				}
			}

			i6 = this.chunkX * 16 + localX;
			i7 = this.chunkZ * 16 + localZ;
			if(i5 < i4) {
				for(i8 = i5; i8 < i4; ++i8) {
					this.getSkyLight().set(localX, i8, localZ, 15);
				}
			} else {
				this.world.updateLight(LightType.SKY, i6, i4, i7, i6, i5, i7);

				for(i8 = i4; i8 < i5; ++i8) {
					this.getSkyLight().set(localX, i8, localZ, 0);
				}
			}

			i8 = 15;

			int i9;
			for(i9 = i5; i5 > 0 && i8 > 0; this.getSkyLight().set(localX, i5, localZ, i8)) {
				--i5;
				int i10 = ExtBlock.OPACITIES[this.getBlockAt(localX, i5, localZ)];
				if(i10 == 0) {
					i10 = 1;
				}

				i8 -= i10;
				if(i8 < 0) {
					i8 = 0;
				}
			}

			while(i5 > 0 && ExtBlock.OPACITIES[this.getBlockAt(localX, i5 - 1, localZ)] == 0) {
				--i5;
			}

			if(i5 != i9) {
				this.world.updateLight(LightType.SKY, i6 - 1, i5, i7 - 1, i6 + 1, i9, i7 + 1);
			}

			this.dirty = true;
		}

	}

	@Overwrite
	public int getBlockAt(int localX, int y, int localZ) {
		int i4 = localX << 11 | localZ << 7 | y;
		return this.getBlocks()[i4];
	}

	@Overwrite
	public boolean setBlockWithMetadataAt(int localX, int y, int localZ, int i4, int metadata) {
		int i7 = this.heightMap[localZ << 4 | localX] & 255;
		int i8 = this.getBlocks()[localX << 11 | localZ << 7 | y];
		if(i8 == i4 && this.getBlockMetadata().get(localX, y, localZ) == metadata) {
			return false;
		} else {
			int i9 = this.chunkX * 16 + localX;
			int i10 = this.chunkZ * 16 + localZ;
			this.getBlocks()[localX << 11 | localZ << 7 | y] = (short)i4;
			if(i8 != 0 && !this.world.isMultiplayer) {
				ExtBlock.BY_ID[i8].onRemoved(this.world, i9, y, i10);
			}

			this.getBlockMetadata().set(localX, y, localZ, metadata);
			if(ExtBlock.OPACITIES[i4] != 0) {
				if(y >= i7) {
					this.updateHeightMap(localX, y + 1, localZ);
				}
			} else if(y == i7 - 1) {
				this.updateHeightMap(localX, y, localZ);
			}

			this.world.updateLight(LightType.SKY, i9, y, i10, i9, y, i10);
			this.world.updateLight(LightType.BLOCK, i9, y, i10, i9, y, i10);
			this.lightGaps(localX, localZ);
			if(i4 != 0) {
				ExtBlock.BY_ID[i4].onAdded(this.world, i9, y, i10);
			}

			this.dirty = true;
			return true;
		}
	}

	@Overwrite
	public boolean setBlockAt(int localX, int y, int localZ, int i4) {
		int i6 = this.heightMap[localZ << 4 | localX] & 255;
		int i7 = this.getBlocks()[localX << 11 | localZ << 7 | y];
		if(i7 == i4) {
			return false;
		} else {
			int i8 = this.chunkX * 16 + localX;
			int i9 = this.chunkZ * 16 + localZ;
			this.getBlocks()[localX << 11 | localZ << 7 | y] = (short)i4;
			if(i7 != 0) {
				ExtBlock.BY_ID[i7].onRemoved(this.world, i8, y, i9);
			}

			this.getBlockMetadata().set(localX, y, localZ, 0);
			if(ExtBlock.OPACITIES[i4] != 0) {
				if(y >= i6) {
					this.updateHeightMap(localX, y + 1, localZ);
				}
			} else if(y == i6 - 1) {
				this.updateHeightMap(localX, y, localZ);
			}

			this.world.updateLight(LightType.SKY, i8, y, i9, i8, y, i9);
			this.world.updateLight(LightType.BLOCK, i8, y, i9, i8, y, i9);
			this.lightGaps(localX, localZ);
			if(i4 != 0 && !this.world.isMultiplayer) {
				ExtBlock.BY_ID[i4].onAdded(this.world, i8, y, i9);
			}

			this.dirty = true;
			return true;
		}
	}

	@Overwrite
	public int getBlockMetadataAt(int localX, int y, int localZ) {
		return this.getBlockMetadata().get(localX, y, localZ);
	}

	@Overwrite
	public void setBlockMetadataAt(int localX, int y, int localZ, int metadata) {
		this.dirty = true;
		this.getBlockMetadata().set(localX, y, localZ, metadata);
	}

	@Overwrite
	public int getLightAt(LightType type, int localX, int y, int localZ) {
		return type == LightType.SKY ? this.getSkyLight().get(localX, y, localZ) : (type == LightType.BLOCK ? this.getBlockLight().get(localX, y, localZ) : 0);
	}

	@Overwrite
	public void setLightAt(LightType type, int localX, int y, int localZ, int light) {
		this.dirty = true;
		if(type == LightType.SKY) {
			this.getSkyLight().set(localX, y, localZ, light);
		} else {
			if(type != LightType.BLOCK) {
				return;
			}

			this.getBlockLight().set(localX, y, localZ, light);
		}

	}

	@Overwrite
	public int getActualLightAt(int localX, int y, int localZ, int ambientDarkness) {
		int i5 = this.getSkyLight().get(localX, y, localZ);
		if(i5 > 0) {
			hasSkyLight = true;
		}

		i5 -= ambientDarkness;
		int i6 = this.getBlockLight().get(localX, y, localZ);
		if(i6 > i5) {
			i5 = i6;
		}

		return i5;
	}
}
