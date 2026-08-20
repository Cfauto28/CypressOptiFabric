package net.cfauto.cypress_optifabric.mixin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.cfauto.cypress_optifabric.impl.IWorldProvider;
import net.cfauto.cypress_optifabric.region.McRegionChunkLoader;
import net.minecraft.world.LightType;
import net.minecraft.world.LightUpdate;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkCache;
import net.minecraft.world.chunk.ChunkSource;
import net.minecraft.world.chunk.storage.AlphaChunkStorage;
import net.minecraft.world.gen.chunk.OverworldChunkGenerator;

@Mixin(World.class)
public class WorldMixin implements IWorldProvider {
	@Shadow
	public long seed;
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
				return new ChunkCache((World)(Object)this, new McRegionChunkLoader(dir, alphaStorage), new OverworldChunkGenerator((World)(Object)this, this.seed));
			}
		}
		return new ChunkCache((World)(Object)this, alphaStorage, new OverworldChunkGenerator((World)(Object)this, this.seed));
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
			lightUpdate21 = (LightUpdate)this.lightUpdates.remove(this.lightUpdates.size() - 1);
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
	public boolean isChunkLoaded(int x, int y, int z) {
		return (Boolean) null;
	}
	
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
