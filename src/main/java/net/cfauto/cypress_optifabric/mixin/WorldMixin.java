package net.cfauto.cypress_optifabric.mixin;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import ext.block.ExtBlock;
import ext.client.InputHandler;
import net.cfauto.cypress_optifabric.impl.IWorldProvider;
import net.cfauto.cypress_optifabric.region.McRegionChunkLoader;
import net.cfauto.cypress_optifabric.ext.ChunkCacheExt;
import net.cfauto.cypress_optifabric.ext.WorldChunkExt;
import net.minecraft.entity.mob.player.PlayerEntity;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LightType;
import net.minecraft.world.LightUpdate;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkCache;
import net.minecraft.world.chunk.ChunkSource;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.storage.AlphaChunkStorage;
import net.minecraft.world.gen.chunk.OverworldChunkGenerator;

@Mixin(World.class)
public abstract class WorldMixin implements IWorldProvider {
	
	@Shadow
	public List players;

	@Shadow
	private Set tickingChunks;

	@Shadow
	public Random random;

	@Shadow
	public boolean snowCovered;

	@Shadow
	public boolean sandCovered;

	@Shadow
	private int ambientSoundCooldown;

	@Shadow
	protected int randomTickLCG;

	@Shadow
	protected int randomTickLCGIncrement;

	@Shadow
	public long seed;

	@Shadow
	public abstract WorldChunk getChunkAt(int chunkX, int chunkZ);

	@Shadow
	public abstract boolean setBlock(int x, int y, int z, int block);

	@Shadow
	public abstract int getSurfaceHeight(int x, int z);

	@Shadow
	public abstract int getRawBrightness(int x, int y, int z);

	@Shadow
	public abstract int getLight(LightType type, int x, int y, int z);

	@Shadow
	public abstract PlayerEntity getNearestPlayer(double x, double y, double z, double range);

	@Shadow
	public abstract void playSound(double x, double y, double z, String sound, float volume, float pitch);
	
	@Shadow
	public List lightUpdates;
	@Unique
	public volatile List blocklightingToUpdate;

	@Inject(method = "<init>(Ljava/lang/String;)V", at = @At(value = "FIELD", target = "Lnet/minecraft/world/World;lightUpdates:Ljava/util/List;"))	
	public void initWorld(String name, CallbackInfo ci) {
		this.blocklightingToUpdate = new ArrayList();
	}

	@Inject(method = "<init>(Ljava/io/File;Ljava/lang/String;J)V", at = @At(value = "FIELD", target = "Lnet/minecraft/world/World;lightUpdates:Ljava/util/List;"))	
	public void init2World(File file1, String string2, long j3, CallbackInfo ci) {
		this.blocklightingToUpdate = new ArrayList();
	}

