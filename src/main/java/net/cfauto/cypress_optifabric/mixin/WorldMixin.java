package net.cfauto.cypress_optifabric.mixin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import ext.client.InputHandler;
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
	public volatile List blocklightingToUpdate = new ArrayList();

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

	@Unique
	public synchronized void doLightUpdatesExt() {
		while(!this.lightUpdates.isEmpty()) {
			LightUpdate lightUpdate21 = (LightUpdate)this.lightUpdates.remove(this.lightUpdates.size() - 1);
			if(lightUpdate21 != null) {
				lightUpdate21.run((World)(Object)this);
			}
		}

		while(!this.blocklightingToUpdate.isEmpty()) {
			LightUpdate lightUpdate21 = (LightUpdate)this.lightUpdates.remove(this.lightUpdates.size() - 1);
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

	@WrapMethod(method = "updateLight(Lnet/minecraft/world/LightType;IIIIIIZ)V")
	private void asyncPatch(LightType type, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, boolean expand, Operation<Void> original) {
		if (!InputHandler.minecraft.options.getAsyncGen()) {
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
						if(metadataChunkBlock14.type == type && metadataChunkBlock14.expand(minX, minY, minZ, maxX, maxY, maxZ)) {
							return;
						}
					}
				}

				this.lightUpdates.add(new LightUpdate(type, minX, minY, minZ, maxX, maxY, maxZ));
			}
		} else {
			original.call(type, minX, minY, minZ, maxX, maxY, maxZ, expand);
		}
	}
}