	@Overwrite
	public void tickChunks() {
		this.tickingChunks.clear();

		int i1;
		int i2;
		int i3;
		int i4;
		for(int i5 = 0; i5 < this.players.size(); ++i5) {
			PlayerEntity playerEntity6 = (PlayerEntity)this.players.get(i5);
			i1 = MathHelper.floor(playerEntity6.x / 16.0D);
			i2 = MathHelper.floor(playerEntity6.z / 16.0D);
			byte b7 = 9;

			for(i3 = -b7; i3 <= b7; ++i3) {
				for(i4 = -b7; i4 <= b7; ++i4) {
					this.tickingChunks.add(new ChunkPos(i3 + i1, i4 + i2));
				}
			}
		}

		if(this.ambientSoundCooldown > 0) {
			--this.ambientSoundCooldown;
		}

		Iterator iterator12 = this.tickingChunks.iterator();

		while(iterator12.hasNext()) {
			ChunkPos chunkPos13 = (ChunkPos)iterator12.next();
			i1 = chunkPos13.x * 16;
			i2 = chunkPos13.z * 16;
			WorldChunkExt worldChunk14 = (WorldChunkExt) this.getChunkAt(chunkPos13.x, chunkPos13.z);
			int i8;
			int i9;
			int i10;
			if(this.ambientSoundCooldown == 0) {
				this.randomTickLCG = this.randomTickLCG * 3 + this.randomTickLCGIncrement;
				i3 = this.randomTickLCG >> 2;
				i4 = i3 & 15;
				i8 = i3 >> 8 & 15;
				i9 = i3 >> 16 & 127;
				i10 = worldChunk14.getBlockAt(i4, i9, i8);
				i4 += i1;
				i8 += i2;
				if(i10 == 0 && this.getRawBrightness(i4, i9, i8) <= this.random.nextInt(8) && this.getLight(LightType.SKY, i4, i9, i8) <= 0) {
					PlayerEntity playerEntity11 = this.getNearestPlayer((double)i4 + 0.5D, (double)i9 + 0.5D, (double)i8 + 0.5D, 8.0D);
					if(playerEntity11 != null && playerEntity11.squaredDistanceTo((double)i4 + 0.5D, (double)i9 + 0.5D, (double)i8 + 0.5D) > 4.0D) {
						this.playSound((double)i4 + 0.5D, (double)i9 + 0.5D, (double)i8 + 0.5D, "ambient.cave.cave", 0.7F, 0.8F + this.random.nextFloat() * 0.2F);
						this.ambientSoundCooldown = this.random.nextInt(12000) + 6000;
					}
				}
			}

			if(this.snowCovered && this.random.nextInt(4) == 0) {
				this.randomTickLCG = this.randomTickLCG * 3 + this.randomTickLCGIncrement;
				i3 = this.randomTickLCG >> 2;
				i4 = i3 & 15;
				i8 = i3 >> 8 & 15;
				i9 = this.getSurfaceHeight(i4 + i1, i8 + i2);
				if(i9 >= 0 && i9 < 128 && worldChunk14.getLightAt(LightType.BLOCK, i4, i9, i8) < 10) {
					i10 = worldChunk14.getBlockAt(i4, i9 - 1, i8);
					if(worldChunk14.getBlockAt(i4, i9, i8) == 0 && ExtBlock.SNOW_LAYER.canBePlaced((World)(Object)this, i4 + i1, i9, i8 + i2)) {
						this.setBlock(i4 + i1, i9, i8 + i2, ExtBlock.SNOW_LAYER.id);
					}

					if(i10 == ExtBlock.WATER.id && worldChunk14.getBlockMetadataAt(i4, i9 - 1, i8) == 0) {
						this.setBlock(i4 + i1, i9 - 1, i8 + i2, ExtBlock.ICE.id);
					}
				}
			}

			if(this.sandCovered && this.random.nextInt(4) == 1) {
				this.randomTickLCG = this.randomTickLCG * 3 + this.randomTickLCGIncrement;
				i3 = this.randomTickLCG >> 2;
				i4 = i3 & 15;
				i8 = i3 >> 8 & 15;
				i9 = this.getSurfaceHeight(i4 + i1, i8 + i2);
				if(i9 >= 0 && i9 < 128 && worldChunk14.getLightAt(LightType.BLOCK, i4, i9, i8) < 10) {
					i10 = worldChunk14.getBlockAt(i4, i9 - 1, i8);
					if(i10 == ExtBlock.WATER.id && worldChunk14.getBlockMetadataAt(i4, i9 - 1, i8) == 0 && this.random.nextInt(16) == 0) {
						this.setBlock(i4 + i1, i9 - 1, i8 + i2, ExtBlock.CLAY_BLOCK.id);
					}
				}
			}

			if(InputHandler.minecraft.raining) {
				this.randomTickLCG = this.randomTickLCG * 3 + this.randomTickLCGIncrement;
				i3 = this.randomTickLCG >> 2;
				i4 = i3 & 15;
				i8 = i3 >> 8 & 15;
				i9 = this.getSurfaceHeight(i4 + i1, i8 + i2);
				if(i9 >= 0 && i9 < 128 && worldChunk14.getLightAt(LightType.BLOCK, i4, i9, i8) < 8) {
					worldChunk14.getBlockAt(i4, i9 - 1, i8);
					if(worldChunk14.getBlockAt(i4, i9, i8) == 0 && i9 < 66) {
						this.setBlock(i4 + i1, i9, i8 + i2, ExtBlock.WATER.id);
					}
				}
			}

			for(i3 = 0; i3 < 80; ++i3) {
				this.randomTickLCG = this.randomTickLCG * 3 + this.randomTickLCGIncrement;
				i4 = this.randomTickLCG >> 2;
				i8 = i4 & 15;
				i9 = i4 >> 8 & 15;
				i10 = i4 >> 16 & 127;
				int i15 = worldChunk14.newblocks[i8 << 11 | i9 << 7 | i10];
				if(ExtBlock.TICKS_RANDOMLY[i15]) {
					ExtBlock.BY_ID[i15].tick((World)(Object)this, i8 + i1, i10, i9 + i2, this.random);
				}
			}
		}

	}

	/**
	 * @author FMG793
	 * @reason McRegion Impl
	 */
	@Overwrite
	public ChunkSource createChunkCache(File dir) {
		AlphaChunkStorage alphaStorage = new AlphaChunkStorage(dir, true);

		String[] string2 = dir.list();
		int i4 = string2.length;

		for(int i5 = 0; i5 < i4; ++i5) {
			String string6 = string2[i5];
			if(string6.equals("region") || this.getRegion()) {
				return new ChunkCacheExt((World)(Object)this, new McRegionChunkLoader(dir, alphaStorage), new OverworldChunkGenerator((World)(Object)this, this.seed));
			}
		}
		return new ChunkCacheExt((World)(Object)this, alphaStorage, new OverworldChunkGenerator((World)(Object)this, this.seed));
	}
	
	public boolean getRegion() {
		return false;
	}
	
	@Overwrite
	public boolean doLightUpdates() {
		return false;
	}

	@Unique
	public synchronized void doLightUpdatesExt() {
		LightUpdate lightUpdate21;
		while(!this.lightUpdates.isEmpty()) {
			lightUpdate21 = (LightUpdate)this.lightUpdates.remove(this.lightUpdates.size() - 1);
			if(lightUpdate21 != null) {
				lightUpdate21.run((World)(Object)this);
			}
		}

		while(!this.blocklightingToUpdate.isEmpty()) {
			lightUpdate21 = (LightUpdate)this.blocklightingToUpdate.remove(this.blocklightingToUpdate.size() - 1);
			if(lightUpdate21 != null) {
				lightUpdate21.run((World)(Object)this);
			}
		}
	}

	@Unique
	public List getBlocklightingToUpdate() {
		return blocklightingToUpdate;
	}

	@Shadow
	public abstract boolean isChunkLoaded(int x, int y, int z);
	
	@Overwrite
	public void updateLight(LightType type, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, boolean expand) {
		int i9 = (maxX + minX) / 2;
		int i10 = (maxZ + minZ) / 2;
		if(this.isChunkLoaded(i9, 64, i10)) {
			int i11 = (type == LightType.SKY ? this.lightUpdates : this.blocklightingToUpdate).size();
			if(expand) {
				int i12 = 4;
				if(i12 > i11) {
					i12 = i11;
				}

				for(int i13 = 0; i13 < i12; ++i13) {
					LightUpdate metadataChunkBlock14 = (LightUpdate)(type == LightType.SKY ? this.lightUpdates : this.blocklightingToUpdate).get((type == LightType.SKY ? this.lightUpdates : this.blocklightingToUpdate).size() - i13 - 1);
					if(metadataChunkBlock14 != null && metadataChunkBlock14.type == type && metadataChunkBlock14.expand(minX, minY, minZ, maxX, maxY, maxZ)) {
						return;
					}
				}
			}

			this.lightUpdates.add(new LightUpdate(type, minX, minY, minZ, maxX, maxY, maxZ));
		}
	}
}
